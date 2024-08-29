package com.zerobase.schoolmealboard.config;

import com.zerobase.schoolmealboard.service.MealService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

  private final MealService mealService;

  // 매일 동기화
  @Bean
  public Job fetchAndSaveMealJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new JobBuilder("fetchAndSaveMealJob", jobRepository)
        .start(fetchAndSaveMealStep(jobRepository, transactionManager))
        .build();
  }

  @Bean
  public Step fetchAndSaveMealStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("fetchAndSaveMealStep", jobRepository)
        .tasklet(fetchAndSaveMealTasklet(), transactionManager)
        .build();
  }

  @Bean
  public Tasklet fetchAndSaveMealTasklet() {
    return (((contribution, chunkContext) -> { 
      mealService.fetchAndSaveMealInfo(); // 동기화
      return RepeatStatus.FINISHED; // 작업이 성공적으로 완료시
    }));
  }

  // 월 1회, 두 달 전의 데이터 삭제
  @Bean
  public Job deleteOldMealJob(JobRepository jobRepository,
      PlatformTransactionManager transactionManager) {
    return new JobBuilder("deleteOldDataJob", jobRepository)
        .start(deleteOldMealStep(jobRepository, transactionManager))
        .build();
  }

  @Bean
  public Step deleteOldMealStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager) {
    return new StepBuilder("deleteOldDataStep", jobRepository)
        .tasklet(deleteOldDataTasklet(), transactionManager)
        .build();
  }

  @Bean
  public Tasklet deleteOldDataTasklet() {
    return ((contribution, chunkContext) -> {
      mealService.deleteOldMeal(); //  두 달 전 데이터 삭제
      return RepeatStatus.FINISHED;
    });
  }


}
