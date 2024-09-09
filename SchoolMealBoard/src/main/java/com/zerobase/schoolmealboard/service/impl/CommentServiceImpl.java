package com.zerobase.schoolmealboard.service.impl;

import com.zerobase.schoolmealboard.dto.CommentDto;
import com.zerobase.schoolmealboard.entity.Comment;
import com.zerobase.schoolmealboard.entity.Review;
import com.zerobase.schoolmealboard.entity.User;
import com.zerobase.schoolmealboard.exceptions.custom.ReviewNotFoundException;
import com.zerobase.schoolmealboard.exceptions.custom.UserNotFoundException;
import com.zerobase.schoolmealboard.repository.CommentRepository;
import com.zerobase.schoolmealboard.repository.LikesRepository;
import com.zerobase.schoolmealboard.repository.ReviewRepository;
import com.zerobase.schoolmealboard.repository.UserRepository;
import com.zerobase.schoolmealboard.service.CommentService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final ReviewRepository reviewRepository;


  @Override
  @Transactional
  public CommentDto createComment(Long reviewId, String content, String email) {

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException("해당 리뷰가 존재하지 않습니다."));

    if (!user.getSchoolCode().equals(review.getUserId().getSchoolCode())) {
      throw new RuntimeException("같은 학교 학생의 리뷰에만 댓글을 달 수 있습니다.");
    }

    Comment comment = Comment.builder()
        .reviewId(review)
        .userId(user)
        .content(content)
        .liked(0) // 초기 공감수 0개
        .build();

    Comment savedComment = commentRepository.save(comment);

    return new CommentDto(savedComment);
  }


  // 댓글 조회 (작성 날짜순)
  public List<CommentDto> getCommentsByCreatedDate(Long reviewId, String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException("해당 리뷰가 존재하지 않습니다."));

    List<Comment> comments = commentRepository.findByReviewIdOrderByCreatedDateAsc(review);

    return comments.stream()
        .map(comment -> CommentDto.builder()
            .commentId(comment.getCommentId())
            .reviewId(comment.getReviewId().getReviewId())
            .content(comment.getContent())
            .nickName(comment.getUserId().getNickName())
            .likes(comment.getLiked())
            .build())
        .collect(Collectors.toList());

  }

  // 댓글 조회 (공감순)
  public List<CommentDto> getCommentsByLikes(Long reviewId, String email) {

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException("해당 리뷰가 존재하지 않습니다."));

    List<Comment> comments = commentRepository.findByReviewIdOrderByLikedDesc(review);

    return comments.stream()
        .map(comment -> (CommentDto.builder()
            .commentId(comment.getCommentId())
            .reviewId(comment.getReviewId().getReviewId())
            .content(comment.getContent())
            .nickName(comment.getUserId().getNickName())
            .likes(comment.getLiked())
            .build()))
        .collect(Collectors.toList());
  }


}
