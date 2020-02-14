package com.bucg.cjcloud.gb28181.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thoughtworks.xstream.XStream;

@Configuration
public class CommonConfiguation {
	
	@Bean
	public XStream getXStream() {
		XStream xstream = new XStream();
		xstream.autodetectAnnotations(true);
		return xstream;
	}
}
