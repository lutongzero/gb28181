package com.github.gb28181.sip;

import javax.sip.message.Request;

public interface RequestHandler {

	ResponseEntity processRequest(Request req);

	boolean isSupport(Request req);
}
