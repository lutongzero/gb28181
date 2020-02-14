package com.bucg.cjcloud.gb28181.core.handler;

import com.bucg.cjcloud.gb28181.core.xmlbean.EventResp;

import gov.nist.javax.sip.message.SIPRequest;

/**
 * 事件的业务处理
 * */
public interface EventHandler {
	
	public void handler (EventResp keepalive,SIPRequest request);
}
