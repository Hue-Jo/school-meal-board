package com.zerobase.schoolmealboard.controller;

import com.zerobase.schoolmealboard.dto.CommentDto;
import com.zerobase.schoolmealboard.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
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

  @PostMapping("/create/{id}")
  public ResponseEntity<CommentDto> createComment(
      @PathVariable Long id,
      @RequestBody CommentDto commentDto) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    CommentDto createdComment = commentService.createComment(id, commentDto.getContent(), email);
    return (createdComment != null) ?
        ResponseEntity.status(HttpStatus.CREATED).body(createdComment) :
        ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  @PatchMapping("/edit/{id}")
  public ResponseEntity<CommentDto> editComment(
      @PathVariable Long id,
      @RequestBody CommentDto commentDto) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    CommentDto editedComment = commentService.editComment(id, commentDto.getContent(), email);
    return (editedComment != null) ?
        ResponseEntity.status(HttpStatus.OK).body(editedComment) :
        ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<String> deleteComment(
      @PathVariable Long id) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    commentService.deleteComment(id, email);
    return ResponseEntity.status(HttpStatus.OK).body("댓글이 삭제되었습니다.");
  }
}