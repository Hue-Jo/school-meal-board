package com.zerobase.schoolmealboard.repository;

import com.zerobase.schoolmealboard.entity.Meal;
import com.zerobase.schoolmealboard.entity.School;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MealRepository extends JpaRepository<Meal, Long> {

  Optional<Meal> findBySchoolCodeAndMealDate(School schoolCode, LocalDate mealDate);

  @Query(value = "DELETE meal WHERE meal_date BETWEEN :startDate AND :endDate", nativeQuery = true)
  void deleteByMealDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
