package com.bucg.cjcloud.gb28181.core.handler;

import com.bucg.cjcloud.gb28181.core.xmlbean.KeepaliveResp;

import gov.nist.javax.sip.message.SIPRequest;
/**
 * 设备注册成功后，维持心跳
 * */
public interface KeepaliveHandler {
	
	
	public void handler (KeepaliveResp keepalive,SIPRequest request);
}
