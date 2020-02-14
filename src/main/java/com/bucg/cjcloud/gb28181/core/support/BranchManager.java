package com.bucg.cjcloud.gb28181.core.support;

public interface BranchManager {

  boolean isExsit(String branch);

  void put(String branch);

  void remove(String branch);

}
