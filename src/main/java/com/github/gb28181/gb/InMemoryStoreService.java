package com.github.gb28181.gb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sip.Dialog;

import org.springframework.stereotype.Service;

import com.github.gb28181.entity.DeviceTree;
import com.github.gb28181.entity.GbDevice;
import com.github.gb28181.entity.StreamInfo;
import com.github.gb28181.support.xmlbean.CatalogResp;
import com.github.gb28181.support.xmlbean.CatalogResp.DeviceItem;

@Service
public class InMemoryStoreService implements CommonStoreService {

	private Map<String, StreamInfo> streamMap = new ConcurrentHashMap<String, StreamInfo>();
	private Set<CatalogResp> catalogSet = ConcurrentHashMap.newKeySet();
	private Map<String, GbDevice> deviceMap = new ConcurrentHashMap<String, GbDevice>();

	private Map<String, Set<DeviceTree>> deviceTreeMap = new ConcurrentHashMap<String, Set<DeviceTree>>();

	@Override
	public void saveCatalog(CatalogResp catalog) {
		String deviceID = catalog.getDeviceID();
		Set<DeviceTree> deviceSet = deviceTreeMap.get(deviceID);

		if (deviceSet == null) {
			deviceSet = new LinkedHashSet<>();
			deviceTreeMap.put(deviceID, deviceSet);
		}

		// 处理item
		List<DeviceItem> items = catalog.getDeviceList();
		for (DeviceItem item : items) {
			deviceSet.add(DeviceTree.builder().deviceId(item.getDeviceID()).name(item.getName()).status(item.getStatus())
					.parentId(item.getParentID()).build());

		}

		catalogSet.add(catalog);
	}

	@Override
	public CatalogResp getCatalog(String deviceId) {
		return catalogSet.stream().filter(d -> d.getDeviceID().equals(deviceId)).findFirst().orElse(null);
	}

	@Override
	public void keepAlive(String deviceId) {

	}

	@Override
	public boolean isAlive(String deviceId) {
		return true;
	}

	@Override
	public void saveStreamInfo(StreamInfo streamInfo) {
		streamMap.put(streamInfo.getCallId(), streamInfo);
	}

	@Override
	public void saveDialog(String callId, Dialog dialog) {
		StreamInfo streamInfo = streamMap.get(callId);
		streamInfo.setDialog(dialog);
	}

	@Override
	public void removeStreamInfo(String callId) {
		streamMap.remove(callId);
	}

	@Override
	public StreamInfo getStreamInfo(String callId) {

		return streamMap.get(callId);
	}

	@Override
	public GbDevice getGbDevice(String deviceId) {
		return deviceMap.get(deviceId);

	}

	@Override
	public void saveGbDevice(GbDevice device) {
		deviceMap.put(device.getDeviceId(), device);
	}

	@Override
	public Set<CatalogResp> getCatalog() {

		return catalogSet;
	}

	@Override
	public Collection<DeviceTree> getDeviceTree(String deviceId) {

		return deviceTreeMap.get(deviceId) == null ? new ArrayList<DeviceTree>(0) : deviceTreeMap.get(deviceId);
	}

	@Override
	public Set<String> getdeviceIds() {
		return deviceMap.keySet();
	}

}
