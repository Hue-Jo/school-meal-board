package com.zerobase.schoolmealboard.controller;

import com.zerobase.schoolmealboard.dto.UserDto;
import com.zerobase.schoolmealboard.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  //
  @PostMapping("/sign-up")
  public ResponseEntity<String> signUp(@RequestBody @Valid UserDto.SignUp signUpDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
                          .body(userService.signUp(signUpDto));
  }
}
