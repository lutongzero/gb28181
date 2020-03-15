package com.github.gb28181.process;

import javax.sip.message.Request;

import com.github.gb28181.sip.RequestHandler;
import com.github.gb28181.sip.ResponseEntity;

public class CatalogRequestProcess implements RequestHandler {

	@Override
	public ResponseEntity processRequest(Request req) {
		return ResponseEntity.builder().statusCode(200).build();
	}

	@Override
	public boolean isSupport(Request req) {
		String method = req.getMethod();
		String content = String.valueOf(req.getContent());
		return Request.MESSAGE.equals(method) && content != null && content.contains("CmdType")
				&& content.contains("Catalog");
	}

}
