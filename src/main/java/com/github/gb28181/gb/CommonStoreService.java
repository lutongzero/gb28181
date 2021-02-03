package com.github.gb28181.gb;

import java.util.Set;

import javax.sip.Dialog;

import com.github.gb28181.entity.DeviceTree;
import com.github.gb28181.entity.GbDevice;
import com.github.gb28181.entity.StreamInfo;
import com.github.gb28181.support.xmlbean.CatalogResp;

public interface CommonStoreService {
	void saveGbDevice(GbDevice device);

	GbDevice getGbDevice(String deviceId);

	public void saveCatalog(CatalogResp catalog);

	public CatalogResp getCatalog(String deviceId);

	public Set<CatalogResp> getCatalog();

	public void keepAlive(String deviceId);

	public boolean isAlive(String deviceId);

	public void saveStreamInfo(StreamInfo streamInfo);

	public void saveDialog(String callId, Dialog dialog);

	public void removeStreamInfo(String callId);

	public StreamInfo getStreamInfo(String callId);

	DeviceTree getDeviceTree(String deviceId);
}
