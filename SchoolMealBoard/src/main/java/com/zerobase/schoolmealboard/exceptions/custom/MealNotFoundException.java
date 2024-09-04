package com.zerobase.schoolmealboard.exceptions.custom;

public class MealNotFoundException extends RuntimeException{

  public MealNotFoundException(String s) {
    super(s);
  }

}
