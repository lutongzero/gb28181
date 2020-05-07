package com.github.gb28181.sip;

import javax.sip.RequestEvent;
import javax.sip.message.Request;

public interface RequestHandler {

  void processRequest(RequestEvent evt) throws Exception;

  boolean isSupport(Request req);
}
