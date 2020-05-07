package com.github.gb28181;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix = "sipinfo")
public class SipServerInfo {
  private String id;
  private String ip;
  private int port;
  private String realm;
  private String password;
  private int heart;
  private String meidaIp;
  private int mediaPort;
}
