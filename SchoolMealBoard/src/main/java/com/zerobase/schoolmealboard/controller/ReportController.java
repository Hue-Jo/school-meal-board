package com.zerobase.schoolmealboard.controller;

import com.zerobase.schoolmealboard.dto.ReportDto;
import com.zerobase.schoolmealboard.exceptions.custom.UnReportableException;
import com.zerobase.schoolmealboard.exceptions.custom.UserNotFoundException;
import com.zerobase.schoolmealboard.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;

  @PostMapping("/{reportedUserNickName}")
  public ResponseEntity<String> report(
      @PathVariable String reportedUserNickname,
      @RequestBody @Valid ReportDto reportDto) {

    String reporterEmail = SecurityContextHolder.getContext().getAuthentication().getName();

    try {
      reportService.processReport(reportedUserNickname, reporterEmail, reportDto);
      return ResponseEntity.status(HttpStatus.OK).body("신고가 접수되었습니다.");
    } catch (UserNotFoundException | UnReportableException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

}
