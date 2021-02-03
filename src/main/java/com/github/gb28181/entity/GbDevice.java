package com.github.gb28181.entity;

import java.io.Serializable;
import java.util.Date;

import javax.sip.header.FromHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;

import org.springframework.util.StringUtils;

import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = "deviceId")
public class GbDevice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String deviceId;
	private String address;
	private String received;
	private Integer rport;
	private String name;
	private String transport;
	private Date lastRegisterDate;
	private Date lastAliveDate;
	private Date createDate;

	public static GbDevice createDevice(Request request) {
		GbDevice device = new GbDevice();
		FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
		AddressImpl address = (AddressImpl) fromHeader.getAddress();
		SipUri uri = (SipUri) address.getURI();
		device.setDeviceId(uri.getUser());
		ViaHeader viaHeader = (ViaHeader) request.getHeader(ViaHeader.NAME);
		String received = viaHeader.getReceived();
		int rPort = viaHeader.getRPort();
		if (StringUtils.isEmpty(received) || rPort == -1) {
			received = viaHeader.getHost();
			rPort = viaHeader.getPort();
		}
		device.setTransport(viaHeader.getTransport());
		device.setReceived(received);
		device.setRport(rPort);
		device.setAddress(received.concat(":").concat(String.valueOf(rPort)));
		return device;
	}
}
