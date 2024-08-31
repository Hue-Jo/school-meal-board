package com.zerobase.schoolmealboard.service.impl;

import com.zerobase.schoolmealboard.dto.ReviewDto;
import com.zerobase.schoolmealboard.entity.Review;
import com.zerobase.schoolmealboard.repository.ReviewRepository;
import com.zerobase.schoolmealboard.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final ReviewRepository reviewRepository;

  @Override
  public Review create(ReviewDto reviewDto) {
    Review review = ReviewDto.toEntity(reviewDto);
    if (review.getReviewId() != null) {
      return null;
    }
    return reviewRepository.save(review);
  }
}
