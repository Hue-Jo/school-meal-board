package com.zerobase.schoolmealboard.service.impl;

import com.zerobase.schoolmealboard.dto.UserDto;
import com.zerobase.schoolmealboard.dto.UserDto.LogIn;
import com.zerobase.schoolmealboard.dto.UserDto.SignUp;
import com.zerobase.schoolmealboard.dto.UserDto.Update;
import com.zerobase.schoolmealboard.entity.School;
import com.zerobase.schoolmealboard.entity.User;
import com.zerobase.schoolmealboard.repository.SchoolRepository;
import com.zerobase.schoolmealboard.repository.UserRepository;
import com.zerobase.schoolmealboard.service.UserService;
import java.util.Optional;
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
   * 로그인
   */
  @Override
  public String logIn(LogIn logInDto) {

    Optional<User> userOpt = userRepository.findByEmail(logInDto.getEmail());

    if (userOpt.isEmpty()) {
      return "등록되지 않은 이메일입니다. 회원가입하시겠습니까?";
    }

    User user = userOpt.get();

    if (!passwordEncoder.matches(logInDto.getPassword(), user.getPassword())) {
      return "비밀번호가 일치하지 않습니다.";
    }

    return "로그인 되었습니다. ";
  }


  /**
   * 회원정보 수정
   */
  @Override
  public String updateUser(Update updateDto, String email) {

    User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("등록되지 않은 이메일"));

    if (!passwordEncoder.matches(updateDto.getCurrentPassword(), user.getPassword())) {
      return "비밀번호가 일치하지 않습니다.";
    }

    if (passwordEncoder.matches(updateDto.getNewPassword(), user.getPassword())) {
      return "현재의 비밀번호와 동일한 비밀번호로는 수정할 수 없습니다. 새로운 비밀번호를 등록해주세요";
    }

    if (userRepository.findByNickName(updateDto.getNewNickname()).isPresent()) {
      return "이미 사용중인 닉네임입니다.";
    }

    return "회원 정보가 수정되었습니다.";
  }

  /**
   * 회원 탈퇴
   */


}
