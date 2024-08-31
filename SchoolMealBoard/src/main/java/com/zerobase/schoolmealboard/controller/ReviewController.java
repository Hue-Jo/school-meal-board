package com.zerobase.schoolmealboard.controller;


import com.zerobase.schoolmealboard.dto.ReviewDto;
import com.zerobase.schoolmealboard.entity.Review;
import com.zerobase.schoolmealboard.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping("/create")
  public ResponseEntity<Review> createReview(@RequestBody ReviewDto reviewDto) {
    Review created = reviewService.create(reviewDto);
    return (created == null) ? ResponseEntity.status(HttpStatus.BAD_REQUEST).build() : ResponseEntity.ok(created);
  }

  @GetMapping("/all-reviews")
  public ResponseEntity<Review> getAllReviews() {
    return null;
  }
}
