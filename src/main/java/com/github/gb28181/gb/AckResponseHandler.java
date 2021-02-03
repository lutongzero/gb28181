package com.github.gb28181.gb;

import java.text.ParseException;

import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.address.SipURI;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.github.gb28181.entity.GbDevice;
import com.github.gb28181.entity.StreamInfo;
import com.github.gb28181.sip.ResponseHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * */
@Slf4j
@Order(1)
@Component
public class AckResponseHandler implements ResponseHandler {
	@Autowired
	private CommonStoreService storeService;

	@Override
	public void handlerResponse(ResponseEvent evt) throws InvalidArgumentException, SipException, ParseException {
		log.info("ack sending");
		Dialog dialog = evt.getDialog();
		storeService.saveDialog(dialog.getCallId().getCallId(), dialog);
		Request reqAck = dialog.createAck(1l);
		SipURI requestURI = (SipURI) reqAck.getRequestURI();

		StreamInfo stream = getStream(evt);
		String deviceId = stream.getDeviceId();
		GbDevice device = storeService.getGbDevice(deviceId);
		requestURI.setHost(device.getReceived());
		requestURI.setPort(device.getRport());
		reqAck.setRequestURI(requestURI);

		dialog.sendAck(reqAck);
	}

	@Override
	public boolean isSupport(ResponseEvent evt) {
		return evt.getResponse().getStatusCode() == 200 && getStream(evt) != null;
	}

	private StreamInfo getStream(ResponseEvent evt) {
		CallIdHeader header = (CallIdHeader) evt.getResponse().getHeader(CallIdHeader.NAME);
		StreamInfo streamInfo = storeService.getStreamInfo(header.getCallId());
		return streamInfo;

	}
}
