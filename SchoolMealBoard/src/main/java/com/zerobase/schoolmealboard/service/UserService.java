package com.zerobase.schoolmealboard.service;

import com.zerobase.schoolmealboard.dto.UserDto.SignUp;

public interface UserService {

  // 회원가입
  String signUp(SignUp signUpDto);
}
