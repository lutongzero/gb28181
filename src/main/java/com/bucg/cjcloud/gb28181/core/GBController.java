package com.bucg.cjcloud.gb28181.core;

import java.text.ParseException;
import javax.sdp.SdpException;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bucg.cjcloud.gb28181.core.entity.Device;

@RestController
public class GBController {
  @Autowired
  RequestMessageService rms;
  
  
  @GetMapping("/play")
  public String play(Device device,String channelId) throws SdpException, ParseException, InvalidArgumentException, SipException {
    
   rms.play(device, channelId);
    
    return "success";
  }
}
