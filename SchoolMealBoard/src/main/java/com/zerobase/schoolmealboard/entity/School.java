package com.zerobase.schoolmealboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class School {

  @Id
  private String schoolCode;     // 학교 코드

  private String schoolName;  // 학교 이름
}
