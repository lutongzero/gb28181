package com.github.gb28181.support;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SsrcManager {
  List<Integer> ssrcList = new CopyOnWriteArrayList<Integer>();
  private String ssrcRealm;
  private static final int MIN = 0;
  private static final int MAX = 9999;
  
  
  public SsrcManager(@Value(value = "${sipinfo.id}")String ssrcRealm) {
    
   this.ssrcRealm = ssrcRealm.substring(3, 8);
  }

  /**
   * 
    */
  public synchronized String getSsrc(boolean isRealTime) {
    int ssrc = 0;
    for (int i = MIN; i <= MAX; i++) {
      if (!ssrcList.contains(i)) {
        ssrc = i;
        ssrcList.add(i);
        break;
      }
    }
    return getSsrcString(ssrc, isRealTime);
  }
  private String getSsrcString(int ssrcint, boolean isRealTime) {
    StringBuffer buffer = new StringBuffer(15);
    buffer.append(String.valueOf(isRealTime ? 0 : 1));
    buffer.append(ssrcRealm);
    String ssrcStr = String.valueOf(ssrcint);
    int length = ssrcStr.length();
    for (int i = length; i < 4; i++) {
      buffer.append("0");
    }
    buffer.append(String.valueOf(ssrcStr));
    return buffer.toString();
  }

}
