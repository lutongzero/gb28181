package com.github.gb28181.support.xmlbean;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备目录查询响应 附录A.2.1.g
 * 
 */

@Data
@XStreamAlias(value = "Response")
public class CatalogResp implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String DeviceID, SumNum;
	private List<DeviceItem> DeviceList;

	@Data
	@XStreamAlias(value = "Item")
	@EqualsAndHashCode(exclude = { "DeviceID" })
	public static class DeviceItem implements Serializable {

		/**
		   * 
		   */
		private static final long serialVersionUID = 1L;

		private String DeviceID, Name, ParentID, Status;
		
	}

}
