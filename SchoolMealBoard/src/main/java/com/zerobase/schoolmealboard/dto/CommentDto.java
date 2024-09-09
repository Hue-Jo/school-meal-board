package com.zerobase.schoolmealboard.dto;

import com.zerobase.schoolmealboard.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

  private Long commentId;

  private String nickName;
  private String content;   // 댓글 내용
  private int likes;        // 공감 수

  public CommentDto(Comment comment) {
    this.commentId = comment.getCommentId();
    this.nickName = comment.getUserId().getNickName();
    this.content = comment.getContent();
    this.likes = comment.getLiked();
  }

}
