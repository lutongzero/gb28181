package com.github.gb28181.gb;

import javax.sip.Dialog;
import com.github.gb28181.entity.Device;
import com.github.gb28181.entity.StreamInfo;
import com.github.gb28181.xmlbean.CatalogResp;

public interface CommonStoreService {

  public void saveDevice(Device device);

  public Device getDevice(String deviceId);

  public void saveCatalog(CatalogResp catalog);

  public CatalogResp getCatalog(String deviceId);

  public void keepAlive(String deviceId);

  public boolean isAlive(String deviceId);

  public void saveStreamInfo(StreamInfo streamInfo);

  public void saveDialog(String callId, Dialog dialog);

  public void removeStreamInfo(String callId);

  public StreamInfo getStreamInfo(String callId);
}
