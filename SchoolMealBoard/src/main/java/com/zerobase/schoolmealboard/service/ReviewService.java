package com.zerobase.schoolmealboard.service;

import com.zerobase.schoolmealboard.dto.ReviewDto;
import com.zerobase.schoolmealboard.entity.Review;

public interface ReviewService {

  // 리뷰 작성
  Review createReview(ReviewDto reviewDto, String email);

  // 리뷰 조회 (전체 / 작성자 닉네임 /페이징 처리/ 최신순 정렬)
  // 리뷰 수정 (작성자만)
  // 리뷰 삭제 (작성자만)




}
