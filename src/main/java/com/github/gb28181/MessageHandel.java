package com.github.gb28181;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.gb28181.support.BranchManager;

@Service
public class MessageHandel {
	@Autowired
	private SipServerInfo sipinfo;
	@Autowired
	private AddressFactory addressFactory;
	@Autowired
	private MessageFactory messageFactory;
	@Autowired
	private HeaderFactory headerFactory;
	@Resource(name = "fromAddress")
	private Address address;
	@Resource(name = "contactHeader")
	private ContactHeader contactHeader;
	@Autowired
	private SipProvider sipProvider;
	@Autowired
	private BranchManager branchManager;

	/***
	 * 获取新的from,new tag
	 * 
	 * @throws ParseException
	 */
	public FromHeader getFromHeader() throws ParseException {
		return headerFactory.createFromHeader(address, RandomStringUtils.randomNumeric(5));

	}

	/**
	 * 获取 ToHeader
	 * 
	 * @throws ParseException
	 */
	public ToHeader getToHeader(String username, String address) throws ParseException {
		SipURI toAddress = addressFactory.createSipURI(username, address);
		Address toNameAddress = addressFactory.createAddress(toAddress);
		toNameAddress.setDisplayName(username);
		return headerFactory.createToHeader(toNameAddress, null);
	}

	public void sendMessage(String toAddress, String toUsername, String message)
			throws ParseException, InvalidArgumentException, SipException {
		CallIdHeader callIdHeader = sipProvider.getNewCallId();
		ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		ViaHeader viaHeader = headerFactory.createViaHeader(sipinfo.getIp(), sipinfo.getPort(), "udp", "branch1");
		viaHeaders.add(viaHeader);
		ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("Application", "MANSCDP+xml");
		sendMessage(toAddress, toUsername, message, Request.MESSAGE, callIdHeader, viaHeaders, contentTypeHeader,
				new ArrayList<>(0));
	}

	public void sendMesdsageINVITE(String toAddress, String toUsername, Object message, List<Header> headers)
			throws ParseException, InvalidArgumentException, SipException {
		CallIdHeader callIdHeader = sipProvider.getNewCallId();
		ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		ViaHeader viaHeader = headerFactory.createViaHeader(sipinfo.getIp(), sipinfo.getPort(), "udp", "live");
		viaHeaders.add(viaHeader);
		ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("Application", "SDP");
		ToHeader toHeader = getToHeader(toUsername, toAddress);

		SipURI requestURI = addressFactory.createSipURI(toUsername, toAddress);
		requestURI.setTransportParam("udp");
		MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);
		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L, Request.INVITE);
		Request request = messageFactory.createRequest(requestURI, Request.INVITE, callIdHeader, cSeqHeader,
				getFromHeader(), toHeader, viaHeaders, maxForwards);
		request.addHeader(contactHeader);
		for (Header header : headers) {
			request.setHeader(header);
		}

		if (message != null) {
			request.setContent(message, contentTypeHeader);

		}
		sipProvider.sendRequest(request);
	}
	/*
	 * sendMessage(toAddress, toUsername, message, Request.INVITE, callIdHeader,
	 * viaHeaders, contentTypeHeader, headers);
	 */
	// branchManager.put(viaHeader.getBranch(),"INVITE");

	public void sendMessage(String toAddress, String toUsername, Object message, String method,
			CallIdHeader callIdHeader, List<ViaHeader> viaHeaders, ContentTypeHeader contentTypeHeader,
			List<Header> headers) throws ParseException, InvalidArgumentException, SipException {
		ToHeader toHeader = getToHeader(toUsername, toAddress);

		SipURI requestURI = addressFactory.createSipURI(toUsername, toAddress);
		requestURI.setTransportParam("udp");
		MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);
		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L, method);
		Request request = messageFactory.createRequest(requestURI, method, callIdHeader, cSeqHeader, getFromHeader(),
				toHeader, viaHeaders, maxForwards);
		request.addHeader(contactHeader);
		for (Header header : headers) {
			request.setHeader(header);
		}

		if (message != null) {
			request.setContent(message, contentTypeHeader);

		}
		sipProvider.sendRequest(request);
	}
}
