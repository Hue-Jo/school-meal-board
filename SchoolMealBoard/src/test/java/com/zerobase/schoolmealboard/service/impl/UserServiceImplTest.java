package com.zerobase.schoolmealboard.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.zerobase.schoolmealboard.component.security.JwtTokenProvider;
import com.zerobase.schoolmealboard.dto.UserDto.Delete;
import com.zerobase.schoolmealboard.dto.UserDto.LogIn;
import com.zerobase.schoolmealboard.dto.UserDto.SignUp;
import com.zerobase.schoolmealboard.dto.UserDto.Update;
import com.zerobase.schoolmealboard.entity.School;
import com.zerobase.schoolmealboard.entity.User;
import com.zerobase.schoolmealboard.repository.SchoolRepository;
import com.zerobase.schoolmealboard.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private SchoolRepository schoolRepository;

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @InjectMocks
  private UserServiceImpl userService;

  private SignUp signUpDto;
  private LogIn logInDto;
  private Update updateDto;
  private Delete deleteDto;
  private User user;

  @BeforeEach
  void setUp() {
    signUpDto = SignUp.builder()
        .phoneNum("010-1111-1111")
        .email("test1@mail.com")
        .nickname("가")
        .password("11111111")
        .schoolCode("000000")
        .build();

    logInDto = LogIn.builder()
        .email("test1@mail.com")
        .password("11111111")
        .build();

    updateDto = Update.builder()
        .currentPassword("11111111")
        .newPassword("newPassword")
        .newNickname("newNickname")
        .build();
  }

  @Test
  @DisplayName("회원가입 실패_이미 등록된 핸드폰 번호")
  void signUpFailureDuplicatePhoneNum() {
    // given
    given(userRepository.findByPhoneNum(signUpDto.getPhoneNum()))
        .willReturn(Optional.of(new User()));

    // when & then
    assertThrows(RuntimeException.class, () -> userService.signUp(signUpDto));
  }

  @Test
  @DisplayName("회원가입 성공")
  void signUpSuccess() {
    // given
    given(userRepository.findByPhoneNum(signUpDto.getPhoneNum()))
        .willReturn(Optional.empty());
    given(userRepository.findByEmail(signUpDto.getEmail()))
        .willReturn(Optional.empty());
    given(userRepository.findByNickName(signUpDto.getNickname()))
        .willReturn(Optional.empty());
    given(schoolRepository.findById(signUpDto.getSchoolCode()))
        .willReturn(Optional.of(new School("000000", "테스트학교")));
    given(passwordEncoder.encode(signUpDto.getPassword()))
        .willReturn("encodedPassword");

    // when
    userService.signUp(signUpDto);

    // then
    verify(userRepository).save(any(User.class));
  }


  @Test
  @DisplayName("로그인 실패_등록되지 않은 이메일")
  void logInFailInvalidEmail() {
    //given
    given(userRepository.findByEmail(logInDto.getEmail()))
        .willReturn(Optional.empty());
    //when
    //then
    assertThrows(RuntimeException.class, () -> userService.logIn(logInDto));

  }

  @Test
  @DisplayName("로그인 실패_잘못된 비밀번호")
  void logInFailInvalidPassword() {
    //given
    User registeredUser = User.builder()
        .email(logInDto.getEmail())
        .password("encodedPassword")
        .build();

    given(userRepository.findByEmail(logInDto.getEmail()))
        .willReturn(Optional.of(registeredUser));
    given(passwordEncoder.matches(logInDto.getPassword(), registeredUser.getPassword()))
        .willReturn(false);

    //when & then
    assertThrows(RuntimeException.class, () -> userService.logIn(logInDto));
  }

  @Test
  @DisplayName("로그인 성공")
  void logInSuccess() {
    //given
    User registeredUser = User.builder()
        .email(logInDto.getEmail())
        .password("encodedPassword")
        .build();

    given(userRepository.findByEmail(logInDto.getEmail()))
        .willReturn(Optional.of(registeredUser));
    given(passwordEncoder.matches(logInDto.getPassword(), registeredUser.getPassword()))
        .willReturn(true);
    given(jwtTokenProvider.generateToken(logInDto.getEmail()))
        .willReturn("jwtToken");

    //when
    String token = userService.logIn(logInDto);

    //then
    assertEquals("jwtToken", token);
    verify(jwtTokenProvider).generateToken(logInDto.getEmail());
  }

  @Test
  @DisplayName("회원정보 수정 실패_이미 존재하는 닉네임")
  void editFail() {
    //given

    User user = User.builder()
        .email(logInDto.getEmail())
        .password("encodedPassword")
        .nickName("oldNickName")
        .build();

    given(userRepository.findByEmail(logInDto.getEmail())).willReturn(Optional.of(user));
    given(passwordEncoder.matches(logInDto.getPassword(), user.getPassword()))
        .willReturn(true);
    given(userRepository.findByNickName(updateDto.getNewNickname()))
        .willReturn(Optional.of(new User()));
    //when
    //then
    assertThrows(RuntimeException.class, () -> userService.updateUser(logInDto.getEmail(), updateDto));
  }

  @Test
  @DisplayName("회원정보 수정 성공")
  void editSuccess() {
    //given
    User user = User.builder()
        .email(logInDto.getEmail())
        .password("encodedPassword")
        .nickName("oldNickName")
        .build();

    given(userRepository.findByEmail(logInDto.getEmail())).willReturn(Optional.of(user));
    given(passwordEncoder.matches(logInDto.getPassword(), user.getPassword()))
        .willReturn(true);
    given(userRepository.findByNickName(updateDto.getNewNickname()))
        .willReturn(Optional.empty());
    given(passwordEncoder.encode(updateDto.getNewPassword()))
        .willReturn("encodedNewPassword");
    //when
    //then

    userService.updateUser(logInDto.getEmail(), updateDto);
    assertEquals("encodedNewPassword", user.getPassword());
    assertEquals(updateDto.getNewNickname(), user.getNickName());
  }

  @Test
  @DisplayName("회원탈퇴 실패_비밀번호 불일치")
  void deleteFailInvalidPw() {
    //given
    deleteDto = Delete.builder()
        .password("wrongPw")
        .build();
    User user = User.builder()
        .email(logInDto.getEmail())
        .password("encodedPassword")
        .build();

    given(userRepository.findByEmail(logInDto.getEmail())).willReturn(Optional.of(user));
    given(passwordEncoder.matches(deleteDto.getPassword(), user.getPassword()))
        .willReturn(false);
    //when
    //then
    assertThrows(RuntimeException.class, () -> userService.deleteUser(logInDto.getEmail(), deleteDto));
  }

  @Test
  @DisplayName("회원탈퇴 성공")
  void deleteSuccess() {
    //given
    User user = User.builder()
        .email(logInDto.getEmail())
        .password("encodedPassword")
        .build();

    deleteDto = Delete.builder()
        .password(logInDto.getPassword())
        .build();

    given(userRepository.findByEmail(logInDto.getEmail()))
        .willReturn(Optional.of(user));
    given(passwordEncoder.matches(deleteDto.getPassword(), user.getPassword()))
        .willReturn(true);
    //when
    userService.deleteUser(logInDto.getEmail(), deleteDto);
    //then
    verify(userRepository).delete(user);
  }
}