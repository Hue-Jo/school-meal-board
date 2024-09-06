package com.zerobase.schoolmealboard.controller;

import com.zerobase.schoolmealboard.component.security.JwtTokenProvider;
import com.zerobase.schoolmealboard.dto.ReviewDto;
import com.zerobase.schoolmealboard.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  public ResponseEntity<ReviewDto> createReview(
      @RequestBody @Valid ReviewDto reviewDto) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    ReviewDto createdReview = reviewService.createReview(reviewDto, email);

    return (createdReview != null) ?
        ResponseEntity.status(HttpStatus.CREATED).body(createdReview) :
        ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

  }

  /**
   * 리뷰 수정 (작성자 ONLY)
   */
  @PatchMapping("/edit/{id}")
  public ResponseEntity<ReviewDto> editReview(
      @PathVariable Long id,
      @RequestBody @Valid ReviewDto.EditReviewDto editReviewDto) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    ReviewDto editedReview = reviewService.editReview(id, editReviewDto, email);

    return ResponseEntity.ok(editedReview);
  }

  /**
   * 리뷰 삭제 (작성자 ONLY)
   */
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<String> deleteReview(
      @RequestHeader("Authorization") String header,
      @PathVariable Long id) {

    String token = header.replace("Bearer ", "");
    String email = jwtTokenProvider.extractUsername(token);

    reviewService.deleteReview(id, email);
    return ResponseEntity.ok("리뷰가 삭제되었습니다.");
  }

  /**
   * 리뷰 전체 조회 (최신순 정렬, 페이징처리)
   */
  @GetMapping
  public ResponseEntity<Page<ReviewDto>> getAllReviews(
      @RequestParam(defaultValue = "0") int page, // 기본 페이지 번호 0
      @RequestParam(defaultValue = "20") int size) { // 기본 페이지 사이즈 20

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
    Page<ReviewDto> reviews = reviewService.getAllReviews(email, pageable);
    return ResponseEntity.ok(reviews);
  }

  /**
   * 내가 쓴 리뷰 조회 (최신순 정렬, 페이징처리)
   */
  @GetMapping("/my-review")
  public ResponseEntity<Page<ReviewDto>> getAllMyReviews(
      @RequestParam(defaultValue = "0") int page, // 기본 페이지 번호 0
      @RequestParam(defaultValue = "20") int size) { // 기본 페이지 사이즈 20

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
    Page<ReviewDto> reviews = reviewService.getAllMyReviews(email, pageable);
    return ResponseEntity.ok(reviews);
  }

  /**
   * 특정 리뷰 조회
   */
  @GetMapping("/{id}")
  public ResponseEntity<ReviewDto> getSpecificReview(
      @PathVariable Long id) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    ReviewDto review = reviewService.getSpecificReview(id, email);
    return ResponseEntity.ok(review);
  }
}