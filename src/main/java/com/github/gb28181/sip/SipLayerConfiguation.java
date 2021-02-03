package com.github.gb28181.sip;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TooManyListenersException;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.ContactHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.message.MessageFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import com.github.gb28181.config.Constants;
import com.github.gb28181.config.SipServerInfo;
import com.github.gb28181.gb.DelegatingRequestHandler;
import com.github.gb28181.gb.DelegatingRespHandler;

@Configuration
public class SipLayerConfiguation {

  @Bean
  public HeaderFactory createHeaderFactory() throws PeerUnavailableException {

    return SipFactory.getInstance().createHeaderFactory();

  }

  @Bean
  public AddressFactory createAddressFactory() throws PeerUnavailableException {

    return SipFactory.getInstance().createAddressFactory();
  }

  @Bean
  public MessageFactory createMessageFactory() throws PeerUnavailableException {

    return SipFactory.getInstance().createMessageFactory();
  }

  @Bean(name = "fromAddress")
  public Address createAddress(AddressFactory addressFactory, SipServerInfo sipinfo)
      throws ParseException {
    SipURI from = addressFactory.createSipURI(sipinfo.getId(),
        sipinfo.getIp() + ":" + sipinfo.getPort());
    Address fromNameAddress = addressFactory.createAddress(from);
    fromNameAddress.setDisplayName(sipinfo.getRealm());
    return fromNameAddress;
  }

  @Bean(name = "contactHeader")
  public ContactHeader getContactHeader(AddressFactory addressFactory, SipServerInfo sipinfo,
      HeaderFactory headerFactory) throws ParseException {
    SipURI contactURI = addressFactory.createSipURI(sipinfo.getRealm(), sipinfo.getPort() + "");
    Address contactAddress = addressFactory.createAddress(contactURI);
    contactAddress.setDisplayName(sipinfo.getRealm());
    ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
    return contactHeader;
  }

  @Bean
  @Scope("prototype")
  public MaxForwardsHeader getDefaultMaxForwardsHeader(HeaderFactory headerFactory)
      throws InvalidArgumentException {
    return headerFactory.createMaxForwardsHeader(70);
  }

  /**
   * 未设置addlistener
   */
  @Bean(name="sipProviderMap")
  public  Map<String,SipProvider> createSipProvider(SipServerInfo info)
      throws TooManyListenersException, TransportNotSupportedException, InvalidArgumentException,
      PeerUnavailableException, ObjectInUseException {
    String ip = info.getIp();
    int port = info.getPort();
    SipFactory sipFactory = SipFactory.getInstance();
    Properties properties = new Properties();
    properties.setProperty("javax.sip.STACK_NAME", "GB28181-2016");
    properties.setProperty("javax.sip.IP_ADDRESS", ip);
    SipStack sipStack = sipFactory.createSipStack(properties);
    ListeningPoint tcp = sipStack.createListeningPoint(ip, port, "tcp");
    ListeningPoint udp = sipStack.createListeningPoint(ip, port, "udp");
    SipProvider tcpSipProvider = sipStack.createSipProvider(tcp);
    SipProvider udpSipProvider = sipStack.createSipProvider(udp);
    Map<String,SipProvider> map=new HashMap<>();
    map.put(Constants.TCP_SIP_PROVIDER, tcpSipProvider);
    map.put(Constants.UDP_SIP_PROVIDER, udpSipProvider);
    return map;
  }

  @Bean(name = "delegatingRequestHandler")
  public RequestHandler getDelegatingRequestProcess(List<RequestHandler> list) {
    return new DelegatingRequestHandler(list);

  }

  @Bean(name = "delegatingResptHandler")
  public ResponseHandler getDelegatingResptHandler(List<ResponseHandler> list) {
    return new DelegatingRespHandler(list);

  }
}
