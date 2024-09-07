package com.zerobase.schoolmealboard.service;

import com.zerobase.schoolmealboard.dto.ReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

  // 리뷰 작성
  ReviewDto createReview(ReviewDto reviewDto, String email);

  // 리뷰 수정 (작성자만)
  ReviewDto editReview(Long reviewId, ReviewDto.EditReviewDto editReviewDto, String email);

  // 리뷰 삭제 (작성자만)
  void deleteReview(Long reviewId, String email);

  // 전체 리뷰 조회 (페이징 처리/ 최신순 정렬)
  Page<ReviewDto> getAllReviews(String email, Pageable pageable);

  // 내가 쓴 리뷰 조회 (페이징 처리)
  Page<ReviewDto> getAllMyReviews(String email, Pageable pageable);

  // 특정 리뷰 조회
  ReviewDto getSpecificReview(Long reviewId, String email);


}
