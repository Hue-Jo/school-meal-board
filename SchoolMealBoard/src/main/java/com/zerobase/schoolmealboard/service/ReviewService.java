package com.zerobase.schoolmealboard.service;

import com.zerobase.schoolmealboard.dto.ReviewDto;
import com.zerobase.schoolmealboard.entity.Review;
import org.springframework.http.ResponseEntity;

public interface ReviewService {

  Review create(ReviewDto reviewDto);

}
