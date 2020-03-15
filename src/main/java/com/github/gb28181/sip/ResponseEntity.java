package com.github.gb28181.sip;

import java.text.ParseException;
import java.util.List;

import javax.sip.InvalidArgumentException;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.Header;
import javax.sip.header.ToHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.commons.lang3.RandomStringUtils;

import gov.nist.javax.sip.header.ContentType;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class ResponseEntity {
  private int statusCode;
  private Request request;
  @Singular("addHeader")
  private List<Header> headers;
  private ContentTypeHeader contentTypeHeader;
  private Object content;

  public void execute(MessageFactory messageFactory, SipProvider sipProvider, Request request)
      throws ParseException, SipException, InvalidArgumentException {

    Response response = messageFactory.createResponse(statusCode, request);
    if (headers != null) {
      for (Header h : headers) {
        response.setHeader(h);
      }
    }
    ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
    toHeader.setTag(RandomStringUtils.randomNumeric(4));
    if (content != null) {
      if (contentTypeHeader == null) {
        contentTypeHeader = new ContentType();
      }
      response.setContent(content, contentTypeHeader);
    }

    ServerTransaction st = sipProvider.getNewServerTransaction(request);
    st.sendResponse(response);



  }
}
