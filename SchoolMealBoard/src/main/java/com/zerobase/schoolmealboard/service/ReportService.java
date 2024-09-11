package com.zerobase.schoolmealboard.service;

import com.zerobase.schoolmealboard.dto.ReportDto;

public interface ReportService {

  void processReport(String reportedUserNickname, String reportUserEmail, ReportDto reportDto);

  }
