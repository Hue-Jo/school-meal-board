package com.zerobase.schoolmealboard.exceptions.custom;

public class ConcurrentException extends RuntimeException {
  public ConcurrentException(String s) {
    super(s);
  }

}
