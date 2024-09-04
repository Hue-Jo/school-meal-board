package com.zerobase.schoolmealboard.exceptions.custom;

public class UserNotFoundException extends RuntimeException{

  public UserNotFoundException(String s){
    super(s);
  }

}
