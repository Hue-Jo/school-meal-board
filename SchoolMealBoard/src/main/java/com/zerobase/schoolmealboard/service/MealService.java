package com.zerobase.schoolmealboard.service;

import com.zerobase.schoolmealboard.ApiResponse.MealResponse;
import com.zerobase.schoolmealboard.ApiResponse.MealResponse.MealServiceDietInfo;
import com.zerobase.schoolmealboard.entity.School;
import java.util.List;

public interface MealService {

  // API로부터 받은 데이터 저장
  void fetchAndSaveMealInfo();

  // JSON 응답을 MealResponse 객체로 파싱
  MealResponse parseMealResponse(String responseBody);

  // 급식 정보 저장/업데이트
  void saveOrUpdateMeal(School school, MealResponse.MealRow row);

  // 2달 전 급식 데이터 삭제
  void deleteOldMeal();

}
