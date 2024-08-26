package com.zerobase.schoolmealboard.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealServiceImpl implements MealService {

  private final WebClient webClient;
  private final ObjectMapper objectMapper;

  private final MealRepository mealRepository;
  private final SchoolRepository schoolRepository;

  @Value("${api.key}")
  private String apiKey;

  private final String API_URL = "https://open.neis.go.kr/hub/mealServiceDietInfo";
  private final String EDU_OFFICE_CODE = "J10"; // 경기도교육청 코드
  private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

  // 서비스 초기화 시 급식 정보를 가져와서 저장
  @PostConstruct
  public void init() {
    fetchAndSaveMealInfo();
  }

  @Transactional
  @Override
  public void fetchAndSaveMealInfo() {
    LocalDate now = LocalDate.now();
    // 현재로부터 1개월 전을 시작일로 설정
    LocalDate startDate = now.minusMonths(1);
    // 현재로부터 1개월 후를 종료일로 설정
    LocalDate endDate = now.plusMonths(1);

    // DB에서 학교 정보 가져오기
    List<School> schools = schoolRepository.findAll();

    // 각 학교의 급식 정보 요청
    for (School school : schools) {

      // 학교 코드 추출
      String schoolCode = school.getSchoolCode();

      // 조회 날짜 범위 내 반복
      for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
        // 날짜를 "yyyyMMdd" 형식으로 포맷.
        String formattedDate = date.format(DATE_FORMAT);
        String url = String.format(
            "%s?KEY=%s&Type=json&pIndex=%d&pSize=%d&ATPT_OFCDC_SC_CODE=%s&SD_SCHUL_CODE=%s&MLSV_FROM_YMD=%s&MLSV_TO_YMD=%s",
            API_URL, apiKey, 1, 100, EDU_OFFICE_CODE, schoolCode, formattedDate,
            formattedDate);

        try {
          // API 호출 및 응답 받기
          String responseBody = webClient.get()
              .uri(url)
              .retrieve()
              .bodyToMono(String.class)
              .block(); // 응답 대기

          // 응답이 없을 경우 에러 로그 출력, 다음 반복으로 이동
          if (responseBody == null) {
            log.error("API로부터 데이터를 받아오는 데에 실패했습니다." +
                "학교 코드: {}, 날짜: {}", schoolCode, formattedDate);
            continue;
          }

          // JSON 응답에서 급식 데이터 추출
          JsonNode rows = objectMapper.readTree(responseBody)
              .path("mealServiceDietInfo")
              .path(1).path("row");

          // 응답 데이터가 배열이고 비어 있지 않으면 급식 데이터 처리
          if (rows.isArray() && !rows.isEmpty()) {
            processMealData(rows, school, formattedDate);
          }

        } catch (Exception e) {
          log.error("급식 정보를 가져오고 저장하는 중 예외가 발생했습니다." +
                  " 학교 코드: {}, 날짜: {}", schoolCode, formattedDate,
              e);
        }
      }
    }
  }

  // 급식 데이터를 처리하여 데이터베이스에 저장하는 메소드
  @Override
  public void processMealData(JsonNode rows, School school, String formattedDate) {
    for (JsonNode node : rows) {
      // 급식 메뉴 추출
      String mealNames = node.path("DDISH_NM").asText();
      // 급식 날짜 추출/파싱
      LocalDate mealDate = LocalDate.parse(node.path("MLSV_YMD").asText(), DATE_FORMAT);

      // 중복 검사
      if (!mealRepository.existsBySchoolCodeAndMealDate(school, mealDate)) {
        Meal meal = new Meal();
        meal.setSchoolCode(school);
        meal.setMealNames(mealNames);
        meal.setMealDate(mealDate);
        mealRepository.save(meal);
      }
    }
  }
}
