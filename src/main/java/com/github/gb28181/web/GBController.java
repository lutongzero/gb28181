package com.github.gb28181.web;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Set;

import javax.sdp.SdpException;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.gb28181.entity.DeviceTree;
import com.github.gb28181.entity.GbDevice;
import com.github.gb28181.entity.StreamInfo;
import com.github.gb28181.gb.CommonStoreService;
import com.github.gb28181.media.ZLmediaClient;
import com.github.gb28181.service.RequestMessageService;
import com.github.gb28181.support.xmlbean.CmdTypeEnmu;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class GBController {
	@Autowired
	private RequestMessageService rms;
	@Autowired
	private CommonStoreService storeService;
	@Autowired
	private ZLmediaClient mediaClient;
	@Value("${media.secret}")
	private String mediaSecrt;

	@GetMapping("/play")
	public StreamInfo play(String deviceId, String channelId)
			throws SdpException, ParseException, InvalidArgumentException, SipException {
		GbDevice device = storeService.getGbDevice(deviceId);
		return rms.play(device, channelId);
	}

	@GetMapping("/playback")
	public StreamInfo play(String userId, String deviceId, String channelId,
			@DateTimeFormat(pattern = "yyyyMMddHHmmss") LocalDateTime start,
			@DateTimeFormat(pattern = "yyyyMMddHHmmss") LocalDateTime end) throws Exception {
		GbDevice device = storeService.getGbDevice(deviceId);
		return rms.playback(device, channelId, start, end, userId);
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

	@GetMapping("/device")
	public Object device() {
		return storeService.getdeviceIds();
	}

	@GetMapping("/deviceTree")
	public Object getDeviceTree(String deviceId) {
		return storeService.getDeviceTree(deviceId);
	}

	@PostMapping("/index/hook/on_stream_none_reader")
	public Stream_none_readerResp on_stream_none_reader(@RequestBody Stream_none_readerReq req) {
		log.info("流无人观看事件：{}", req);
		rms.closeStream(req.getApp(), req.getStream());
		return new Stream_none_readerResp();
	}

	@GetMapping("/snap")
	public Object snap(String playUrl, int timeout, int expire) {
		Object snap = mediaClient.getSnap(mediaSecrt, playUrl, timeout, expire);
		return snap;

	}

	@GetMapping("/ptz")
	public void ptz(ptzReq req) throws ParseException, SipException, Exception {
		GbDevice gbDevice = searchGbDevice(req.getChannelId());
		if (gbDevice != null) {
			log.info("找到通道关联设备，正在发送ptz命令");
			rms.ptzCmd(gbDevice, req.getChannelId(), req.getLeftRight(), req.getUpDown(), req.getInOut(),
					req.getMoveSpeed(), req.getZoomSpeed());
		} else {
			log.error("未找到通道关联设备{}", req.getChannelId());
		}
	}

	@GetMapping("/record")
	public void recordInfoQuery(String channelId, @DateTimeFormat(pattern = "yyyyMMddHHmmss") LocalDateTime start,
			@DateTimeFormat(pattern = "yyyyMMddHHmmss") LocalDateTime end) throws Exception {
		GbDevice gbDevice = searchGbDevice(channelId);
		rms.recordInfoQuery(gbDevice, channelId, start, end);

	}

	@PostMapping("/index/hook/on_stream_not_found")
	public Stream_none_foundResp on_stream_not_found(@RequestBody Stream_none_foundReq req) throws Exception {
		log.info("流404事件：{}", req);
		if (!"rtp".equals(req.getApp())) {
			return new Stream_none_foundResp();
		}
		String params = req.getParams();
		if (params.contains("playback")) {
			sendPlayBack(req);
			return new Stream_none_foundResp();
		}

		GbDevice gbDevice = searchGbDevice(req.getStream());
		if (gbDevice != null) {
			log.info("找到通道关联设备，正在发送");
			rms.play(gbDevice, req.getStream());
		} else {
			log.error("未找到通道关联设备{}", req.getStream());

		}

		return new Stream_none_foundResp();
	}

	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	private void sendPlayBack(Stream_none_foundReq req) throws Exception {
		String params = req.getParams();
		// 找开始时间和结束时间
		String[] split = params.split("&");
		if (split.length < 3) {
			log.error("没有开始时间，结束时间");
			return;
		}
		String start = null;
		String end = null;
		for (String str : split) {
			if (str.contains("start") && str.contains("=")) {
				start = str.split("=")[1];
			}
			if (str.contains("end") && str.contains("=")) {
				end = str.split("=")[1];
			}
		}
		if (start == null || end == null) {
			log.error("未获取到开始时间或结束时间");
			return;
		}
		String stream = req.getStream();
		String[] split2 = stream.split("/");

		GbDevice gbDevice = searchGbDevice(split2[1]);
		if (gbDevice == null) {
			log.error("未找到通道关联设备{}", req.getStream());
			return;
		}
		LocalDateTime sDate = LocalDateTime.parse(start, dtf);
		LocalDateTime eDate = LocalDateTime.parse(end, dtf);
		rms.recordInfoQuery(gbDevice, split2[1], sDate, eDate);
		rms.playback(gbDevice, split2[1], sDate, eDate, split2[0]);

	}

	private GbDevice searchGbDevice(String channelId) {
		Set<String> ids = storeService.getdeviceIds();
		for (String id : ids) {
			Collection<DeviceTree> trees = storeService.getDeviceTree(id);
			for (DeviceTree t : trees) {
				if (t.getDeviceId().equals(channelId)) {
					GbDevice device = storeService.getGbDevice(id);
					return device;
				}
			}

		}
		return null;
	}

	@Data
	public static class ptzReq {
		private String channelId;
		private int leftRight, upDown, inOut, moveSpeed, zoomSpeed;

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
