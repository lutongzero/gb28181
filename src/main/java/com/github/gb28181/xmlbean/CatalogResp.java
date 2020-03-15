package com.github.gb28181.xmlbean;

import lombok.Data;

@Data
public class CatalogResp {
	private CmdTypeEnmu CmdType;
	private String SN,DeviceID,SumNum;
}
