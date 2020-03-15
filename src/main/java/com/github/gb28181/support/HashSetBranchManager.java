package com.github.gb28181.support;

import java.util.HashMap;
import org.springframework.stereotype.Component;
@Component
public class HashSetBranchManager implements BranchManager {
  private HashMap<String,String> map = new HashMap<>();


  @Override
  public void remove(String branch) {
    map.remove(branch);
  }

  @Override
  public String get(String branch) {
    return map.get(branch);
  }

  @Override
  public String put(String branch, String status) {
    return   map.put(branch, status);
  }


}
