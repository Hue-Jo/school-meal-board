package com.zerobase.schoolmealboard.controller;

import com.zerobase.schoolmealboard.entity.Meal;
import com.zerobase.schoolmealboard.service.MealService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meal")
@RequiredArgsConstructor
public class MealController {

  private final MealService mealService;

  // 특정 학교코드와 특정날짜 입력 후 급식 정보 조회
  @GetMapping("/{schoolCode}/{date}")
  public ResponseEntity<List<Meal>> getMeals(
      @PathVariable String schoolCode,
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

    List<Meal> meals = mealService.getMealBySchoolCodeAndDate(schoolCode, date);
    return ResponseEntity.ok(meals);
  }


  // 특정 학교코드와 특정날짜 입력 후 해당급식의 평균별점 조회
  @GetMapping("/{schoolCode}/{date}/average-rating")
  public ResponseEntity<Double> getAverageRating(
      @PathVariable String schoolCode,
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

    double averageRating = mealService.getAverageRating(schoolCode, date);
    return ResponseEntity.ok(averageRating);
  }

}
