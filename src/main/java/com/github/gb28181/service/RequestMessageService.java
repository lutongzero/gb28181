package com.github.gb28181.service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.sdp.SdpException;
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
	public StreamInfo play(GbDevice device, String channelId)
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

		String content = createPlayContent(channelId, openRtpServer.getPort(), true, ssrc);
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

	public StreamInfo playback(GbDevice device, String channelId, LocalDateTime start, LocalDateTime end, String userId)
			throws Exception {
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

		OpenRtpResp openRtpServer = mediaClient.openRtpServer(mediaSecrt, 0, 1, userId + channelId);

		String content = createPlayBackContent(channelId, openRtpServer.getPort(), true, ssrc, start, end);
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

	public void closeStream(String app, String stream) {
		mediaClient.closeStreams(mediaSecrt, app, stream, 1);

	}

	private void sendRequest(Request request, String protocol) throws SipException {
		SipProvider sipProvider = getSipProvider(protocol);
		log.info("sip消息即将发送{}:", request);
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

	String createPlayContent(String channelId, int mPort, boolean isTcp, String ssrc) {
		StringBuffer content = new StringBuffer(200);
		content.append("v=0\r\n");
		content.append("o=" + channelId + " 0 0 IN IP4 " + sipinfo.getIp() + "\r\n");
		content.append("s=Play\r\n");
		content.append("c=IN IP4 " + sipinfo.getMeidaIp() + "\r\n");
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

	private String createPlayBackContent(String channelId, int mPort, boolean isTcp, String ssrc, LocalDateTime start,
			LocalDateTime end) {
		StringBuffer content = new StringBuffer(200);
		content.append("v=0\r\n");
		content.append("o=" + channelId + " 0 0 IN IP4 " + sipinfo.getIp() + "\r\n");
		content.append("s=Playback\r\n");
		content.append("u=" + channelId + ":0\r\n");
		content.append("c=IN IP4 " + sipinfo.getMeidaIp() + "\r\n");
		content.append("t=" + start.toEpochSecond(ZoneOffset.of("+8")) + " " + end.toEpochSecond(ZoneOffset.of("+8"))
				+ "\r\n");
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

	/**
	 * 云台指令码计算
	 *
	 * @param leftRight 镜头左移右移 0:停止 1:左移 2:右移
	 * @param upDown    镜头上移下移 0:停止 1:上移 2:下移
	 * @param inOut     镜头放大缩小 0:停止 1:缩小 2:放大
	 * @param moveSpeed 镜头移动速度 默认 0XFF (0-255)
	 * @param zoomSpeed 镜头缩放速度 默认 0X1 (0-255)
	 */
	private static String cmdString(int leftRight, int upDown, int inOut, int moveSpeed, int zoomSpeed) {
		int cmdCode = 0;
		if (leftRight == 2) {
			cmdCode |= 0x01; // 右移
		} else if (leftRight == 1) {
			cmdCode |= 0x02; // 左移
		}
		if (upDown == 2) {
			cmdCode |= 0x04; // 下移
		} else if (upDown == 1) {
			cmdCode |= 0x08; // 上移
		}
		if (inOut == 2) {
			cmdCode |= 0x10; // 放大
		} else if (inOut == 1) {
			cmdCode |= 0x20; // 缩小
		}
		StringBuilder builder = new StringBuilder("A50F01");
		String strTmp;
		strTmp = String.format("%02X", cmdCode);
		builder.append(strTmp, 0, 2);
		strTmp = String.format("%02X", moveSpeed);
		builder.append(strTmp, 0, 2);
		builder.append(strTmp, 0, 2);
		strTmp = String.format("%X", zoomSpeed);
		builder.append(strTmp, 0, 1).append("0");
		// 计算校验码
		int checkCode = (0XA5 + 0X0F + 0X01 + cmdCode + moveSpeed + moveSpeed + (zoomSpeed /* << 4 */ & 0XF0)) % 0X100;
		strTmp = String.format("%02X", checkCode);
		builder.append(strTmp, 0, 2);
		return builder.toString();
	}

	/**
	 * 云台控制，支持方向与缩放控制
	 * 
	 * @param device    控制设备
	 * @param channelId 预览通道
	 * @param leftRight 镜头左移右移 0:停止 1:左移 2:右移
	 * @param upDown    镜头上移下移 0:停止 1:上移 2:下移
	 * @param inOut     镜头放大缩小 0:停止 1:缩小 2:放大
	 * @param moveSpeed 镜头移动速度
	 * @param zoomSpeed 镜头缩放速度
	 * @throws Exception
	 * @throws SipException
	 * @throws ParseException
	 */
	public void ptzCmd(GbDevice device, String channelId, int leftRight, int upDown, int inOut, int moveSpeed,
			int zoomSpeed) throws ParseException, SipException, Exception {
		String cmdStr = cmdString(leftRight, upDown, inOut, moveSpeed, zoomSpeed);
		StringBuffer ptzXml = new StringBuffer(200);
		ptzXml.append("<?xml version=\"1.0\" ?>");
		ptzXml.append("<Control>");
		ptzXml.append("<CmdType>DeviceControl</CmdType>");
		ptzXml.append("<SN>" + snManager.getSN() + "</SN>");
		ptzXml.append("<DeviceID>" + channelId + "</DeviceID>");
		ptzXml.append("<PTZCmd>" + cmdStr + "</PTZCmd>");
		ptzXml.append("<Info>");
		ptzXml.append("</Info>");
		ptzXml.append("</Control>");

		Request request = createRequest(device, Request.MESSAGE, device.getDeviceId());
		request.setContent(ptzXml.toString(), headerFactory.createContentTypeHeader("Application", "MANSCDP+xml"));
		sendRequest(request, device.getTransport());

	}

	public void recordInfoQuery(GbDevice device, String channelId, LocalDateTime start, LocalDateTime end)
			throws Exception {

		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		StringBuffer recordInfoXml = new StringBuffer(200);
		recordInfoXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		recordInfoXml.append("<Query>");
		recordInfoXml.append("<CmdType>RecordInfo</CmdType>");
		recordInfoXml.append("<SN>" + (int) ((Math.random() * 9 + 1) * 100000) + "</SN>");
		recordInfoXml.append("<DeviceID>" + channelId + "</DeviceID>");
		recordInfoXml.append("<StartTime>" + start.format(df) + "</StartTime>");
		recordInfoXml.append("<EndTime>" + end.format(df) + "</EndTime>");
		recordInfoXml.append("<Secrecy>0</Secrecy>");
		// 大华NVR要求必须增加一个值为all的文本元素节点Type
		recordInfoXml.append("<Type>all</Type>");
		recordInfoXml.append("</Query>");

		Request request = createRequest(device, Request.MESSAGE, device.getDeviceId());
		request.setContent(recordInfoXml.toString(),
				headerFactory.createContentTypeHeader("Application", "MANSCDP+xml"));
		sendRequest(request, device.getTransport());
		return;
	}

	public static void main(String[] args) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		String format = LocalDateTime.now().format(df);
		System.out.println(format);
	}

}
