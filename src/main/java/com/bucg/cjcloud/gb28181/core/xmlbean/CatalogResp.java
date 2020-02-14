package com.bucg.cjcloud.gb28181.core.xmlbean;

import lombok.Data;

@Data
public class CatalogResp {
	private CmdTypeEnmu CmdType;
	private String SN,DeviceID,SumNum;
}
