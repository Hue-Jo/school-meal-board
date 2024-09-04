package com.zerobase.schoolmealboard.controller;

import com.zerobase.schoolmealboard.component.security.JwtTokenProvider;
import com.zerobase.schoolmealboard.dto.ReviewDto;
import com.zerobase.schoolmealboard.entity.Review;
import com.zerobase.schoolmealboard.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;
  private final JwtTokenProvider jwtTokenProvider;

  @PostMapping("/create")
  public ResponseEntity<ReviewDto> createReview(@RequestHeader("Authorization") String header,
                                            @RequestBody @Valid ReviewDto reviewDto) {
    // JWT 토큰에서 이메일 추출
    String token = header.replace("Bearer ", "");
    String email = jwtTokenProvider.extractUsername(token);

    ReviewDto createdReview = reviewService.createReview(reviewDto, email);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);

  }
}
