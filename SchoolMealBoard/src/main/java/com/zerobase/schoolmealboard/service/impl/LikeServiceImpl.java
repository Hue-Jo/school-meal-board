package com.zerobase.schoolmealboard.service.impl;

import com.zerobase.schoolmealboard.entity.Comment;
import com.zerobase.schoolmealboard.entity.Likes;
import com.zerobase.schoolmealboard.entity.User;
import com.zerobase.schoolmealboard.exceptions.custom.CommentNotFoundException;
import com.zerobase.schoolmealboard.exceptions.custom.UserNotFoundException;
import com.zerobase.schoolmealboard.repository.CommentRepository;
import com.zerobase.schoolmealboard.repository.LikesRepository;
import com.zerobase.schoolmealboard.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl {

  private final LikesRepository likesRepository;
  private final CommentRepository commentRepository;
  private final UserRepository userRepository;


  @Transactional
  public String toggleLike(Long commentId, Long userId) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));

    // 동일한 학교인지 확인
    if (!user.getSchoolCode().equals(comment.getUserId().getSchoolCode())) {
      throw new IllegalArgumentException("동일한 학교의 학생만 공감할 수 있습니다.");
    }

    Optional<Likes> likes = likesRepository.findByCommentAndUser(comment, user);

    if (likes.isPresent()) {
      likesRepository.delete(likes.get());
      comment.setLiked(comment.getLiked() - 1);
      commentRepository.save(comment);
      return "공감취소";
    } else {
      Likes newLikes = Likes.builder()
          .comment(comment)
          .user(user)
          .build();
      likesRepository.save(newLikes);
      comment.setLiked(comment.getLiked() + 1);
      commentRepository.save(comment);
      return "공감 처리가 완료되었습니다.";
    }
  }
}
