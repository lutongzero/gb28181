package com.github.gb28181.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = { "deviceId", "name" })
public class DeviceTree {
	private String deviceId;
	private String name;
	private String parentId;
	private String status;

}
