package com.github.gb28181.entity;

import java.io.Serializable;
import javax.sip.header.FromHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import org.springframework.util.StringUtils;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(exclude = "deviceId")
public class Device implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  /**
   * 设备id
   */
  private String id;
  private String deviceId;
  private String address;
  private String received;
  private int rport;
  private String name;
  private String transport;

  public static Device createDevice(Request request) {
    Device device = new Device();
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
