package com.github.gb28181.gb;

import java.util.Map;
import javax.annotation.Resource;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipProvider;
import javax.sip.TransactionAlreadyExistsException;
import javax.sip.TransactionUnavailableException;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.gb28181.Constants;
import com.github.gb28181.sip.RequestHandler;

/**
 * 根据protocol选择 sipProvider,发送响应
 */
public abstract class AbstractCommonRequestHandler implements RequestHandler {
  @Resource(name = "sipProviderMap")
  private Map<String, SipProvider> sipProviderMap;
  @Autowired
  protected MessageFactory messageFactory;

  @Override
  public void processRequest(RequestEvent evt) throws Exception {
    ViaHeader viaHeader = (ViaHeader) evt.getRequest().getHeader(ViaHeader.NAME);
    String protocol = viaHeader.getProtocol();
    SipProvider sipProvider = null;
    if (Constants.TCP.equalsIgnoreCase(protocol)) {
      sipProvider = sipProviderMap.get(Constants.TCP_SIP_PROVIDER);
    } else {
      sipProvider = sipProviderMap.get(Constants.UDP_SIP_PROVIDER);
    }

    Response resp = service(evt);

    // 响应
    ServerTransaction st = getServerTransaction(evt, sipProvider);
    st.sendResponse(resp);
  }

  private ServerTransaction getServerTransaction(RequestEvent evt, SipProvider sipProvider)
      throws TransactionAlreadyExistsException, TransactionUnavailableException {
    ServerTransaction st = evt.getServerTransaction();
    if (st == null) {
      st = sipProvider.getNewServerTransaction(evt.getRequest());
    }
    return st;
  }

  @Override
  public abstract boolean isSupport(Request req);

  /**
   * 具体业务
   */
  public abstract Response service(RequestEvent evt) throws Exception;
}
