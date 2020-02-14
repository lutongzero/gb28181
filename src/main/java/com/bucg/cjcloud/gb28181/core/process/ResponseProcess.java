package com.bucg.cjcloud.gb28181.core.process;

import javax.sip.message.Response;
import com.bucg.cjcloud.gb28181.core.ResponseEntity;

public interface ResponseProcess {
  
  ResponseEntity processResponse(Response resp);

  boolean isSupport(Response resp);
}
