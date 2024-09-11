package com.zerobase.schoolmealboard.service.impl;

import com.zerobase.schoolmealboard.dto.ReportDto;
import com.zerobase.schoolmealboard.entity.Report;
import com.zerobase.schoolmealboard.entity.User;
import com.zerobase.schoolmealboard.exceptions.custom.UnReportableException;
import com.zerobase.schoolmealboard.exceptions.custom.UserNotFoundException;
import com.zerobase.schoolmealboard.repository.ReportRepository;
import com.zerobase.schoolmealboard.repository.UserRepository;
import com.zerobase.schoolmealboard.service.ReportService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

  private final ReportRepository reportRepository;
  private final UserRepository userRepository;

  @Transactional
  @Override
  public void processReport(String reportedUserNickname, String reportUserEmail, ReportDto reportDto) {

    User reportUser = userRepository.findByEmail(reportUserEmail)
        .orElseThrow(() -> new UserNotFoundException("신고자 정보가 존재하지 않습니다."));

    User reportedUser = userRepository.findByNickName(reportedUserNickname)
        .orElseThrow(() -> new UserNotFoundException("피신고자 정보가 존재하지 않습니다."));

    LocalDate today = LocalDate.now();

    // 같은 날 동일 피신고자에 대한 중복 신고 확인
    if (reportRepository.existsByReportUserAndReportedUserAndDate(reportUser, reportedUser, today)) {
      throw new UnReportableException("오늘 이미 신고한 유저입니다. 신고 처리중입니다.");
    }

    // 신고자가 피신고자에 대해 신고할 수 있는지(이미 이용정지상태인지) 확인
    if (reportedUser.getBanEndDate() != null && reportedUser.getBanEndDate().isAfter(today)) {
      throw new UnReportableException("이미 이용정지 상태의 유저입니다.");
    }

    // 신고 저장
    Report report = Report.builder()
        .reportUser(reportUser)
        .reportedUser(reportedUser)
        .reason(reportDto.getReason())
        .date(today)
        .build();
    reportRepository.save(report);

    // 해당 피신고자에 대한 오늘의 신고 수 계산
    int reportedCount = reportRepository.countByReportedUserAndDate(reportedUser, today);
    // 신고 수가 10건 이상이면 이용 정지 처리
    if (reportedCount >= 10) {
      reportedUser.setBanEndDate(today.plusDays(7));
      userRepository.save(reportedUser);
    }

  }

}
