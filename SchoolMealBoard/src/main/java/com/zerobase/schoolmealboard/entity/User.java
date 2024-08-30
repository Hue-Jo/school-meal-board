package com.zerobase.schoolmealboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "school_code")
  private School schoolCode;

  private String phoneNum;  // 연락처
  private String email;     // 이메일
  private String password;  // 비밀번호
  private String nickName;  // 닉네임

  private LocalDate banEndDate;   // 이용정지 종료일

}
