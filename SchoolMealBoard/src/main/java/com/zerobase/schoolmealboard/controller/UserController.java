package com.zerobase.schoolmealboard.controller;

import com.zerobase.schoolmealboard.component.security.JwtFilter;
import com.zerobase.schoolmealboard.component.security.JwtUtil;
import com.zerobase.schoolmealboard.dto.UserDto;
import com.zerobase.schoolmealboard.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  private final JwtUtil jwtUtil;


  //
  @PostMapping("/sign-up")
  public ResponseEntity<String> signUp(@RequestBody @Valid UserDto.SignUp signUpDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.signUp(signUpDto));
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody @Valid UserDto.LogIn loginDto) {
    return ResponseEntity.status(HttpStatus.OK).body(userService.logIn(loginDto));
  }

  @PatchMapping("/update")
  public ResponseEntity<String> update(@RequestHeader("Authorization") String header,
                                      @RequestBody @Valid UserDto.Update updateDto) {
    // JWT 토큰에서 이메일 추출
    String token = header.replace("Bearer ", "");
    String email = jwtUtil.extractUsername(token);

    // 사용자 정보 업데이트
    try {
      String result = userService.updateUser(email, updateDto);
      return ResponseEntity.ok(result);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @DeleteMapping("/delete")
  public ResponseEntity<String> delete(@RequestHeader("Authorization") String header,
                                      @RequestBody @Valid UserDto.Delete deleteDto) {
    String token = header.replace("Bearer ", "");
    String email = jwtUtil.extractUsername(token);

    try{
      String result = userService.deleteUser(email, deleteDto);
      return ResponseEntity.ok(result);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }
}
