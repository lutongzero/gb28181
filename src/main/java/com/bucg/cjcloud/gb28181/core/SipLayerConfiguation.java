package com.bucg.cjcloud.gb28181.core;

import java.text.ParseException;
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
import javax.sip.message.MessageFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.bucg.cjcloud.gb28181.core.process.RequestProcess;
import com.bucg.cjcloud.gb28181.core.process.RequestProcessFactories;

@Configuration
public class SipLayerConfiguation {
	
	@Bean
	public HeaderFactory  createHeaderFactory() throws PeerUnavailableException {
		
		return SipFactory.getInstance().createHeaderFactory();
		
	}
	
	@Bean
	public AddressFactory createAddressFactory() throws PeerUnavailableException {
		
	return 	 SipFactory.getInstance().createAddressFactory();
	}
	
	@Bean
	public MessageFactory createMessageFactory() throws PeerUnavailableException  {
		
	return 	 SipFactory.getInstance().createMessageFactory();
	}
	
	@Bean(name = "fromAddress")
	public Address createAddress(AddressFactory addressFactory,SipServerInfo sipinfo) throws ParseException {
		SipURI from = addressFactory.createSipURI(sipinfo.getUsername(), sipinfo.getIp()+ ":" +sipinfo.getPort());
			Address fromNameAddress = addressFactory.createAddress(from);
			fromNameAddress.setDisplayName(sipinfo.getUsername());
			System.out.println("aa");
			return fromNameAddress;
	}
	@Bean(name="contactHeader")
	public ContactHeader getContactHeader(AddressFactory addressFactory,SipServerInfo sipinfo,HeaderFactory headerFactory) throws ParseException {
		SipURI contactURI = addressFactory.createSipURI(sipinfo.getUsername(),sipinfo.getPort()+"");
		Address contactAddress = addressFactory.createAddress(contactURI);
		contactAddress.setDisplayName(sipinfo.getUsername());
		ContactHeader contactHeader = headerFactory
			.createContactHeader(contactAddress);
		System.out.println("bb");
			return contactHeader;
	}
	
	/**
	 * 未设置addlistener
	 * */
	@Bean
	public SipProvider createSipProvider(SipServerInfo info) throws TooManyListenersException, TransportNotSupportedException, InvalidArgumentException, PeerUnavailableException, ObjectInUseException {
		String ip=info.getIp();
    	int port=info.getPort();
		SipFactory sipFactory = SipFactory.getInstance();
		Properties properties = new Properties();
		properties.setProperty("javax.sip.STACK_NAME", "GB28181-2016");
		properties.setProperty("javax.sip.IP_ADDRESS",ip);
		SipStack sipStack = sipFactory.createSipStack(properties);
		ListeningPoint tcp = sipStack.createListeningPoint(ip,port,"tcp");
		ListeningPoint udp = sipStack.createListeningPoint(ip,port, "udp");
		SipProvider sipProvider= sipStack.createSipProvider(tcp);
		sipProvider = sipStack.createSipProvider(udp);
	
		return 	 sipProvider;
	}
	@Bean
	public RequestProcess getDelegatingRequestProcess() {
	  return RequestProcessFactories.createDelegatingRequestProcess();
	  
	}
}
