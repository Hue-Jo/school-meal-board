package com.zerobase.schoolmealboard.service;

import com.zerobase.schoolmealboard.dto.UserDto;
import com.zerobase.schoolmealboard.dto.UserDto.LogIn;
import com.zerobase.schoolmealboard.dto.UserDto.SignUp;
import com.zerobase.schoolmealboard.dto.UserDto.Update;

public interface UserService {

  // 회원가입
  String signUp(SignUp signUpDto);

  String updateProfile(Update updateDto);

  String logIn(LogIn logInDto);
}
