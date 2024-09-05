package com.zerobase.schoolmealboard.controller;

import com.zerobase.schoolmealboard.component.security.JwtTokenProvider;
import com.zerobase.schoolmealboard.dto.ReviewDto;
import com.zerobase.schoolmealboard.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  /**
   * 리뷰 작성 (로그인 이후 사용 가능)
   */
  @PostMapping("/create")
  public ResponseEntity<ReviewDto> createReview(@RequestHeader("Authorization") String header,
                                                @RequestBody @Valid ReviewDto reviewDto) {
    // JWT 토큰에서 이메일 추출
    String token = header.replace("Bearer ", "");
    String email = jwtTokenProvider.extractUsername(token);

    ReviewDto createdReview = reviewService.createReview(reviewDto, email);

    return (createdReview != null) ?
        ResponseEntity.status(HttpStatus.CREATED).body(createdReview) :
        ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

  }

  /**
   * 리뷰 수정 (작성자 ONLY)
   */
  @PatchMapping("/edit/{reviewId}")
  public ResponseEntity<ReviewDto> editReview(@RequestHeader("Authorization") String header,
                                              @PathVariable Long reviewId,
                                              @RequestBody @Valid ReviewDto.EditReviewDto editReviewDto) {
    // JWT 토큰에서 이메일 추출
    String token = header.replace("Bearer ", "");
    String email = jwtTokenProvider.extractUsername(token);

    ReviewDto editedReview = reviewService.editReview(reviewId, editReviewDto, email);

    return ResponseEntity.ok(editedReview);
  }

  /**
   * 리뷰 삭제 (작성자 ONLY)
   */
  @DeleteMapping("/delete/{reviewId}")
  public ResponseEntity<String> deleteReview(@RequestHeader("Authorization") String header,
                                            @PathVariable Long reviewId) {
    // JWT 토큰에서 이메일 추출
    String token = header.replace("Bearer ", "");
    String email = jwtTokenProvider.extractUsername(token);

    reviewService.deleteReview(reviewId, email);
    return ResponseEntity.ok("리뷰가 삭제되었습니다.");
  }
}
