package com.zerobase.schoolmealboard.repository;

import com.zerobase.schoolmealboard.entity.Meal;
import com.zerobase.schoolmealboard.entity.School;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<Meal, Long> {

  // 특정 학교와 날짜에 해당하는 급식 정보가 존재하는지 확인
  boolean existsBySchoolCodeAndMealDate(School schoolCode, LocalDate mealDate);

}
