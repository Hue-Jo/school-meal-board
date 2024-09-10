package com.zerobase.schoolmealboard.service;

import com.zerobase.schoolmealboard.dto.CommentDto;
import java.util.List;

public interface CommentService {

  CommentDto createComment(Long reviewId, String content, String email);

  CommentDto editComment(Long commentId, String content, String email);

  void deleteComment(Long commentId, String email);

  List<CommentDto> getCommentsByCreatedDate(Long reviewId, String email);

  List<CommentDto> getCommentsByLikes(Long reviewId, String email);
}
