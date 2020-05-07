package com.github.gb28181.gb;

import javax.sip.RequestEvent;
import javax.sip.message.Request;
import javax.sip.message.Response;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 最后处理
 */
@Order(50)
@Component
public class OkStatusRequestHandler extends AbstractCommonRequestHandler {

  @Override
  public boolean isSupport(Request req) {
    return true;
  }

  @Override
  public Response service(RequestEvent evt) throws Exception {

    return messageFactory.createResponse(Response.OK, evt.getRequest());
  }

}
