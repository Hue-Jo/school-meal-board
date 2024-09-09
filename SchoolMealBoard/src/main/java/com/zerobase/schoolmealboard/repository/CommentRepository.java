package com.zerobase.schoolmealboard.repository;

import com.zerobase.schoolmealboard.entity.Comment;
import com.zerobase.schoolmealboard.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  // 특정 리뷰의 모든 댓글 조회
  @Query(value = "SELECT * FROM comment WHERE review_id = :reviewId", nativeQuery = true)
  List<Comment> findByReviewId(Review review);

}
