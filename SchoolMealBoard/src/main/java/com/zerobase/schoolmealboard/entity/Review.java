package com.zerobase.schoolmealboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reviewId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "meal_id")
  private Meal mealId;

  private LocalDate date; // 날짜
  private String title;   // 제목
  private String content; // 100자평
  private int rating;     // 별점 (1-5점)
  private String imgUrl;  // 이미지 (선택적)

}

