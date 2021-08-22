package com.github.gb28181.media;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.Data;

@FeignClient(name = "zlmeida", url = "${media.addr}")
public interface ZLmediaClient {

	/**
	 * 关闭流
	 */
	@GetMapping("/index/api/close_streams")
	CsResp closeStreams(@RequestParam("secret") String secret, @RequestParam("app") String app,
			@RequestParam("stream") String stream, @RequestParam("force") int force);

	/***
	 * 打开rtp端口
	 * 
	 * @param secret
	 * @param port
	 * @param enable_tcp
	 * @param stream_id
	 * @return
	 */
	@GetMapping("/index/api/openRtpServer")
	OpenRtpResp openRtpServer(@RequestParam("secret") String secret, @RequestParam("port") int port,
			@RequestParam("enable_tcp") int enable_tcp, @RequestParam("stream_id") String stream_id);

	/***
	 * 截图
	 * 
	 * @param secret
	 * @param url
	 * @param timeout_sec
	 * @param expire_sec
	 * @return
	 */
	@GetMapping(value = "/index/api/getSnap")
	byte []  getSnap(@RequestParam("secret") String secret, @RequestParam("url") String url,
			@RequestParam("timeout_sec") int timeout_sec, @RequestParam("expire_sec") int expire_sec);

	@Data
	public static class R<T> {
		public String msg;
		public Integer code;
		public T data;

	}

	@Data
	public static class CsResp {
		public Integer code;
		public Integer count_hit;
		public Integer count_closed;
	}

	@Data
	public static class OpenRtpResp {
		public Integer code;
		public Integer port;

	}
}
