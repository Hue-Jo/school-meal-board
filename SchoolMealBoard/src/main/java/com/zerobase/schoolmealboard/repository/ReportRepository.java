package com.zerobase.schoolmealboard.repository;

import com.zerobase.schoolmealboard.entity.Report;
import com.zerobase.schoolmealboard.entity.User;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

  // 같은 날 동일 피신고자에 대한 중복 신고 확인
  boolean existsByReportUserAndReportedUserAndDate(User reportUser, User reportedUser, LocalDate date);

  // 해당 피신고자에 대한 오늘의 신고 수 계산
  int countByReportedUserAndDate(User reportedUser, LocalDate date);

}
