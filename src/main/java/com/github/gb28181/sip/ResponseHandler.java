package com.github.gb28181.sip;

import javax.sip.ResponseEvent;

public interface ResponseHandler {

    void handlerResponse(ResponseEvent evt) throws Exception;

    boolean isSupport(ResponseEvent evt);
}
