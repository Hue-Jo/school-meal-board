package com.zerobase.schoolmealboard.repository;

import com.zerobase.schoolmealboard.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<Meal, Long> {

}
