package com.zerobase.schoolmealboard.service;

import com.zerobase.schoolmealboard.dto.SchoolDto;

public interface SchoolService {

  // 외부 API로부터 데이터를 받아 저장
  void fetchAndSaveSchoolInfo();

  // 학교코드 찾기
  String getSchoolCode(String schoolName);

}
