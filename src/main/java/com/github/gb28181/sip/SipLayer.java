package com.github.gb28181.sip;

import java.util.Map;
import java.util.TooManyListenersException;
import javax.annotation.Resource;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.message.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.github.gb28181.Constants;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SipLayer implements SipListener {
  @Resource(name = "delegatingResptHandler")
  private ResponseHandler respHandler;
  @Resource(name = "delegatingRequestHandler")
  private RequestHandler requestHandler;

  @Autowired
  public SipLayer(Map<String, SipProvider> sipProviderMap) throws TooManyListenersException {
    sipProviderMap.get(Constants.TCP_SIP_PROVIDER).addSipListener(this);
    sipProviderMap.get(Constants.UDP_SIP_PROVIDER).addSipListener(this);
  }


  @Override
  public void processResponse(ResponseEvent evt) {
    if (respHandler.isSupport(evt)) {
      try {
        respHandler.handlerResponse(evt);
      } catch (Exception e) {
        log.error("processResponse error!", e);
      }
    }
  }

  @Override
  public void processRequest(RequestEvent evt) {
    Request request = evt.getRequest();
    if (requestHandler.isSupport(request)) {
      try {
        requestHandler.processRequest(evt);
      } catch (Exception e) {
        log.error("processRequest error!", e);

      }
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

  public RequestHandler getrequestHandler() {
    return requestHandler;
  }

  public void setrequestHandler(RequestHandler requestHandler) {
    this.requestHandler = requestHandler;
  }
}
