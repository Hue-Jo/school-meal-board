package com.zerobase.schoolmealboard.exceptions.custom;

public class UnAuthorizedUser extends RuntimeException{
  public UnAuthorizedUser(String s) {
    super(s);
  }

}
