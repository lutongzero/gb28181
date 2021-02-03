package com.github.gb28181.web;

import java.text.ParseException;

import javax.sdp.SdpException;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.gb28181.entity.GbDevice;
import com.github.gb28181.entity.StreamInfo;
import com.github.gb28181.gb.CommonStoreService;
import com.github.gb28181.service.RequestMessageService;
import com.github.gb28181.support.xmlbean.CmdTypeEnmu;

import lombok.Data;

@RestController
public class GBController {
	@Autowired
	private RequestMessageService rms;
	@Autowired
	private CommonStoreService storeService;

	@GetMapping("/play")
	public StreamInfo play(String deviceId, String channelId)
			throws SdpException, ParseException, InvalidArgumentException, SipException {
		GbDevice device = storeService.getGbDevice(deviceId);
		return rms.sendInviate(device, channelId);
	}

	@GetMapping("/catalog")
	public String refreshCatalog(String deviceId) throws ParseException, SipException, InvalidArgumentException {
		boolean alive = true;
		if (alive) {
			GbDevice device = storeService.getGbDevice(deviceId);
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
		return storeService.getCatalog();
	}

	@GetMapping("/deviceTree")
	public Object getDeviceTree(String deviceId) {
		return storeService.getDeviceTree(deviceId);
	}

	@PostMapping("/index/hook/on_stream_none_reader")
	public Stream_none_readerResp on_stream_none_reader(@RequestBody Stream_none_readerReq req) {
		System.out.println(req);
		return new Stream_none_readerResp();

	}

	@PostMapping("/index/hook/on_stream_not_found")
	public Stream_none_foundResp on_stream_not_found(@RequestBody Stream_none_foundReq req)
			throws SdpException, ParseException, InvalidArgumentException, SipException {
		System.out.println(req);
		if (req.getApp().equals("rtp")) {
			GbDevice device = storeService.getGbDevice("21012114572367709930");
			rms.sendInviate(device, req.getStream());
		}
		return new Stream_none_foundResp();
	}

	@Data
	public static class Stream_none_readerReq {
		private String mediaServerId, app, schema, stream, vhost;

	}

	@Data
	public static class Stream_none_readerResp {
		private int code = 0;
		private boolean close = true;

	}

	@Data
	public static class Stream_none_foundReq {
		private String mediaServerId, app, schema, stream, vhost, params, ip, port;

	}

	@Data
	public static class Stream_none_foundResp {
		private int code = 0;
		private String msg = "success";

	}

}
