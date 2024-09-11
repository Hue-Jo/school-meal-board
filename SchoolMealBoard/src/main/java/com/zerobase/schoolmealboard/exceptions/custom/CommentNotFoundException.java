package com.zerobase.schoolmealboard.exceptions.custom;

public class CommentNotFoundException extends RuntimeException{
  public CommentNotFoundException(String message) {
    super(message);
  }

}
