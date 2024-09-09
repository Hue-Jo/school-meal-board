package com.zerobase.schoolmealboard.repository;


import com.zerobase.schoolmealboard.entity.Comment;
import com.zerobase.schoolmealboard.entity.Likes;
import com.zerobase.schoolmealboard.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, Long> {

  // 특정 댓글의 공감수를 계산
  int countByComment(Comment comment);

  // 특정 사용자가 특정 댓글에 공감을 했는지 확인
  Optional<Likes> findByCommentAndUser(Comment comment, User user);
}
