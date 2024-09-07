package com.zerobase.schoolmealboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

  private Long commentId;

  private Long reviewId;
  private Long userId;

  private String content;   // 댓글 내용
  private int likes;        // 공감 수

}
