package com.github.gb28181.support;

import java.text.ParseException;
import javax.annotation.Resource;
import javax.sip.address.Address;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * fromHeader 管理
 * from header 作为发送方来说，是固定的
 * 
 * <example>From: <sip:34020000001320000001@172.16.31.44:5060>;tag=1373018625</example>
 * <sip:34020000002000000001@3402000000>;tag=451458703
 * */
@Service
public class FromHeaderManager {

  @Autowired
  private HeaderFactory headerFactory;
  //address 启动已经固定
  @Resource(name = "fromAddress")
  private Address myAddress;
  /***
   * 获取新的from,new tag
   * 
   * @throws ParseException
   */
  public FromHeader getFromHeader() throws ParseException {
    return headerFactory.createFromHeader(myAddress, RandomStringUtils.randomNumeric(5));

  }
  /***
   * @throws ParseException
   */
  public FromHeader getFromHeader(String tag) throws ParseException {
    return headerFactory.createFromHeader(myAddress,tag);
  }
  
  public FromHeader getFromHeader(Address address,String tag) throws ParseException {
    return headerFactory.createFromHeader(address,tag);
  }
  
  public ToHeader getToHeader(Address address,String tag) throws ParseException {
    return headerFactory.createToHeader(address, tag);
  }
  
}
