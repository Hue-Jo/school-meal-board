package com.zerobase.schoolmealboard.dto;

import com.zerobase.schoolmealboard.entity.Comment;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

  private Long commentId;
  private Long reviewId;

  private String nickName;  // 댓글 작성자 닉네임

  @NotBlank
  private String content;   // 댓글 내용

  private int likes;        // 공감 수

  public CommentDto(Comment comment) {
    this.commentId = comment.getCommentId();
    this.reviewId = comment.getReview().getReviewId();
    this.nickName = comment.getUser().getNickName();
    this.content = comment.getContent();
    this.likes = comment.getLiked();
  }

}
