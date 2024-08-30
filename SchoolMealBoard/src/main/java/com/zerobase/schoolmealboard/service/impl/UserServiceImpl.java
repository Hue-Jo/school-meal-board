package com.zerobase.schoolmealboard.service.impl;

import com.zerobase.schoolmealboard.dto.UserDto;
import com.zerobase.schoolmealboard.dto.UserDto.SignUp;
import com.zerobase.schoolmealboard.entity.School;
import com.zerobase.schoolmealboard.entity.User;
import com.zerobase.schoolmealboard.repository.SchoolRepository;
import com.zerobase.schoolmealboard.repository.UserRepository;
import com.zerobase.schoolmealboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final SchoolRepository schoolRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * 회원가입
   */
  @Override
  public String signUp(SignUp signInDto) {

    if (userRepository.findByPhoneNum(signInDto.getPhoneNum()).isPresent()) {
      return "중복 가입은 불가능합니다.";
    }

    if (userRepository.findByEmail(signInDto.getEmail()).isPresent()) {
      return "이미 사용중인 이메일입니다.";
    }

    if (userRepository.findByNickName(signInDto.getNickname()).isPresent()) {
      return "이미 사용중인 닉네임입니다.";
    }

    User user = UserDto.SignUp.toUser(signInDto);

    School schoolCode = schoolRepository.findById(signInDto.getSchoolCode())
        .orElseThrow(() -> new RuntimeException("학교코드를 정확히 적어주세요."));
    user.setSchoolCode(schoolCode); // 학교코드

    user.setPassword(passwordEncoder.encode(signInDto.getPassword())); // 비밀번호 암호화

    userRepository.save(user);
    return "회원가입이 완료되었습니다.";
  }


  /**
   * 회원정보 수정
   */



  /**
   * 회원 탈퇴
   */


  /**
   * 로그인
   */

}
