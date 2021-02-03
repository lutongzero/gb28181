package com.github.gb28181.support.xmlbean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias(value = "Query")
public class CommonReq {
  private CmdTypeEnmu CmdType;
  private String SN, DeviceID;

  public CommonReq(CmdTypeEnmu cmdType, String sN, String deviceID) {
    super();
    CmdType = cmdType;
    SN = sN;
    DeviceID = deviceID;
  }

  public CommonReq() {
    super();
  }


}
