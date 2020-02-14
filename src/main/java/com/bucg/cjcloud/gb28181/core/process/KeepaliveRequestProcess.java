package com.bucg.cjcloud.gb28181.core.process;

import javax.sip.message.Request;

import com.bucg.cjcloud.gb28181.core.ResponseEntity;

public class KeepaliveRequestProcess implements RequestProcess{
	
	@Override
	public ResponseEntity processRequest(Request req) {
		return ResponseEntity.builder().statusCode(200).build();
	}
	
	@Override
	public boolean isSupport(Request req) {
		String method = req.getMethod();
		String content = String.valueOf(req.getContent());
		return Request.MESSAGE.equals(method)&&content!=null&&content.contains("CmdType")&&content.contains("Keepalive");
	}
	
	
}
