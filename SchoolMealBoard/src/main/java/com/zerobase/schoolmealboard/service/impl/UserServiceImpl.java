package com.zerobase.schoolmealboard.service.impl;

import com.zerobase.schoolmealboard.component.security.JwtTokenProvider;
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
  private final JwtTokenProvider jwtUtil;

  /**
   * 회원가입
   */
  @Override
  public void signUp(SignUp signUpDto) {

    if (userRepository.findByPhoneNum(signUpDto.getPhoneNum()).isPresent()) {
      throw new RuntimeException("중복 가입은 불가능합니다.");
    }
    if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
      throw new RuntimeException("이미 사용중인 이메일입니다.");
    }
    if (userRepository.findByNickName(signUpDto.getNickname()).isPresent()) {
      throw new RuntimeException("이미 사용중인 닉네임입니다.");
    }

    User user = UserDto.SignUp.toEntity(signUpDto);
    School schoolCode = schoolRepository.findById(signUpDto.getSchoolCode())
        .orElseThrow(() -> new RuntimeException("학교코드를 정확히 적어주세요."));
    user.setSchoolCode(schoolCode);
    user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

    userRepository.save(user);
  }


  /**
   * 로그인
   */
  @Override
  public String logIn(LogIn loginDto) {

    User registeredUser = userRepository.findByEmail(loginDto.getEmail())
        .orElseThrow(() -> new RuntimeException("등록되지 않은 이메일입니다."));

    if (passwordEncoder.matches(loginDto.getPassword(), registeredUser.getPassword())) {
      return jwtUtil.generateToken(registeredUser.getEmail());
    } else {
      throw new RuntimeException("비밀번호가 일치하지 않습니다.");
    }
  }

  /**
   * 회원정보 수정
   */
  public void updateUser(String email, UserDto.Update updateDto) {

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("등록되지 않은 이메일입니다."));

    if (!passwordEncoder.matches(updateDto.getCurrentPassword(), user.getPassword())) {
      throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
    }

    if (userRepository.findByNickName(updateDto.getNewNickname()).isPresent()) {
      throw new RuntimeException("이미 존재하는 닉네임입니다. 다른 닉네임을 적어주세요.");
    }

    user.setPassword(passwordEncoder.encode(updateDto.getNewPassword()));
    user.setNickName(updateDto.getNewNickname());
    userRepository.save(user);
  }

  /**
   * 회원 탈퇴
   */
  public void deleteUser(String email, UserDto.Delete deleteDto) {

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("등록되지 않은 이메일입니다."));

    if (!passwordEncoder.matches(deleteDto.getPassword(), user.getPassword())) {
      throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
    }

    userRepository.delete(user);
  }


}
