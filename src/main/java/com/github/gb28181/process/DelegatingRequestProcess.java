package com.github.gb28181.process;

import java.util.List;

import javax.sip.message.Request;

import com.github.gb28181.sip.RequestHandler;
import com.github.gb28181.sip.ResponseEntity;

public class DelegatingRequestProcess implements RequestHandler {
	 private List<RequestHandler> requestProcessList;
	@Override
	public ResponseEntity processRequest(Request req) {
		for(RequestHandler process:requestProcessList) {
			if(process.isSupport(req)) {
				return process.processRequest(req);
			}
		}
		throw new RuntimeException("no support request Process find");
	}

	@Override
	public boolean isSupport(Request req) {
		return true;
	}

	public DelegatingRequestProcess(List<RequestHandler> requestProcessList) {
		super();
		this.requestProcessList = requestProcessList;
	}
	
}
