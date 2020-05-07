package com.github.gb28181.web;

import java.text.ParseException;
import javax.sdp.SdpException;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.gb28181.RequestMessageService;
import com.github.gb28181.entity.Device;
import com.github.gb28181.entity.StreamInfo;
import com.github.gb28181.gb.CommonStoreService;
import com.github.gb28181.xmlbean.CmdTypeEnmu;

@RestController
public class GBController {
  @Autowired
  private RequestMessageService rms;
  @Autowired
  private CommonStoreService storeService;

  @GetMapping("/play")
  public StreamInfo play(Device device, String channelId)
      throws SdpException, ParseException, InvalidArgumentException, SipException {

    return rms.sendInviate(storeService.getDevice(device.getDeviceId()), channelId);
  }

  @GetMapping("/catalog")
  public String refreshCatalog(String deviceId)
      throws ParseException, SipException, InvalidArgumentException {
    boolean alive = true;
    if (alive) {
      Device device = storeService.getDevice(deviceId);
      rms.sendCommonMessage(device, CmdTypeEnmu.Catalog);
    }
    return "success";
  }

  @GetMapping("/bye")
  public Object sendBye(String callId) throws SipException {
    rms.sendBye(callId);
    return "success";
  }

  @GetMapping("/getCatalog")
  public Object getCatalog(String deviceId) {
    return storeService.getCatalog(deviceId);

  }
}
