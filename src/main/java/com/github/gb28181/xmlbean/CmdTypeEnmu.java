package com.github.gb28181.xmlbean;

/**
 * 命令类型
 **/
public enum CmdTypeEnmu {
  /**
   * 目录查询
   **/
  Catalog("Catalog"),

  /**
   * 设备信息查询
   */
  DeviceInfo("DeviceInfo");

  private String value;

  private CmdTypeEnmu(String value) {
    this.setValue(value);
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String toString() {

    return this.value;
  }
}
