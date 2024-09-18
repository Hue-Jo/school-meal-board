package com.zerobase.schoolmealboard.service.impl;

import com.zerobase.schoolmealboard.entity.Comment;
import com.zerobase.schoolmealboard.entity.Likes;
import com.zerobase.schoolmealboard.entity.User;
import com.zerobase.schoolmealboard.exceptions.custom.CommentNotFoundException;
import com.zerobase.schoolmealboard.exceptions.custom.ConcurrentException;
import com.zerobase.schoolmealboard.exceptions.custom.UnAuthorizedUser;
import com.zerobase.schoolmealboard.exceptions.custom.UserNotFoundException;
import com.zerobase.schoolmealboard.repository.CommentRepository;
import com.zerobase.schoolmealboard.repository.LikesRepository;
import com.zerobase.schoolmealboard.repository.UserRepository;
import com.zerobase.schoolmealboard.service.LikeService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

  private final LikesRepository likesRepository;
  private final CommentRepository commentRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public String toggleLike(Long commentId, String email) {

    User user = validateUser(email);

    Comment comment = commentRepository.findByIdWithLock(commentId)
        .orElseThrow(() -> new CommentNotFoundException("해당 댓글이 존재하지 않습니다."));

    // 동일한 학교인지 확인
    if (!user.getSchoolCode().equals(comment.getUser().getSchoolCode())) {
      throw new UnAuthorizedUser("동일한 학교의 학생만 공감할 수 있습니다.");
    }

    try {
      // 이미 좋아요 눌렀는지 확인
      Optional<Likes> existingLikes = likesRepository.findByCommentAndUser(comment, user);

      // 있으면 취소, 없으면 추가
      if (existingLikes.isPresent()) {
        likesRepository.delete(existingLikes.get());
        comment.setLiked(comment.getLiked() - 1);
      } else {
        Likes newLikes = Likes.builder()
            .comment(comment)
            .user(user)
            .build();
        likesRepository.save(newLikes);
        comment.setLiked(comment.getLiked() + 1);
      }
      commentRepository.save(comment);
      return existingLikes.isPresent() ? "공감이 취소되었습니다." : "공감처리가 완료되었습니다.";
    } catch (Exception e) {
      throw new ConcurrentException("현재 처리중인 요청이 너무 많습니다. 다시 시도해주세요.");
    }
  }

  private User validateUser(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));
  }
}