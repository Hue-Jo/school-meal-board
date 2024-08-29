package com.zerobase.schoolmealboard.component;

import com.zerobase.schoolmealboard.service.MealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

  private final MealService mealService;


  // 업데이트된 급식 정보를 매일 오전 10시에 API에서 받아 DB에 저장 실행되도록 스케줄링
  @Scheduled(cron = "0 0 10 * * *")
  public void scheduleFetchAndSaveMealInfo() {
    log.info("급식정보를 업데이트합니다.");
    mealService.fetchAndSaveMealInfo();
    log.info("급식정보 업데이트가 완료되었습니다.");
  }


  @Scheduled(cron = "0 0 0 1 * *")
  public void scheduleDeleteOldMeal() {
    log.info("2개월 전의 급식 데이터가 삭제됩니다.");
    mealService.deleteOldMeal();
    log.info("데이터가 삭제되었습니다.");
  }
}
