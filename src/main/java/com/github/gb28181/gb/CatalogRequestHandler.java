package com.github.gb28181.gb;

import java.nio.charset.Charset;

import javax.sip.RequestEvent;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.github.gb28181.support.xmlbean.CatalogResp;
import com.thoughtworks.xstream.XStream;

import lombok.extern.slf4j.Slf4j;

/**
 * uac 推送catalog后，信息存储到本地
 **/
@Slf4j
@Order(2)
@Component
public class CatalogRequestHandler extends AbstractCommonRequestHandler {
	@Autowired
	private XStream xstream;
	@Autowired
	private CommonStoreService storeService;

	@Override
	public boolean isSupport(Request req) {
		String method = req.getMethod();
		req.getContent();
		String content = null;
		byte[] bytes = req.getRawContent();
		if (bytes != null) {
			content = new String(bytes);
		}
		return Request.MESSAGE.equals(method) && content != null && content.contains("CmdType")
				&& content.contains("Catalog");
	}

	@Override
	public Response service(RequestEvent evt) throws Exception {
		String string = new String(evt.getRequest().getRawContent(), Charset.forName("gbk"));
		log.info("catalog update:{}", string);
		xstream.toXML(new CatalogResp());
		CatalogResp catalog = (CatalogResp) xstream.fromXML(string);
		storeService.saveCatalog(catalog);
		return messageFactory.createResponse(Response.OK, evt.getRequest());
	}

}
