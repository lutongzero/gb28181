package com.github.gb28181.support;

import org.springframework.stereotype.Service;

@Service
public class MemorySNManager implements SNManager{
  int i=1000;
  @Override
  public String getSN() {
    i++;
    return i+"";
  }

}
