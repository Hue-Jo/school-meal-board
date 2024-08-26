package com.zerobase.schoolmealboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class Report {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reportId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "report_user")
  private User reportUser;  // 신고자

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reported_user")
  private User reportedUser;  // 피신고자

  private String reason;  // 신고 사유
  private LocalDate date; // 신고일

}
