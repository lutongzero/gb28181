package com.github.gb28181.process;

import java.util.ArrayList;
import java.util.List;

import com.github.gb28181.gb.RegisterRequestHandler;
import com.github.gb28181.sip.RequestHandler;

public class RequestProcessFactories {

	public static RequestHandler createDelegatingRequestProcess() {
		List<RequestHandler> list = new ArrayList<>();
		list.add(new RegisterRequestHandler());
		list.add(new KeepaliveRequestProcess());
		list.add(new CatalogRequestProcess());
		list.add(new OkStatusRequestProcess());
		return new DelegatingRequestProcess(list);

	}

	private RequestProcessFactories() {

	}
}
