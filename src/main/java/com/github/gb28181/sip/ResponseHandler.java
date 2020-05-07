package com.github.gb28181.sip;

import javax.sip.InvalidArgumentException;
import javax.sip.ResponseEvent;
import javax.sip.SipException;

public interface ResponseHandler {

  void handlerResponse(ResponseEvent evt)throws InvalidArgumentException, SipException;

  boolean isSupport(ResponseEvent evt);
}
