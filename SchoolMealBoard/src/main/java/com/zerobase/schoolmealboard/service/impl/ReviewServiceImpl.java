package com.zerobase.schoolmealboard.service.impl;

import com.zerobase.schoolmealboard.dto.ReviewDto;
import com.zerobase.schoolmealboard.entity.Meal;
import com.zerobase.schoolmealboard.entity.Review;
import com.zerobase.schoolmealboard.entity.User;
import com.zerobase.schoolmealboard.exceptions.custom.MealNotFoundException;
import com.zerobase.schoolmealboard.exceptions.custom.ReviewNotFoundException;
import com.zerobase.schoolmealboard.exceptions.custom.UserNotFoundException;
import com.zerobase.schoolmealboard.repository.MealRepository;
import com.zerobase.schoolmealboard.repository.ReviewRepository;
import com.zerobase.schoolmealboard.repository.UserRepository;
import com.zerobase.schoolmealboard.service.ReviewService;
import java.nio.file.AccessDeniedException;
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
  public ReviewDto createReview(ReviewDto reviewDto, String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

    Meal meal = mealRepository.findBySchoolCodeAndMealDate(user.getSchoolCode(), reviewDto.getMealDate())
        .orElseThrow(() -> new MealNotFoundException("해당 날짜의 급식이 존재하지 않습니다."));

    Review review = Review.builder()
        .userId(user)
        .mealId(meal)
        .date(reviewDto.getMealDate())
        .title(reviewDto.getTitle())
        .content(reviewDto.getContent())
        .rating(reviewDto.getRating())
        .imgUrl(reviewDto.getImgUrl())
        .build();
    review = reviewRepository.save(review);

    return ReviewDto.builder()
        .mealDate(review.getDate())
        .title(review.getTitle())
        .content(review.getContent())
        .rating(review.getRating())
        .imgUrl(review.getImgUrl())
        .build();
  }

  @Override
  @Transactional
  public ReviewDto editReview(Long reviewId, ReviewDto.EditReviewDto editReviewDto, String email) {

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException("리뷰 게시글이 존재하지 않습니다."));

    if (!review.getUserId().equals(user)) {
      throw new RuntimeException("게시자 당사자만 수정할 수 있습니다.");
    }

    // 수정된 내용
    if (editReviewDto.getTitle() != null) {
      review.setTitle(editReviewDto.getTitle());
    }
    if (editReviewDto.getContent() != null) {
      review.setContent(editReviewDto.getContent());
    }
    if (editReviewDto.getRating() > 0) {
      review.setRating(editReviewDto.getRating());
    }
    if (editReviewDto.getImgUrl() != null) {
      review.setImgUrl(editReviewDto.getImgUrl());
    }
    // 리뷰 수정본 저장
    review = reviewRepository.save(review);

    return ReviewDto.builder()
        .mealDate(review.getDate())
        .title(review.getTitle())
        .content(review.getContent())
        .rating(review.getRating())
        .imgUrl(review.getImgUrl())
        .build();
  }

}