package com.zerobase.schoolmealboard.service;

import com.zerobase.schoolmealboard.dto.ReviewDto;

public interface ReviewService {

  // 리뷰 작성
  ReviewDto createReview(ReviewDto reviewDto, String email);

  // 리뷰 수정 (작성자만)
  ReviewDto editReview(Long reviewId, ReviewDto.EditReviewDto editReviewDto, String email);

  // 리뷰 삭제 (작성자만)
  void deleteReview(Long reviewId, String email);

  // 리뷰 조회 (전체 / 작성자 닉네임 /페이징 처리/ 최신순 정렬)



}
