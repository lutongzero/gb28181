package com.bucg.cjcloud.gb28181.core.process;

import java.text.ParseException;

import javax.sip.message.Request;

import org.apache.commons.lang3.RandomStringUtils;

import com.bucg.cjcloud.gb28181.core.ResponseEntity;

import gov.nist.javax.sip.header.Authorization;
import gov.nist.javax.sip.header.WWWAuthenticate;
import gov.nist.javax.sip.message.SIPRequest;
/**
 * 
 * 
 * **/
public class RegisterRequestProcess implements RequestProcess {

	@Override
	public ResponseEntity processRequest(Request req) {
		SIPRequest sipReq =(SIPRequest) req;
		Authorization authorization = sipReq.getAuthorization();
    	//如果 authorization 为null，说明第一次REGISTER. 返回401
    	if(authorization==null) {
    		WWWAuthenticate wwwAuthenticate = new WWWAuthenticate();
    		try {
				wwwAuthenticate.setDomain("3402000000");
				wwwAuthenticate.setNonce(RandomStringUtils.random(10));
	    		wwwAuthenticate.setQop("auth");
			} catch (ParseException e) {
				e.printStackTrace();
			}
    		
    		return ResponseEntity.builder().request(req).addHeader(wwwAuthenticate).statusCode(401).build();
    	}
    	return ResponseEntity.builder().request(req).statusCode(200).build();
	}

	@Override
	public boolean isSupport(Request req) {
		return "REGISTER".equals(req.getMethod());
	}



}
