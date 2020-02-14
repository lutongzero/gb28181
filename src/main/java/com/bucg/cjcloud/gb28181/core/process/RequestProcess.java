package com.bucg.cjcloud.gb28181.core.process;

import javax.sip.message.Request;

import com.bucg.cjcloud.gb28181.core.ResponseEntity;

public interface RequestProcess {
	
	ResponseEntity processRequest(Request req);
	
	
	boolean isSupport(Request req);
}
