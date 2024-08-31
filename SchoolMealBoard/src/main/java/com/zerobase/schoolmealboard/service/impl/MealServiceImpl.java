package com.zerobase.schoolmealboard.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.schoolmealboard.ApiResponse.MealResponse;
import com.zerobase.schoolmealboard.entity.Meal;
import com.zerobase.schoolmealboard.entity.School;
import com.zerobase.schoolmealboard.repository.MealRepository;
import com.zerobase.schoolmealboard.repository.SchoolRepository;
import com.zerobase.schoolmealboard.service.MealService;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@RequiredArgsConstructor
@Slf4j
public class MealServiceImpl implements MealService {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  private final MealRepository mealRepository;
  private final SchoolRepository schoolRepository;

  @Value("${api.key}")
  private String apiKey;

  private final String API_URL = "https://open.neis.go.kr/hub/mealServiceDietInfo";
  private final String EDU_OFFICE_CODE = "J10"; // 경기도교육청 코드
  private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

  @PostConstruct
  public void init() {
    fetchAndSaveMealInfo();
  }

  @Override
  @Transactional
  public void fetchAndSaveMealInfo() {
    LocalDate now = LocalDate.now();
    LocalDate startDate = now.minusMonths(1);
    LocalDate endDate = now.plusMonths(1);

    int indexPage = 0; // 시작 페이지 번호
    int pageSize = 100; // 페이지 크기
    Page<School> page;

    do {
      Pageable pageable = PageRequest.of(indexPage, pageSize);
      page = schoolRepository.findAll(pageable);

      // 각 페이지의 학교 목록을 처리
      for (School school : page.getContent()) {
        String schoolCode = school.getSchoolCode();

        // 날짜 범위 내 반복
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
          String formattedDate = date.format(DATE_FORMAT);

          String url = UriComponentsBuilder
              .fromHttpUrl(API_URL)
              .queryParam("KEY", apiKey)
              .queryParam("Type", "json")
              .queryParam("pIndex", 1) // 페이지 인덱스는 1로 고정 (한 페이지만 가져올 경우)
              .queryParam("pSize", 100) // 페이지 크기 설정
              .queryParam("ATPT_OFCDC_SC_CODE", EDU_OFFICE_CODE)
              .queryParam("SD_SCHUL_CODE", schoolCode)
              .queryParam("MLSV_YMD", formattedDate)
              .toUriString(); // URL 문자열로 반환

          try {
            // API에 GET 요청을 보내고 응답 받기
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // API 호출 실패시 로그기록 후 다음으로 넘어감
            if (response.getStatusCode() != HttpStatus.OK) {
              log.error("API로부터 데이터를 받아오는 데에 실패했습니다. 상태 코드: {}", response.getStatusCode());
              continue;
            }

            // JSON에서 Response로 변환
            MealResponse mealResponse = objectMapper.readValue(response.getBody(),
                MealResponse.class);
            List<MealResponse.MealServiceDietInfo> mealInfoList = mealResponse.getMealServiceDietInfo();

            if (mealInfoList == null) {
              continue; // 급식 정보가 없으면 다음 날짜로 넘어감
            }

            List<MealResponse.MealRow> rows = mealInfoList.get(1).getRow();  // 데이터 추출

            // 데이터를 순회하며 메뉴와 날짜 DB에 저장
            for (MealResponse.MealRow row : rows) {
              String mealNames = row.getMealName();
              LocalDate mealDate = LocalDate.parse(row.getMealDate(), DATE_FORMAT);

              // 기존 데이터 조회
              mealRepository.findBySchoolCodeAndMealDate(school, mealDate)
                  .ifPresentOrElse(existingMeal -> {
                    // 기존 데이터가 있으면 업데이트
                    existingMeal.setMealNames(mealNames);
                    mealRepository.save(existingMeal);
                  }, () -> {
                    // 기존 데이터가 없으면 새로 추가
                    Meal meal = new Meal();
                    meal.setSchoolCode(school);
                    meal.setMealNames(mealNames);
                    meal.setMealDate(mealDate);
                    mealRepository.save(meal);
                  });
            }

          } catch (Exception e) {
            log.error("급식 정보를 가져오고 저장하는 중 예외 발생", e);
          }
        }
      }

      indexPage++;

    } while (page.hasNext()); // 다음 페이지가 있는 경우 반복
  }


  // 2달 전 급식 데이터 삭제 (ex. 9월 1일이 되면 7월 1~31일의 데이터 삭제)
  @Override
  public void deleteOldMeal() {
    LocalDate now = LocalDate.now();
    LocalDate twoMonthsAgoStartDate = now.minusMonths(2).withDayOfMonth(1); // 두 달 전의 첫째 날
    LocalDate twoMonthsAgoEndDate = twoMonthsAgoStartDate.withDayOfMonth(
        twoMonthsAgoStartDate.lengthOfMonth()); // 두 달 전의 마지막 날

    // 두 달 전 데이터 삭제
    mealRepository.deleteByMealDateBetween(twoMonthsAgoStartDate, twoMonthsAgoEndDate);
  }
}