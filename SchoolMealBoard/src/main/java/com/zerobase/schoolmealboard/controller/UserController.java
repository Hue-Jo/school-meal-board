package com.zerobase.schoolmealboard.controller;

import com.zerobase.schoolmealboard.component.security.JwtTokenProvider;
import com.zerobase.schoolmealboard.dto.UserDto;
import com.zerobase.schoolmealboard.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final JwtTokenProvider jwtProvider;


  //
  @PostMapping("/sign-up")
  public ResponseEntity<String> signUp(@RequestBody @Valid UserDto.SignUp signUpDto) {
    try {
      userService.signUp(signUpDto);
      return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody @Valid UserDto.LogIn loginDto) {
    try {
      String token = userService.logIn(loginDto);
      return ResponseEntity.status(HttpStatus.OK).body("로그인되었습니다. 토큰: " + token);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @PatchMapping("/my-profile")
  public ResponseEntity<String> update(@RequestBody @Valid UserDto.Update updateDto) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    // 사용자 정보 업데이트
    try {
      userService.updateUser(email, updateDto);
      return ResponseEntity.ok("회원정보가 성공적으로 수정되었습니다.");
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @DeleteMapping("/my-profile")
  public ResponseEntity<String> delete(@RequestBody @Valid UserDto.Delete deleteDto) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    try {
      userService.deleteUser(email, deleteDto);
      return ResponseEntity.ok("탈퇴처리가 완료되었습니다.");
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }
}