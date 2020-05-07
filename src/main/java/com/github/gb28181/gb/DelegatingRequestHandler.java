package com.github.gb28181.gb;

import java.util.List;
import javax.sip.RequestEvent;
import javax.sip.message.Request;
import com.github.gb28181.sip.RequestHandler;

public class DelegatingRequestHandler implements RequestHandler {
  private List<RequestHandler> requestProcessList;

  @Override
  public void processRequest(RequestEvent evt) throws Exception {
    for (RequestHandler process : requestProcessList) {
      if (process.isSupport(evt.getRequest())) {
        process.processRequest(evt);
        return;
      }
    }
    throw new RuntimeException("no support request Process find");
  }

  @Override
  public boolean isSupport(Request req) {
    return true;
  }

  public DelegatingRequestHandler(List<RequestHandler> requestProcessList) {
    super();
    this.requestProcessList = requestProcessList;
  }

}
