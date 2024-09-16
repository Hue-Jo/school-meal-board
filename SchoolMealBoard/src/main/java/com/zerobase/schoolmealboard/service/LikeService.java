package com.zerobase.schoolmealboard.service;

public interface LikeService {

  // 댓글에 대한 좋아요 기능 (토글식)
  String toggleLike(Long commentId, String email);

}
