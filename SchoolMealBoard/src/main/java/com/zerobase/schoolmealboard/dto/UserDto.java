package com.zerobase.schoolmealboard.dto;

import com.zerobase.schoolmealboard.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDto {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SignUp {

    @NotNull(message = "핸드폰 번호를 입력하세요.")
    @Size(max = 13, message = "13자리 이상 입력할 수 없습니다.")
    private String phoneNum;

    @Email(message = "이메일 형식에 맞게 입력해주세요")
    @NotBlank(message = "로그인에 사용할 이메일을 입력하세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자리 이상 20자리 이하로 입력해야 합니다.")
    private String password;

    @NotBlank(message = "게시판에서 사용할 닉네임을 입력해주세요.")
    private String nickname;

    @NotBlank(message = "재학중인 학교의 학교코드를 작성해주세요. 학교코드를 모른다면 학교코드 찾기를 누르세요")
    private String schoolCode;


    public static User toEntity(SignUp signUp) {
      return User.builder()
          .phoneNum(signUp.phoneNum)
          .email(signUp.email)
          .password(signUp.password)
          .nickName(signUp.nickname)
          .build();
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Update {

    @NotBlank(message = "사용하던 비밀번호를 입력해주세요.")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자리 이상 20자리 이하로 입력해야 합니다.")
    private String newPassword;

    @NotBlank(message = "새 닉네임을 입력해주세요")
    private String newNickname;


  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Delete {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class LogIn {

    @Email(message = "이메일 형식에 맞게 입력해주세요")
    @NotBlank(message = "이메일을 입력하세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
  }

}
