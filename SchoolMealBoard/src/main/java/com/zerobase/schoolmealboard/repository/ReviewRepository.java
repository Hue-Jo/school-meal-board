package com.zerobase.schoolmealboard.repository;

import com.zerobase.schoolmealboard.entity.Review;
import com.zerobase.schoolmealboard.entity.School;
import com.zerobase.schoolmealboard.entity.User;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  Page<Review> findAllByUser(User user, Pageable pageable);

  @Query("select avg(r.rating) from Review r where r.meal.schoolCode = :schoolCode and r.meal.mealDate = :mealDate")
  Double findAverageRatingBySchoolCodeAndMealDate(@Param("schoolCode") School school, @Param("mealDate") LocalDate mealDate);
}
