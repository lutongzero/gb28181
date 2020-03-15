package com.github.gb28181.sip;

import java.util.TooManyListenersException;

import javax.sip.Dialog;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SipLayer implements SipListener {

	private RequestHandler requestHandler;
	private SipProvider sipProvider;
	@Autowired
	private MessageFactory messageFactory;

	@Autowired
	public SipLayer(RequestHandler requestHandler, SipProvider sipProvider) throws TooManyListenersException {
		this.requestHandler = requestHandler;
		sipProvider.addSipListener(this);
		this.sipProvider = sipProvider;
	}

	/** This method is called by the SIP stack when a response arrives. */
	@Override
	public void processResponse(ResponseEvent evt) {
		SIPResponse response = (SIPResponse) (evt.getResponse());
		int status = response.getStatusCode();

		if ((status >= 200) && (status < 300)) { // Success!
			if (evt.getResponse().getHeader("Via").toString().indexOf("branch=live") != -1) {
				Dialog dialog = evt.getDialog();
				Request reqAck;
				try {
					reqAck = dialog.createAck(1L);
					dialog.sendAck(reqAck);
				} catch (InvalidArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SipException e) {
					e.printStackTrace();
				}

			}

		}
		return;
	}

	/**
	 * This method is called by the SIP stack when a new request arrives.
	 */
	@Override
	public void processRequest(RequestEvent evt) {
		Request request = evt.getRequest();
		if (requestHandler.isSupport(request)) {
			ResponseEntity processRequest = requestHandler.processRequest(request);
			try {
				processRequest.execute(messageFactory, sipProvider, request);
			} catch (Exception e) {
				log.error("processRequest error!", e);

			}
		}
	}

	/**
	 * This method is called by the SIP stack when there's no answer to a message.
	 * Note that this is treated differently from an error message.
	 */
	@Override
	public void processTimeout(TimeoutEvent evt) {
		System.out.println("Previous message not sent: " + "timeout");
	}

	/**
	 * This method is called by the SIP stack when there's an asynchronous message
	 * transmission error.
	 */
	public void processIOException(IOExceptionEvent evt) {
		System.out.println("Previous message not sent: " + "I/O Exception");
	}

	/**
	 * This method is called by the SIP stack when a dialog (session) ends.
	 */
	public void processDialogTerminated(DialogTerminatedEvent evt) {
	}

	/**
	 * This method is called by the SIP stack when a transaction ends.
	 */

	public void processTransactionTerminated(TransactionTerminatedEvent evt) {
	}

	public RequestHandler getrequestHandler() {
		return requestHandler;
	}

	public void setrequestHandler(RequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}
}
