package com.zerobase.schoolmealboard.service;

public interface MealService {

  // API로부터 받은 데이터 저장
  void fetchAndSaveMealInfo();

  // 2달 전 급식 데이터 삭제
  void deleteOldMeal();

}
