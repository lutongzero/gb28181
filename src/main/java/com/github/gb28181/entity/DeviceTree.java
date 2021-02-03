package com.github.gb28181.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceTree {
	private String deviceId;
	private String name;
	@Builder.Default
	private Set<DeviceTree> children=new LinkedHashSet<>(0);
	private String parentId;

}
