package com.github.gb28181.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.sdp.SdpException;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.SubjectHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.gb28181.config.Constants;
import com.github.gb28181.config.SipServerInfo;
import com.github.gb28181.entity.GbDevice;
import com.github.gb28181.entity.StreamInfo;
import com.github.gb28181.gb.CommonStoreService;
import com.github.gb28181.media.ZLmediaClient;
import com.github.gb28181.media.ZLmediaClient.OpenRtpResp;
import com.github.gb28181.support.SNManager;
import com.github.gb28181.support.SsrcManager;
import com.github.gb28181.support.xmlbean.CmdTypeEnmu;
import com.github.gb28181.support.xmlbean.CommonReq;
import com.thoughtworks.xstream.XStream;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * 由uac产生的请求的请求头必备如下： to,from,cseq,call-id,max-forword,via 所有的请求都是非 直接响应 ers
 **/
@Slf4j
@Service
public class RequestMessageService {
	@Autowired
	private XStream xstream;
	@Autowired
	private SNManager snManager;
	@Autowired
	private SipServerInfo sipinfo;
	@Autowired
	private HeaderFactory headerFactory;
	@Autowired
	private AddressFactory addressFactory;
	@Resource(name = "fromAddress")
	private Address address;
	@Resource(name = "contactHeader")
	private ContactHeader contactHeader;
	@Resource(name = "sipProviderMap")
	private Map<String, SipProvider> sipProviderMap;
	@Autowired
	private MessageFactory messageFactory;
	@Autowired
	private SsrcManager ssrcManager;
	@Autowired
	private CommonStoreService storeService;
	@Autowired
	private ZLmediaClient mediaClient;
	@Value("${media.secret}")
	private String mediaSecrt;

	public void sendCommonMessage(GbDevice device, CmdTypeEnmu cmdtype)
			throws ParseException, InvalidArgumentException, SipException {
		CommonReq req = new CommonReq(cmdtype, snManager.getSN(), device.getDeviceId());
		Request request = createRequest(device, Request.MESSAGE, device.getDeviceId());
		request.setContent(xstream.toXML(req), headerFactory.createContentTypeHeader("Application", "MANSCDP+xml"));
		sendRequest(request, device.getTransport());
	}

	/**
	 * 
	 * @return streamInfo
	 */
	public StreamInfo sendInviate(GbDevice device, String channelId)
			throws SdpException, ParseException, InvalidArgumentException, SipException {
		Address concatAddress = addressFactory.createAddress(addressFactory.createSipURI(sipinfo.getId(),
				sipinfo.getIp().concat(":").concat(String.valueOf(sipinfo.getPort()))));
		Request request = createRequest(device, Request.INVITE, channelId);
		request.addHeader(headerFactory.createContactHeader(concatAddress));
		ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("Application", "SDP");
		String ssrc = ssrcManager.getSsrc(true);
		StreamInfo streamInfo = new StreamInfo();
		streamInfo.setChannelId(channelId);
		streamInfo.setCreateDate(new Date());
		streamInfo.setTransport("TCP");
		streamInfo.setSsrc(Integer.toHexString(Integer.parseInt(ssrc)));
		streamInfo.setDeviceId(device.getDeviceId());
		
		 OpenRtpResp openRtpServer = mediaClient.openRtpServer(mediaSecrt, 0, 1, channelId);
		 
		String content = createPlaySessionDescription(channelId, sipinfo.getIp(), sipinfo.getMeidaIp(),
				openRtpServer.getPort(), true, false, "Play", ssrc);
		request.setContent(content, contentTypeHeader);
		// Subject
		SubjectHeader subjectHeader = headerFactory
				.createSubjectHeader(String.format("%s:%s,%s:%s", channelId, ssrc, sipinfo.getId(), 0));
		request.addHeader(subjectHeader);
		sendRequest(request, device.getTransport());

		CallIdHeader callIdHeader = (CallIdHeader) request.getHeader(CallIdHeader.NAME);
		streamInfo.setCallId(callIdHeader.getCallId());
		storeService.saveStreamInfo(streamInfo);
		
		return streamInfo;
	}

	public void sendBye(String callId) throws SipException {
		StreamInfo streamInfo = storeService.getStreamInfo(callId);
		Dialog dialog = streamInfo.getDialog();
		if (dialog != null) {
			Request request = dialog.createRequest(Request.BYE);
			ClientTransaction clientTransaction = getSipProvider(request).getNewClientTransaction(request);
			dialog.sendRequest(clientTransaction);
			log.info("sendRequest >>> {}", request);
			storeService.removeStreamInfo(callId);
		}

	}

	private void sendRequest(Request request, String protocol) throws SipException {
		SipProvider sipProvider = getSipProvider(protocol);
		System.out.println("send :" + request);
		sipProvider.sendRequest(request);

	}

	public Request createRequest(GbDevice device, String method, String channelId)
			throws ParseException, SipException, InvalidArgumentException {
		SipURI sipuri = addressFactory.createSipURI(device.getDeviceId(), device.getAddress());
		ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		ViaHeader viaHeader = headerFactory.createViaHeader(sipinfo.getIp(), sipinfo.getPort(), device.getTransport(),
				null);
		viaHeaders.add(viaHeader);
		SipURI touri = addressFactory.createSipURI(channelId, device.getAddress());
		Address toAddress = addressFactory.createAddress(touri);
		ToHeader toheader = headerFactory.createToHeader(toAddress, null);

		SipProvider sipProvider = getSipProvider(device.getTransport());
		CallIdHeader callId = sipProvider.getNewCallId();
		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L, method);

		MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);

		Request request = messageFactory.createRequest(sipuri, method, callId, cSeqHeader, getFromHeader(), toheader,
				viaHeaders, maxForwardsHeader);
		return request;

	}

	private SipProvider getSipProvider(String protocol) {
		SipProvider sp = null;
		if (protocol.equals(Constants.TCP)) {
			sp = sipProviderMap.get(Constants.TCP_SIP_PROVIDER);
		} else {
			sp = sipProviderMap.get(Constants.UDP_SIP_PROVIDER);
		}
		return sp;
	}

	private SipProvider getSipProvider(Request request) {
		ViaHeader header = (ViaHeader) request.getHeader(ViaHeader.NAME);
		return getSipProvider(header.getProtocol());

	}

	/**
	 * SDP 创建
	 * 
	 * @param sessionId   源id 本机sip编码
	 * @param oip         信令服务器ip
	 * @param mIp         媒体服务器ip
	 * @param mPort       媒体服务器端口
	 * @param isTcp       tcp?udp
	 * @param sessionName play
	 * @param ssrc        序列号
	 * 
	 * 
	 */
	private static String createPlaySessionDescription(String sessionId, String oip, String mIp, int mPort,
			boolean isTcp, boolean isActive, String sessionName, String ssrc) {
		StringBuffer content = new StringBuffer(200);
		content.append("v=0\r\n");
		content.append("o=" + sessionId + " 0 0 IN IP4 " + oip + "\r\n");
		content.append("s=" + sessionName + "\r\n");
		content.append("c=IN IP4 " + mIp + "\r\n");
		content.append("t=0 0\r\n");
		content.append("m=video " + mPort + " " + (isTcp ? "TCP/" : "") + "RTP/AVP 96 98 97\r\n");
		content.append("a=recvonly\r\n");
		content.append("a=rtpmap:96 PS/90000\r\n");
		content.append("a=rtpmap:98 H264/90000\r\n");
		content.append("a=rtpmap:97 MPEG4/90000\r\n");

		if (isTcp) {
			content.append("a=setup:passive\r\n");
			// content.append("a=connection:new\r\n");
		}

		content.append("y=" + ssrc + "\r\n");
		return content.toString();
	}

	public FromHeader getFromHeader() throws ParseException {
		return headerFactory.createFromHeader(address, RandomStringUtils.randomNumeric(5));

	}
}
