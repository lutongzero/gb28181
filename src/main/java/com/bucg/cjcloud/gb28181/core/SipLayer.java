package com.bucg.cjcloud.gb28181.core;

import java.text.ParseException;
import java.util.TooManyListenersException;

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
import javax.sip.message.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bucg.cjcloud.gb28181.core.process.RequestProcess;

@Component
public class SipLayer implements SipListener {

  private RequestProcess requestProcess;
  private SipProvider sipProvider;
  @Autowired
  private MessageFactory messageFactory;

  @Autowired
  public SipLayer(RequestProcess requestProcess, SipProvider sipProvider)
      throws TooManyListenersException {
    this.requestProcess = requestProcess;
    sipProvider.addSipListener(this);
    this.sipProvider = sipProvider;
  }


  /** This method is called by the SIP stack when a response arrives. */
  @Override
  public void processResponse(ResponseEvent evt) {
    Response response = evt.getResponse();
    int status = response.getStatusCode();

    if ((status >= 200) && (status < 300)) { // Success!
      System.out.println("send");
      return;
    }
    System.out.println("Previous message not sent: " + status);
  }

  /**
   * This method is called by the SIP stack when a new request arrives.
   */
  @Override
  public void processRequest(RequestEvent evt) {
    Request request = evt.getRequest();
    if (requestProcess.isSupport(request)) {
      ResponseEntity processRequest = requestProcess.processRequest(request);
      try {

        processRequest.execute(messageFactory, sipProvider, request);
      } catch (ParseException e) {
        e.printStackTrace();
      } catch (SipException e) {
        e.printStackTrace();
      } catch (InvalidArgumentException e) {
        e.printStackTrace();
      }
      return;
    }

  }


  /**
   * This method is called by the SIP stack when there's no answer to a message. Note that this is
   * treated differently from an error message.
   */
  @Override
  public void processTimeout(TimeoutEvent evt) {
    System.out.println("Previous message not sent: " + "timeout");
  }

  /**
   * This method is called by the SIP stack when there's an asynchronous message transmission error.
   */
  public void processIOException(IOExceptionEvent evt) {
    System.out.println("Previous message not sent: " + "I/O Exception");
  }

  /**
   * This method is called by the SIP stack when a dialog (session) ends.
   */
  public void processDialogTerminated(DialogTerminatedEvent evt) {}

  /**
   * This method is called by the SIP stack when a transaction ends.
   */

  public void processTransactionTerminated(TransactionTerminatedEvent evt) {}


  public RequestProcess getRequestProcess() {
    return requestProcess;
  }

  public void setRequestProcess(RequestProcess requestProcess) {
    this.requestProcess = requestProcess;
  }
}
