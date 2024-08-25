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
public class Meal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long mealId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "school_code")
  private School schoolCode;

  private String mealNames;   // 급식 메뉴
  private LocalDate mealDate; // 급식 일자
}

