package com.github.gb28181.gb;

import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.sip.RequestEvent;
import javax.sip.header.ContactHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.github.gb28181.config.SipServerInfo;
import com.github.gb28181.entity.GbDevice;
import com.github.gb28181.service.RequestMessageService;
import com.github.gb28181.support.xmlbean.CmdTypeEnmu;

import gov.nist.javax.sip.clientauthutils.DigestServerAuthenticationHelper;
import gov.nist.javax.sip.header.AuthenticationHeader;
import gov.nist.javax.sip.header.ProxyAuthorization;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 9.1.2.1 基本注册
 * 
 **/
@Order(1)
@Slf4j
@Component
public class RegisterRequestHandler extends AbstractCommonRequestHandler {
	private DigestServerAuthenticationHelper digestHelper;
	@Autowired
	private SipServerInfo info;
	@Autowired
	private MessageFactory messageFactory;
	@Autowired
	private HeaderFactory headerFactory;
	@Autowired
	private CommonStoreService storeService;
	@Autowired
	private RequestMessageService rms;
	@Value("${skipReg}")
	private boolean skipReg;

	public RegisterRequestHandler() throws GeneralSecurityException {
		super();
		this.digestHelper = new DigestServerAuthenticationHelper();
	}

	@Override
	public Response service(RequestEvent evt) throws Exception {
		SIPRequest sipReq = (SIPRequest) evt.getRequest();
		AuthenticationHeader authorization = sipReq.getAuthorization();
		if (authorization == null) {
			authorization = (AuthenticationHeader) sipReq.getHeader(ProxyAuthorization.NAME);

		}
		Response response = null;

		boolean chackpwd = false; // 如果携带认证信息，验证密码 i
		if (authorization != null) {
			chackpwd = digestHelper.doAuthenticatePlainTextPassword(sipReq, info.getPassword());
			if (!chackpwd) {
				log.info("password error!");
			}
		}

		// 如果未携带或者密码错误,添加验证信息，返回401
		if (authorization == null || !chackpwd || !skipReg) {
			response = messageFactory.createResponse(Response.UNAUTHORIZED, sipReq);
			// 添加验证信息
			digestHelper.generateChallenge(headerFactory, response, info.getRealm());
		}

		// 验证成功
		if (authorization != null && chackpwd || skipReg) {
			response = messageFactory.createResponse(Response.OK, sipReq);
			response.addHeader(headerFactory.createDateHeader(Calendar.getInstance(Locale.ENGLISH)));
			response.addHeader(sipReq.getHeader(ContactHeader.NAME));
			response.addHeader(sipReq.getExpires());

			// 保存
			GbDevice device = saveDevice(sipReq);
			
			rms.sendCommonMessage(device, CmdTypeEnmu.Catalog);
		}
		return response;

	}

	private GbDevice saveDevice(Request req) {
		GbDevice device = GbDevice.createDevice(req);
		GbDevice gbDevice = storeService.getGbDevice(device.getDeviceId());
		if (gbDevice == null) {
			device.setCreateDate(new Date());
			device.setLastRegisterDate(new Date());
			storeService.saveGbDevice(device);
		} else {
			gbDevice.setLastRegisterDate(new Date());
			gbDevice.setReceived(device.getReceived());
			gbDevice.setAddress(device.getAddress());
			gbDevice.setReceived(device.getReceived());
			gbDevice.setRport(device.getRport());
			storeService.saveGbDevice(gbDevice);
		}
		
		log.info("device {} register success! ", device.getDeviceId());
		return device;
	}

	@Override
	public boolean isSupport(Request req) {
		return Request.REGISTER.equals(req.getMethod());
	}

}
