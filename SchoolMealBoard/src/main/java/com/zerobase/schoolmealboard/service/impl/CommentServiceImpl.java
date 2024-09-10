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

    User user = validateUser(email);
    Review review = validateReview(reviewId);

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

  @Transactional
  public CommentDto editComment(Long commentId, String content, String email) {
    User user = validateUser(email);
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException("해당 댓글이 존재하지 않습니다."));

    if (!comment.getUserId().getUserId().equals(user.getUserId())) {
      throw new UnAuthorizedUser("댓글 작성자만 댓글을 수정할 수 있습니다.");
    }

    comment.setContent(content);
    Comment editedComment = commentRepository.save(comment);
    return new CommentDto(editedComment);
  }

  @Transactional
  public void deleteComment(Long commentId, String email) {

    userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

    Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException("해당 댓글이 존재하지 않습니다."));

    if (!comment.getUserId().getEmail().equals(email)) {
      throw new UnAuthorizedUser("댓글 작성자만 댓글을 삭제할 수 있습니다.");
    }

    commentRepository.deleteById(commentId);

  }

  // 댓글 조회 (작성 날짜순)
  @Transactional(readOnly = true)
  public List<CommentDto> getCommentsByCreatedDate(Long reviewId, String email) {
    User user = validateUser(email);
    Review review = validateReview(reviewId);
    List<Comment> comments = commentRepository.findByReviewIdOrderByCreatedDateAsc(review);

    return comments.stream()
        .map(CommentDto::new)
        .collect(Collectors.toList());

  }

  // 댓글 조회 (공감순)
  @Transactional(readOnly = true)
  public List<CommentDto> getCommentsByLikes(Long reviewId, String email) {

    User user = validateUser(email);
    Review review = validateReview(reviewId);
    List<Comment> comments = commentRepository.findByReviewIdOrderByLikedDesc(review);

    return comments.stream()
        .map(CommentDto::new)
        .collect(Collectors.toList());
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
