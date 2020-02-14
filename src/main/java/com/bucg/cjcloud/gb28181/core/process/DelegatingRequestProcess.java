package com.bucg.cjcloud.gb28181.core.process;

import java.util.List;

import javax.sip.message.Request;

import com.bucg.cjcloud.gb28181.core.ResponseEntity;

public class DelegatingRequestProcess implements RequestProcess {
	 private List<RequestProcess> requestProcessList;
	@Override
	public ResponseEntity processRequest(Request req) {
		for(RequestProcess process:requestProcessList) {
			if(process.isSupport(req)) {
				return process.processRequest(req);
			}
		}
		throw new RuntimeException();
	}

	@Override
	public boolean isSupport(Request req) {
		return true;
	}

	public DelegatingRequestProcess(List<RequestProcess> requestProcessList) {
		super();
		this.requestProcessList = requestProcessList;
	}
	
}
