package com.zerobase.schoolmealboard.repository;

import com.zerobase.schoolmealboard.entity.Comment;
import com.zerobase.schoolmealboard.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  // 특정 리뷰의 모든 댓글 조회
  List<Comment> findByReviewId(Review reviewId);

  // 특정 리뷰의 모든 댓글 공감순으로 정렬
  List<Comment> findByReviewIdOrderByLikedAsc(Review reviewId);

}
