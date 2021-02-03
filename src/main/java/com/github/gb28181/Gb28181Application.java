package com.github.gb28181;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.gb28181.support.xmlbean.CatalogResp;
import com.github.gb28181.support.xmlbean.CommonReq;
import com.github.gb28181.support.xmlbean.KeepaliveResp;
import com.thoughtworks.xstream.XStream;

@SpringBootApplication
@RestController
@EnableFeignClients()
public class Gb28181Application implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(Gb28181Application.class, args);

	}

	@Bean
	public XStream getXStream() {
		XStream xstream = new XStream();
		xstream.autodetectAnnotations(true);
		xstream.ignoreUnknownElements();
		XStream.setupDefaultSecurity(xstream);
		xstream.allowTypes(
				new Class[] { CatalogResp.class, CommonReq.class, CatalogResp.DeviceItem.class, KeepaliveResp.class });
		return xstream;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
				.allowCredentials(true).maxAge(3600).allowedHeaders("*");
	}

}
