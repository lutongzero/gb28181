package com.bucg.cjcloud.gb28181.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix = "sipinfo")
public class SipServerInfo {
	private String username;
	private String ip;
	private int port;
	private String domain;
}
