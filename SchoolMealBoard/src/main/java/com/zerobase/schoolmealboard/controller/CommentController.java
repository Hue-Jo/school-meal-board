package com.zerobase.schoolmealboard.controller;

import com.zerobase.schoolmealboard.dto.CommentDto;
import com.zerobase.schoolmealboard.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @PostMapping("/create/{id}")
  public ResponseEntity<CommentDto> createComment(
      @PathVariable Long id,
      @RequestBody CommentDto commentDto) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    CommentDto createdComment = commentService.createComment(id, commentDto.getContent(), email);
    return (createdComment != null) ?
        ResponseEntity.status(HttpStatus.CREATED).body(createdComment) :
        ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }
}
