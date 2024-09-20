package com.zerobase.schoolmealboard.service.impl;

import com.zerobase.schoolmealboard.dto.ReviewDto;
import com.zerobase.schoolmealboard.entity.Meal;
import com.zerobase.schoolmealboard.entity.Review;
import com.zerobase.schoolmealboard.entity.User;
import com.zerobase.schoolmealboard.exceptions.custom.MealNotFoundException;
import com.zerobase.schoolmealboard.exceptions.custom.ReviewNotFoundException;
import com.zerobase.schoolmealboard.exceptions.custom.UnAuthorizedUser;
import com.zerobase.schoolmealboard.exceptions.custom.UserNotFoundException;
import com.zerobase.schoolmealboard.repository.MealRepository;
import com.zerobase.schoolmealboard.repository.ReviewRepository;
import com.zerobase.schoolmealboard.repository.UserRepository;
import com.zerobase.schoolmealboard.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final UserRepository userRepository;
  private final MealRepository mealRepository;
  private final ReviewRepository reviewRepository;

  // 리뷰 작성
  @Override
  @Transactional
  public ReviewDto createReview(ReviewDto reviewDto, String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

    Meal meal = mealRepository.findBySchoolCodeAndMealDate(user.getSchoolCode(),
            reviewDto.getMealDate())
        .orElseThrow(() -> new MealNotFoundException("해당 날짜의 급식이 존재하지 않습니다."));

    Review review = Review.builder()
        .user(user)
        .meal(meal)
        .date(reviewDto.getMealDate())
        .title(reviewDto.getTitle())
        .content(reviewDto.getContent())
        .rating(reviewDto.getRating())
        .imgUrl(reviewDto.getImgUrl())
        .build();
    review = reviewRepository.save(review);

    return toDto(review);
  }

  // 리뷰 수정 (작성자 ONLY)
  @Override
  @Transactional
  public ReviewDto editReview(Long reviewId, ReviewDto.EditReviewDto editReviewDto, String email) {

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException("리뷰 게시글이 존재하지 않습니다."));

    if (!review.getUser().equals(user)) {
      throw new UnAuthorizedUser("게시자만 글을 수정할 수 있습니다.");
    }

    // 수정된 내용
    if (editReviewDto.getTitle() != null && !editReviewDto.getTitle().isEmpty()) {
      review.setTitle(editReviewDto.getTitle());
    }

    if (editReviewDto.getContent() != null && !editReviewDto.getContent().isEmpty()) {
      review.setContent(editReviewDto.getContent());
    }

    if (editReviewDto.getRating() > 0) {
      review.setRating(editReviewDto.getRating());
    }

    if (editReviewDto.getImgUrl() != null && !editReviewDto.getImgUrl().isEmpty()) {
      review.setImgUrl(editReviewDto.getImgUrl());
    }
    review = reviewRepository.save(review);

    return toDto(review);
  }

  // 리뷰 삭제 (작성자 ONLY)
  @Override
  public void deleteReview(Long reviewId, String email) {

    userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException("리뷰 게시글이 존재하지 않습니다."));

    if (!review.getUser().getEmail().equals(email)) {
      throw new UnAuthorizedUser("게시자만 글을 삭제할 수 있습니다.");
    }
    reviewRepository.deleteById(reviewId);

  }

  // 전체 리뷰 최신순 정렬
  @Override
  public Page<ReviewDto> getAllReviews(String email, Pageable pageable) {

    userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

    Pageable sortedByDate = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
        Sort.Direction.DESC, "date");
    return reviewRepository.findAll(sortedByDate).map(this::toDto);
  }

  // 내가 쓴 리뷰 최신순 정렬
  @Override
  public Page<ReviewDto> getAllMyReviews(String email, Pageable pageable) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

    Pageable sortedByDate = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
        Sort.Direction.DESC, "date");

    return reviewRepository.findAllByUser(user, sortedByDate).map(this::toDto);
  }

  // 특정 게시물 조회
  @Override
  public ReviewDto getSpecificReview(Long reviewId, String email) {

    userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException("리뷰 게시글이 존재하지 않습니다."));

    return toDto(review);
  }

  private ReviewDto toDto(Review review) {
    return ReviewDto.builder()
        .mealDate(review.getDate())
        .title(review.getTitle())
        .content(review.getContent())
        .rating(review.getRating())
        .imgUrl(review.getImgUrl())
        .build();
  }

}