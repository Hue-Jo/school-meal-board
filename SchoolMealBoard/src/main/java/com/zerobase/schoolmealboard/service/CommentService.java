package com.zerobase.schoolmealboard.service;

import com.zerobase.schoolmealboard.dto.CommentDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

  // 댓글 작성 (게시글 작성자와 동일 학교 학생만 가능)
  CommentDto createComment(Long reviewId, String content, String email);

  // 댓글 수정 (댓글 작성자만 수정 가능)
  CommentDto editComment(Long commentId, String content, String email);

  // 댓글 삭제 (댓글 작성자만 삭제 가능)
  void deleteComment(Long commentId, String email);

  // 댓글 리스트 조회 (작성 날짜순 정렬)
  Page<CommentDto> getCommentsByCreatedDate(Long reviewId, String email, Pageable pageable);

  // 댓글 리스트 조회 (공감순 정렬)
  Page<CommentDto> getCommentsByLikes(Long reviewId, String email, Pageable pageable);
}
