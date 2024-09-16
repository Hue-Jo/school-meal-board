package com.zerobase.schoolmealboard.repository;

import com.zerobase.schoolmealboard.entity.Comment;
import com.zerobase.schoolmealboard.entity.Review;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  // 댓글이 달린 순서대로 조회
  List<Comment> findByReviewOrderByCreatedDateTimeAsc(Review review);

  // 댓글에 달린 공감수 정렬하여 조회
  List<Comment> findByReviewOrderByLikedDesc(Review review);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT c From Comment c WHERE c.commentId = :commentId")
  Optional<Comment> findByCommentIdWithLock(@Param("commentId") Long commentId);

}
