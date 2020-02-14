package com.bucg.cjcloud.gb28181.core.process;

import java.util.ArrayList;
import java.util.List;

public class RequestProcessFactories {
	
	
	public static  RequestProcess createDelegatingRequestProcess() {
		List<RequestProcess> list =new ArrayList<>();
		list.add(new RegisterRequestProcess());
		list.add(new KeepaliveRequestProcess());
		list.add(new CatalogRequestProcess());
		list.add(new OkStatusRequestProcess());
		return new DelegatingRequestProcess(list);
		
	}
	
	private RequestProcessFactories(){
		
	}
}
