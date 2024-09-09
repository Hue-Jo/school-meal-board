package com.zerobase.schoolmealboard.service;

import com.zerobase.schoolmealboard.dto.CommentDto;

public interface CommentService {

  CommentDto createComment(Long reviewId, String content, String email);
}
