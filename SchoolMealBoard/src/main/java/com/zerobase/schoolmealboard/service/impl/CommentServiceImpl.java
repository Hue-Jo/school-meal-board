package com.zerobase.schoolmealboard.service.impl;

import com.zerobase.schoolmealboard.dto.CommentDto;
import com.zerobase.schoolmealboard.entity.Comment;
import com.zerobase.schoolmealboard.entity.Review;
import com.zerobase.schoolmealboard.entity.User;
import com.zerobase.schoolmealboard.exceptions.custom.CommentNotFoundException;
import com.zerobase.schoolmealboard.exceptions.custom.ReviewNotFoundException;
import com.zerobase.schoolmealboard.exceptions.custom.UnAuthorizedUser;
import com.zerobase.schoolmealboard.exceptions.custom.UserNotFoundException;
import com.zerobase.schoolmealboard.repository.CommentRepository;
import com.zerobase.schoolmealboard.repository.ReviewRepository;
import com.zerobase.schoolmealboard.repository.UserRepository;
import com.zerobase.schoolmealboard.service.CommentService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final ReviewRepository reviewRepository;


  // 댓글 작성
  @Override
  @Transactional
  public CommentDto createComment(Long reviewId, String content, String email) {

    User user = validateUser(email);
    Review review = validateReview(reviewId);

    if (!user.getSchoolCode().equals(review.getUser().getSchoolCode())) {
      throw new RuntimeException("같은 학교 학생의 리뷰에만 댓글을 달 수 있습니다.");
    }

    Comment comment = Comment.builder()
        .review(review)
        .user(user)
        .content(content)
        .liked(0) // 초기 공감수 0개
        .build();

    Comment savedComment = commentRepository.save(comment);

    return new CommentDto(savedComment);
  }

  // 댓글 수정
  @Override
  @Transactional
  public CommentDto editComment(Long commentId, String content, String email) {
    User user = validateUser(email);
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException("해당 댓글이 존재하지 않습니다."));

    if (!comment.getUser().getUserId().equals(user.getUserId())) {
      throw new UnAuthorizedUser("댓글 작성자만 댓글을 수정할 수 있습니다.");
    }

    comment.setContent(content);
    Comment editedComment = commentRepository.save(comment);
    return new CommentDto(editedComment);
  }

  // 댓글 삭제
  @Override
  @Transactional
  public void deleteComment(Long commentId, String email) {

    userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException("해당 댓글이 존재하지 않습니다."));

    if (!comment.getUser().getEmail().equals(email)) {
      throw new UnAuthorizedUser("댓글 작성자만 댓글을 삭제할 수 있습니다.");
    }

    commentRepository.deleteById(commentId);

  }

  // 댓글 조회 (작성 날짜순)
  @Transactional(readOnly = true)
  public Page<CommentDto> getCommentsByCreatedDate(Long reviewId, String email, Pageable pageable) {
    User user = validateUser(email);
    Review review = validateReview(reviewId);

    Page<Comment> comments = commentRepository.findByReviewOrderByCreatedDateTimeAsc(review, pageable);

    return comments.map(CommentDto::new);

  }

  // 댓글 조회 (공감순)
  @Transactional(readOnly = true)
  public Page<CommentDto> getCommentsByLikes(Long reviewId, String email, Pageable pageable) {
    User user = validateUser(email);
    Review review = validateReview(reviewId);

    Page<Comment> comments = commentRepository.findByReviewOrderByLikedDesc(review, pageable);

    return comments.map(CommentDto::new);

  }


  private User validateUser(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));
  }

  private Review validateReview(Long reviewId) {
    return reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException("해당 리뷰가 존재하지 않습니다."));
  }
}
