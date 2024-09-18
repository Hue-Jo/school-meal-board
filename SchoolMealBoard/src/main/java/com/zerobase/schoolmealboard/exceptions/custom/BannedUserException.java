package com.zerobase.schoolmealboard.exceptions.custom;

public class BannedUserException extends RuntimeException {
  public BannedUserException(String s) {
    super(s);
  }

}
