package com.github.gb28181.gb;

import javax.sip.RequestEvent;
import javax.sip.message.Request;
import javax.sip.message.Response;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 
 * */
@Order(4)
@Component
public class KeepaliveRequestHandler extends AbstractCommonRequestHandler {


  @Override
  public boolean isSupport(Request req) {
    String method = req.getMethod();
    String content = req.getRawContent()==null?null:new String(req.getRawContent());
    return Request.MESSAGE.equals(method) && content != null && content.contains("CmdType")
        && content.contains("Keepalive");
  }

  @Override
  public Response service(RequestEvent evt) throws Exception {

    return messageFactory.createResponse(Response.OK, evt.getRequest());
  }


}
