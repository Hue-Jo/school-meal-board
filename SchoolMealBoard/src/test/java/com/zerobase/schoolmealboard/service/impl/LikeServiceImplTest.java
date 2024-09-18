package com.zerobase.schoolmealboard.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.schoolmealboard.entity.Comment;
import com.zerobase.schoolmealboard.entity.Likes;
import com.zerobase.schoolmealboard.entity.Review;
import com.zerobase.schoolmealboard.entity.School;
import com.zerobase.schoolmealboard.entity.User;
import com.zerobase.schoolmealboard.exceptions.custom.ConcurrentException;
import com.zerobase.schoolmealboard.repository.CommentRepository;
import com.zerobase.schoolmealboard.repository.LikesRepository;
import com.zerobase.schoolmealboard.repository.ReviewRepository;
import com.zerobase.schoolmealboard.repository.UserRepository;
import com.zerobase.schoolmealboard.service.LikeService;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class LikeServiceImplTest {

  @MockBean
  private LikesRepository likesRepository;

  @MockBean
  private CommentRepository commentRepository;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private ReviewRepository reviewRepository;

  @Autowired
  private LikeService likesService;

  private User user1;
  private User user2;
  private Review review;
  private Comment comment;

  @BeforeEach
  public void setUp() {
    // 테스트용 사용자 및 리뷰 생성

    School school = new School("111111", "SchoolName");
    user1 = User.builder()
        .userId(1L)
        .schoolCode(school)
        .phoneNum("010-1111-1111")
        .email("user1@example.com")
        .nickName("user1")
        .password("12345678")
        .banEndDate(null)
        .build();

    user2 = User.builder()
        .userId(2L)
        .schoolCode(school)
        .phoneNum("010-2222-2222")
        .email("user2@example.com")
        .nickName("user2")
        .password("12345678")
        .banEndDate(null)
        .build();

    review = Review.builder()
        .reviewId(1L)
        .user(user1)
        .date(LocalDate.now())
        .title("Test Review")
        .content("This is a test review.")
        .rating(5)
        .imgUrl(null)
        .build();

    comment = Comment.builder()
        .commentId(1L)
        .user(user1)
        .review(review)
        .content("Test Comment")
        .liked(0) // 초기 좋아요 수
        .createdDateTime(null)
        .build();

    // Mock 설정
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
    when(userRepository.findByEmail(user2.getEmail())).thenReturn(Optional.of(user2));
    when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));
    when(commentRepository.findByIdWithLock(comment.getCommentId())).thenReturn(Optional.of(comment));
  }

  @Test
  public void testLikeToggle() {
    // 좋아요 추가
    when(likesRepository.findByCommentAndUser(comment, user1)).thenReturn(Optional.empty());
    String result1 = likesService.toggleLike(comment.getCommentId(), user1.getEmail());
    assertEquals("공감처리가 완료되었습니다.", result1);
    assertEquals(1, comment.getLiked());

    // 좋아요 취소
    when(likesRepository.findByCommentAndUser(comment, user1)).thenReturn(Optional.of(new Likes(1L, comment, user1)));
    String result2 = likesService.toggleLike(comment.getCommentId(), user1.getEmail());
    assertEquals("공감이 취소되었습니다.", result2);
    assertEquals(0, comment.getLiked());
  }

  @Test
  public void testConcurrentLikeToggle() throws InterruptedException {
    // 두 개의 스레드를 생성하여 동시에 좋아요 시도
    when(likesRepository.findByCommentAndUser(comment, user1)).thenReturn(Optional.empty());
    when(likesRepository.findByCommentAndUser(comment, user2)).thenReturn(Optional.empty());

    Thread thread1 = new Thread(() -> likesService.toggleLike(comment.getCommentId(), user1.getEmail()));
    Thread thread2 = new Thread(() -> likesService.toggleLike(comment.getCommentId(), user2.getEmail()));

    thread1.start();
    thread2.start();

    thread1.join();
    thread2.join();

    // 좋아요 수가 2가 되었는지 확인
    verify(commentRepository, times(2)).save(comment);
    assertEquals(2, comment.getLiked());
  }

  @Test
  public void testConcurrencyException() {
    // Mock 설정: 동시성 문제 발생 시 예외 던지기
    doThrow(new ConcurrentException("현재 처리중인 요청이 너무 많습니다. 다시 시도해주세요."))
        .when(likesRepository).save(any(Likes.class));

    // 동시성 문제 발생 시 예외 처리 확인
    ConcurrentException exception = assertThrows(ConcurrentException.class, () -> {
      likesService.toggleLike(comment.getCommentId(), user1.getEmail());
    });

    assertEquals("현재 처리중인 요청이 너무 많습니다. 다시 시도해주세요.", exception.getMessage());
  }
}