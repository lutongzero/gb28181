package com.github.gb28181.sip;

import javax.sip.message.Response;

public interface ResponseHandler {

	ResponseEntity processResponse(Response resp);

	boolean isSupport(Response resp);
}
