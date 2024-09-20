package com.zerobase.schoolmealboard.controller;

import com.zerobase.schoolmealboard.dto.CommentDto;
import com.zerobase.schoolmealboard.service.CommentService;
import com.zerobase.schoolmealboard.service.LikeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;
  private final LikeService likeService;

  @PostMapping("/{reviewId}")
  public ResponseEntity<CommentDto> createComment(
      @PathVariable Long reviewId,
      @RequestBody CommentDto commentDto) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    CommentDto createdComment = commentService.createComment(reviewId, commentDto.getContent(),
        email);
    return (createdComment != null) ?
        ResponseEntity.status(HttpStatus.CREATED).body(createdComment) :
        ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  @PatchMapping("/{commentId}")
  public ResponseEntity<CommentDto> editComment(
      @PathVariable Long commentId,
      @RequestBody CommentDto commentDto) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    CommentDto editedComment = commentService.editComment(commentId, commentDto.getContent(),
        email);
    return (editedComment != null) ?
        ResponseEntity.status(HttpStatus.OK).body(editedComment) :
        ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  @DeleteMapping("/{commentId}")
  public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    commentService.deleteComment(commentId, email);
    return ResponseEntity.status(HttpStatus.OK).body("댓글이 삭제되었습니다.");
  }


  @PostMapping("/{commentId}/like")
  public ResponseEntity<String> toggleLike(@PathVariable Long commentId) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    String message = likeService.toggleLike(commentId, email);
    return ResponseEntity.status(HttpStatus.OK).body(message);
  }


  @GetMapping("/{reviewId}/sorted-by-date")
  public ResponseEntity<Page<CommentDto>> getCommentsByDate(
      @PathVariable Long reviewId,
      Pageable pageable) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    Page<CommentDto> comments = commentService.getCommentsByCreatedDate(reviewId, email, pageable);
    return ResponseEntity.status(HttpStatus.OK).body(comments);

  }

  @GetMapping("/{reviewId}/sorted-by-likes")
  public ResponseEntity<Page<CommentDto>> getCommentsByLikes(
      @PathVariable Long reviewId,
      Pageable pageable) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    Page<CommentDto> comments = commentService.getCommentsByLikes(reviewId, email, pageable);
    return ResponseEntity.status(HttpStatus.OK).body(comments);
  }

}