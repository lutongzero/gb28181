package com.bucg.cjcloud.gb28181.core;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.sdp.Connection;
import javax.sdp.SdpException;
import javax.sdp.SessionDescription;
import javax.sdp.Version;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.SubjectHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bucg.cjcloud.gb28181.core.entity.Device;
import com.bucg.cjcloud.gb28181.core.support.SNManager;
import com.bucg.cjcloud.gb28181.core.xmlbean.CmdTypeEnmu;
import com.bucg.cjcloud.gb28181.core.xmlbean.CommonReq;
import com.thoughtworks.xstream.XStream;
import gov.nist.javax.sdp.MediaDescriptionImpl;
import gov.nist.javax.sdp.SessionDescriptionImpl;
import gov.nist.javax.sdp.TimeDescriptionImpl;
import gov.nist.javax.sdp.fields.AttributeField;
import gov.nist.javax.sdp.fields.ConnectionField;
import gov.nist.javax.sdp.fields.MediaField;
import gov.nist.javax.sdp.fields.OriginField;
import gov.nist.javax.sdp.fields.ProtoVersionField;
import gov.nist.javax.sdp.fields.RepeatField;
import gov.nist.javax.sdp.fields.SessionNameField;
import gov.nist.javax.sdp.fields.TimeField;

@Service
public class RequestMessageService {
	@Autowired
	private XStream xstream;
	@Autowired
	private SNManager snManager;
	@Autowired
	private MessageHandel messageHandel;
	@Autowired
	private SipServerInfo sipinfo;
	@Autowired
    private HeaderFactory headerFactory;

	 public void getCatalog(Device device) throws ParseException, InvalidArgumentException, SipException {
		 CommonReq req = new CommonReq(CmdTypeEnmu.Catalog, snManager.getSN(), device.getDeviceId());
		 String message = xstream.toXML(req);
		 messageHandel.sendMessage(device.getAddress(), device.getDeviceId(), message);
		 
	 }
	 
	 
	 public void  play(Device device,String channelId) throws SdpException, ParseException, InvalidArgumentException, SipException {
	   SessionDescription sessionDescription = createSessionDescription();
	   SubjectHeader header = headerFactory.createSubjectHeader(device.getDeviceId()+":001,"+sipinfo.getDomain()+":001");
	   List<Header> list = new ArrayList<>();
	   list.add(header);
	   messageHandel.sendMesdsageINVITE(device.getAddress(), device.getDeviceId(), sessionDescription, list); 
	 }
	 
	 private SessionDescription createSessionDescription() throws SdpException {
	   SessionDescriptionImpl session=new SessionDescriptionImpl();
       Version version = new ProtoVersionField();
       version.setVersion(0);
       session.setVersion(version);
       
       OriginField origin= new OriginField();
       origin.setAddress(sipinfo.getIp());
       origin.setUsername(sipinfo.getUsername());
       origin.setSessionId(0);
       origin.setSessionVersion(0);
       origin.setAddressType("IP4");
       origin.setNettype("IN");
       session.setOrigin(origin);
       
       SessionNameField nameField = new SessionNameField();
       nameField.setSessionName("Play");
       session.setSessionName(nameField);
       
       Connection connect = new ConnectionField();
       connect.setAddress(sipinfo.getIp());
      // connect.setAddress("192.168.3.88");
       connect.setAddressType("IP4");
       connect.setNetworkType("IN");
       session.setConnection(connect);
       TimeDescriptionImpl impl = new TimeDescriptionImpl();
       Vector<String> RepeatTime = new Vector<>();
         RepeatField field = new RepeatField();
      
      // impl.setRepeatTimes(RepeatTime);
       TimeField timeField = new TimeField();
       timeField.setStartTime(0);
       timeField.setStopTime(0);
       impl.setTime(timeField);
       Vector<Object> vector = new Vector<>();
       vector.add(impl);
       session.setTimeDescriptions(vector);
       
       MediaDescriptionImpl mediaDescriptionImpl = new MediaDescriptionImpl();
     MediaField mediaField = new MediaField();
     mediaField.setMediaType("video");
     mediaField.setPort(1935);
     mediaField.setProtocol("RTP/AVP");
     Vector<Object> mediaFormat = new Vector<>();
     mediaFormat.add("96");
     mediaFormat.add("97");
     mediaFormat.add("98");
     mediaField.setMediaFormats(mediaFormat);
       mediaDescriptionImpl.setMedia(mediaField);
       Vector<Object> vector2 = new Vector<>();
       vector2.add(mediaDescriptionImpl);
       session.setMediaDescriptions(vector2);
       Vector<AttributeField> v3 = new Vector<>();
       AttributeField a1 = new AttributeField();
       a1.setValue("recvonly");;
       v3.add(a1);
       AttributeField a2 = new AttributeField();
       a2.setValue("rtpmap:96 PS/90000");;
       v3.add(a2);
       AttributeField a3 = new AttributeField();
       a3.setValue("rtpmap:98 H264/90000");;
       v3.add(a3);
       AttributeField a4 = new AttributeField();
       a4.setValue("rtpmap:97 MPEG4/90000");;
       v3.add(a4);
       mediaDescriptionImpl.setAttributes(v3);
      return session;
	   
	 }
}
