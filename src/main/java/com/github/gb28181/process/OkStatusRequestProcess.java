package com.github.gb28181.process;

import javax.sip.message.Request;

import com.github.gb28181.sip.RequestHandler;
import com.github.gb28181.sip.ResponseEntity;
/**
 * 最后处理
 * */
public class OkStatusRequestProcess implements RequestHandler {

	@Override
	public ResponseEntity processRequest(Request req) {
		
		return ResponseEntity.builder().statusCode(200).build();
	}

	@Override
	public boolean isSupport(Request req) {
		
		return true;
	}
	
}
