package com.bucg.cjcloud.gb28181.core.xmlbean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@XStreamAlias(value = "Notify")
@Data
public class KeepaliveResp {
	
	private String CmdType,SN,DeviceID,Status;
}
