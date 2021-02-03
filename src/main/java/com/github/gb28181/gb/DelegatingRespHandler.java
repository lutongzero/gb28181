package com.github.gb28181.gb;

import java.util.List;

import javax.sip.ResponseEvent;

import com.github.gb28181.sip.ResponseHandler;

public class DelegatingRespHandler implements ResponseHandler {
	private List<ResponseHandler> resqProcessList;

	@Override
	public void handlerResponse(ResponseEvent evt) throws Exception {
		for (ResponseHandler process : resqProcessList) {
			if (process.isSupport(evt)) {
				process.handlerResponse(evt);
				return;
			}
		}
		throw new RuntimeException("no support request Process find");
	}

	@Override
	public boolean isSupport(ResponseEvent evt) {
		return true;
	}

	public DelegatingRespHandler(List<ResponseHandler> resqProcessList) {
		super();
		this.resqProcessList = resqProcessList;
	}

}
