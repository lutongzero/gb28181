package com.bucg.cjcloud.gb28181.core.support;

import java.util.HashSet;
import org.springframework.stereotype.Component;
@Component
public class HashSetBranchManager implements BranchManager {
  private HashSet<String> set = new HashSet<>();

  @Override
  public boolean isExsit(String branch) {
    return set.contains(branch);
  }

  @Override
  public void put(String branch) {
    set.add(branch);
  }

  @Override
  public void remove(String branch) {
    set.remove(branch);
  }


}
