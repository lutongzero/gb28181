package com.github.gb28181.gb;

import javax.sip.InvalidArgumentException;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.github.gb28181.sip.ResponseHandler;

/**
 * 
 * */
@Order(100)
@Component
public class OkResponseHandler implements ResponseHandler {

  @Override
  public void handlerResponse(ResponseEvent evt) throws InvalidArgumentException, SipException {
    
  }

  @Override
  public boolean isSupport(ResponseEvent evt) {

    return true;
  }
  
}
