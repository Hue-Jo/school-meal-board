package com.zerobase.schoolmealboard.service;

import com.zerobase.schoolmealboard.dto.UserDto;
import com.zerobase.schoolmealboard.dto.UserDto.LogIn;
import com.zerobase.schoolmealboard.dto.UserDto.SignUp;
import com.zerobase.schoolmealboard.dto.UserDto.Update;

public interface UserService {

  // 회원가입
  void signUp(SignUp signUpDto);

  // 로그인
  String logIn(LogIn logInDto);

  // 회원정보 수정
  void updateUser(String email, Update updateDto);

  // 탈퇴
  void deleteUser(String email, UserDto.Delete deleteDto);
}
