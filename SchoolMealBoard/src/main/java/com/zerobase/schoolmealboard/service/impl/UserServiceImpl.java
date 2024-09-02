package com.zerobase.schoolmealboard.service.impl;

import com.zerobase.schoolmealboard.component.security.JwtUtil;
import com.zerobase.schoolmealboard.dto.UserDto;
import com.zerobase.schoolmealboard.dto.UserDto.LogIn;
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
  private final JwtUtil jwtUtil;

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

    User user = UserDto.SignUp.toEntity(signInDto);

    School schoolCode = schoolRepository.findById(signInDto.getSchoolCode())
        .orElseThrow(() -> new RuntimeException("학교코드를 정확히 적어주세요."));
    user.setSchoolCode(schoolCode); // 학교코드

    user.setPassword(passwordEncoder.encode(signInDto.getPassword())); // 비밀번호 암호화

    userRepository.save(user);
    return "회원가입이 완료되었습니다.";
  }


  /**
   * 로그인
   */
  @Override
  public String logIn(LogIn loginDto) {

    User registedUser = userRepository.findByEmail(loginDto.getEmail())
        .orElseThrow(() -> new RuntimeException("등록되지 않은 이메일입니다."));

    if (passwordEncoder.matches(loginDto.getPassword(), registedUser.getPassword())) {
      // JWT 생성
      String token = jwtUtil.generateToken(registedUser.getEmail());
      return "로그인되었습니다. 토큰: " + token;
    } else {
      throw new RuntimeException("비밀번호가 일치하지 않습니다.");
    }
  }

  /**
   * 회원정보 수정
   */
  public String updateUser(String email, UserDto.Update updateDto) {

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("등록되지 않은 이메일입니다."));

    // 현재 비밀번호 확인
    if (!passwordEncoder.matches(updateDto.getCurrentPassword(), user.getPassword())) {
      throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
    }

    if (userRepository.findByNickName(updateDto.getNewNickname()).isPresent()) {
      throw new RuntimeException("이미 존재하는 닉네임입니다. 다른 닉네임을 적어주세요");
    }

    String encodedNewPassword = passwordEncoder.encode(updateDto.getNewPassword());
    user.setPassword(encodedNewPassword);
    user.setNickName(updateDto.getNewNickname());
    userRepository.save(user);
    return "회원정보가 성공적으로 수정되었습니다.";
  }

  /**
   * 회원 탈퇴
   */
  public String deleteUser(String email, UserDto.Delete deleteDto) {

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("등록되지 않은 이메일입니다."));

    // 현재 비밀번호 확인
    if (!passwordEncoder.matches(deleteDto.getPassword(), user.getPassword())) {
      throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
    }

    userRepository.delete(user);
    return "탈퇴처리가 완료되었습니다.";
  }


}
