package com.bucg.cjcloud.gb28181.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Gb28181Application {
	public static void main(String[] args) {
		SpringApplication.run(Gb28181Application.class, args);
		//new Gb28181Application().run();
	}
	/*
	 * public void run() { SipServerInfo info = new SipServerInfo("admin",
	 * "172.16.16.107", 5060,"3420001"); RequestProcess requestProcess =
	 * RequestProcessFactories.createDelegatingRequestProcess(); try {
	 * 
	 * sipLayer = new SipLayer();;//本地 sipLayer.setRequestProcess(requestProcess);
	 * 
	 * System.out.println("服务启动完毕"); } catch (PeerUnavailableException e) {
	 * e.printStackTrace(); } catch (TransportNotSupportedException e) {
	 * e.printStackTrace(); } catch (ObjectInUseException e) { e.printStackTrace();
	 * } catch (InvalidArgumentException e) { e.printStackTrace(); } catch
	 * (TooManyListenersException e) { e.printStackTrace(); } }
	 * 
	 */	

}
