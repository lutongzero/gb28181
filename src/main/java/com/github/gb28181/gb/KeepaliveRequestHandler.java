package com.github.gb28181.gb;

import java.nio.charset.Charset;

import javax.sip.RequestEvent;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.github.gb28181.entity.GbDevice;
import com.github.gb28181.support.xmlbean.KeepaliveResp;
import com.thoughtworks.xstream.XStream;

/**
 * 
 * */
@Order(4)
@Component
public class KeepaliveRequestHandler extends AbstractCommonRequestHandler {
	@Autowired
	private CommonStoreService storeService;
	@Autowired
	private XStream xstream;

	@Override
	public boolean isSupport(Request req) {
		String method = req.getMethod();
		String content = req.getRawContent() == null ? null : new String(req.getRawContent());
		return Request.MESSAGE.equals(method) && content != null && content.contains("CmdType")
				&& content.contains("Keepalive");
	}

	@Override
	public Response service(RequestEvent evt) throws Exception {
		String string = new String(evt.getRequest().getRawContent(), Charset.forName("gbk"));
		xstream.toXML(new KeepaliveResp());
		KeepaliveResp keeplive = (KeepaliveResp) xstream.fromXML(string);
		GbDevice gbDevice = storeService.getGbDevice(keeplive.getDeviceID());
		if (gbDevice == null) {
			return messageFactory.createResponse(Response.UNAUTHORIZED, evt.getRequest());

		}
		return messageFactory.createResponse(Response.OK, evt.getRequest());
	}

}
