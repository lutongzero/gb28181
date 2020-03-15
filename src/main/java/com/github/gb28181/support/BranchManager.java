package com.github.gb28181.support;

/**
 * branch 标识同一个事务<br/>
 * branch以z9hG4bk开头
 */
public interface BranchManager {
  public static final String MAGIC_HEAD = "z9hG4bK";

  String get(String branch);

  void remove(String branch);

  String put(String branch, String status);
}
