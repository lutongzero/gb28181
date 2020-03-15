package com.github.gb28181;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import com.thoughtworks.xstream.XStream;

@SpringBootApplication
@RestController
public class Gb28181Application {
  public static void main(String[] args) {
    SpringApplication.run(Gb28181Application.class, args);
  }

  @Bean
  public XStream getXStream() {
    XStream xstream = new XStream();
    xstream.autodetectAnnotations(true);
    return xstream;
  }
}
