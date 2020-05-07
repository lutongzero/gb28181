package com.github.gb28181.gb;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Resource;
import javax.sip.Dialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.github.gb28181.SipServerInfo;
import com.github.gb28181.entity.Device;
import com.github.gb28181.entity.StreamInfo;
import com.github.gb28181.xmlbean.CatalogResp;

@Service
public class RedisStoreService implements CommonStoreService {
    private static final String DEVICE = "device_GB";
    private static final String ALIVE = "alive_GB_";
    private static final String CATALOG = "catalog_GB_";

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemp;
    @Autowired
    private SipServerInfo info;

    private Map<String, StreamInfo> streamMap = new ConcurrentHashMap<String, StreamInfo>();

    @Override
    public void saveDevice(Device device) {
        redisTemp.opsForSet().add(DEVICE, device);
    }

    @Override
    public void saveCatalog(CatalogResp catalog) {
        redisTemp.opsForValue().set(CATALOG + catalog.getDeviceID(), catalog);
    }

    @Override
    public CatalogResp getCatalog(String deviceId) {

        return (CatalogResp) redisTemp.opsForValue().get(CATALOG + deviceId);
    }

    @Override
    public Device getDevice(String deviceId) {
        Device d = null;
        Set<Object> members = redisTemp.opsForSet().members(DEVICE);
        members.size();
        for (Object o : members) {
            Device de = (Device) o;
            if (de.getDeviceId().equals(deviceId)) {
                d = de;
                break;
            }
        }
        return d;
    }



    @Override
    public void keepAlive(String deviceId) {
        redisTemp.opsForValue().set(ALIVE + deviceId, deviceId,
                Duration.ofSeconds(info.getHeart() + 10));
    }

    @Override
    public boolean isAlive(String deviceId) {

        return redisTemp.opsForValue().get(deviceId) == null ? false : true;
    }

    @Override
    public void saveStreamInfo(StreamInfo streamInfo) {
        streamMap.put(streamInfo.getCallId(), streamInfo);
    }

    @Override
    public void removeStreamInfo(String callId) {
        streamMap.remove(callId);
    }

    @Override
    public void saveDialog(String callId, Dialog dialog) {
        StreamInfo streamInfo = streamMap.get(callId);
        streamInfo.setDialog(dialog);
    }

    @Override
    public StreamInfo getStreamInfo(String callId) {

        return streamMap.get(callId);
    }

    public Map<String, StreamInfo> getStreamMap() {
        return streamMap;
    }

    public void setStreamMap(Map<String, StreamInfo> streamMap) {
        this.streamMap = streamMap;
    }


}
