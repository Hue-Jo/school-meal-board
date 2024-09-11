package com.zerobase.schoolmealboard.repository;

import com.zerobase.schoolmealboard.entity.Comment;
import com.zerobase.schoolmealboard.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  // 댓글이 달린 순서대로 조회
  List<Comment> findByReviewIdOrderByCreatedDateTimeAsc(Review review);

  // 댓글에 달린 공감수 정렬하여 조회
  List<Comment> findByReviewIdOrderByLikedDesc(Review review);

}
