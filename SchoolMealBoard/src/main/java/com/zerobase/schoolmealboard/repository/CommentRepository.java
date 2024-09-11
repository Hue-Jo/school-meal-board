package com.zerobase.schoolmealboard.repository;

import com.zerobase.schoolmealboard.entity.Comment;
import com.zerobase.schoolmealboard.entity.Review;
import com.zerobase.schoolmealboard.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  // 댓글이 달린 순서대로 조회
  List<Comment> findByReviewIdOrderByCreatedDateTimeAsc(Review review);

  // 댓글에 달린 공감수 정렬하여 조회
  List<Comment> findByReviewIdOrderByLikedDesc(Review review);

  // 특정 유저가 특정 댓글에 이미 공감했는지 체크하는 메서드
  Optional<Comment> findByUserIdAndCommentId(User user, Long commentId);
}
