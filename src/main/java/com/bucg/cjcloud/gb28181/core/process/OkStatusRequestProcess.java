package com.bucg.cjcloud.gb28181.core.process;

import javax.sip.message.Request;

import com.bucg.cjcloud.gb28181.core.ResponseEntity;
/**
 * 最后处理
 * */
public class OkStatusRequestProcess implements RequestProcess {

	@Override
	public ResponseEntity processRequest(Request req) {
		
		return ResponseEntity.builder().statusCode(200).build();
	}

	@Override
	public boolean isSupport(Request req) {
		
		return true;
	}
	
}
