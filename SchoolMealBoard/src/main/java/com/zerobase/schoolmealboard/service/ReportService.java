package com.zerobase.schoolmealboard.service;

import com.zerobase.schoolmealboard.dto.ReportDto;

public interface ReportService {

  // 신고
  void processReport(String reportedUserNickname, String reportUserEmail, ReportDto reportDto);

  }
