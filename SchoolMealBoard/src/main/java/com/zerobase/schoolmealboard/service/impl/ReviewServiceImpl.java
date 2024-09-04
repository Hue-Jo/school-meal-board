package com.zerobase.schoolmealboard.service.impl;

import com.zerobase.schoolmealboard.dto.ReviewDto;
import com.zerobase.schoolmealboard.entity.Meal;
import com.zerobase.schoolmealboard.entity.Review;
import com.zerobase.schoolmealboard.entity.User;
import com.zerobase.schoolmealboard.repository.MealRepository;
import com.zerobase.schoolmealboard.repository.ReviewRepository;
import com.zerobase.schoolmealboard.repository.UserRepository;
import com.zerobase.schoolmealboard.service.MealService;
import com.zerobase.schoolmealboard.service.ReviewService;
import com.zerobase.schoolmealboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final UserRepository userRepository;
  private final MealRepository mealRepository;
  private final ReviewRepository reviewRepository;

  @Override
  @Transactional
  public Review createReview(ReviewDto reviewDto, String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

    Meal meal = mealRepository.findBySchoolCodeAndMealDate(user.getSchoolCode(), reviewDto.getMealDate())
        .orElseThrow(() -> new RuntimeException("해당 날짜의 급식이 존재하지 않습니다."));

    Review review = Review.builder()
        .userId(user)
        .mealId(meal)
        .date(reviewDto.getMealDate())
        .title(reviewDto.getTitle())
        .content(reviewDto.getContent())
        .rating(reviewDto.getRating())
        .imgUrl(reviewDto.getImgUrl())
        .build();

    return reviewRepository.save(review);
  }
}