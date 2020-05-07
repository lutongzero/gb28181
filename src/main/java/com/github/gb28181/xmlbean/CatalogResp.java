package com.github.gb28181.xmlbean;

import java.io.Serializable;
import java.util.List;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

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
  private CmdTypeEnmu CmdType;
  private String DeviceID, SumNum;
  private List<DeviceItem> DeviceList;
  private String SN;
}


@Data
@XStreamAlias(value = "Item")
class DeviceItem implements Serializable{
  /**
     * 
     */
    private static final long serialVersionUID = 1L;

/**
   * Manufacturer 厂商 Model 型号 Owner 归属 CivilCode 行政区域 Block 警区 Parental是否有子设备 SafetyWay
   * 信令安全模式(可选)缺省为 0 ; 0 :不采用; 2 : S / MIME 签名方式; 3 : S /MIME 加密签名同时采用方式; 4 :数字摘要方式 RegisterWay
   * 注册方式(必选)缺省为 1 ; 1 :符合 IETFRFC3261 标准的认证注册模式;2 :基于口令的双向认证注册模式; 3 :基于数字证书的双向认证注册模式 Certifiable
   * 证书有效标识(有证书的设备必选)缺省为 0 ;证书有效标识: 0 :无效 1 :有效 --
   * 
   */
  private String DeviceID, Name, Manufacturer, Model, Owner, CivilCode, Block, Address, Parental,
      ParentID, SafetyWay, RegisterWay, CertNum, Certifiable, ErrCode, EndTime, Secrecy, IPAddress,
      Port, Password, Status, Longitude, Latitude;

  private Integer PTZType, PositionType, RoomType, UseType, SupplyLightType, DirectionType,
      Resolution, BusinessGroupID, DownloadSpeed, SVCSpaceSupportMode, SVCTimeSupportMode;


}
