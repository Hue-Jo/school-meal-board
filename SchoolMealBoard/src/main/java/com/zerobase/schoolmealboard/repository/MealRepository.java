package com.zerobase.schoolmealboard.repository;

import com.zerobase.schoolmealboard.entity.Meal;
import com.zerobase.schoolmealboard.entity.School;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<Meal, Long> {

  Optional<Meal> findBySchoolCodeAndMealDate(School schoolCode, LocalDate mealDate);

  void deleteByMealDateBetween(LocalDate startDate, LocalDate endDate);

}
