package com.github.gb28181.support.xmlbean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 设备状态信息报送消息
 */
@XStreamAlias(value = "Notify")
@Data
public class KeepaliveResp {

  private String CmdType, SN, DeviceID, Status;
}
