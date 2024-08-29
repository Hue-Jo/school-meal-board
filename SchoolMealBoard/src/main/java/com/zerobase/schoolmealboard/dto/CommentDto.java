package com.zerobase.schoolmealboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

  private String content; // 댓글 내용
  private int liked;      // 좋아요 수

}
