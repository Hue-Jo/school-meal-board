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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

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

  @Transactional
  public void fetchAndSaveMealInfo() {
    LocalDate now = LocalDate.now();
    LocalDate startDate = now.minusMonths(1);
    LocalDate endDate = now.plusMonths(1);

    List<School> schools = schoolRepository.findAll(); // 저장된 학교 정보 가져오기

    for (School school : schools) {
      String schoolCode = school.getSchoolCode();

      for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
        String formattedDate = date.format(DATE_FORMAT);
        String url = String.format(
            "%s?KEY=%s&Type=json&pIndex=%d&pSize=%d&ATPT_OFCDC_SC_CODE=%s&SD_SCHUL_CODE=%s&MLSV_FROM_YMD=%s&MLSV_TO_YMD=%s",
            API_URL, apiKey, 1, 100, EDU_OFFICE_CODE, schoolCode, formattedDate,
            formattedDate);

        try {
          ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

          if (response.getStatusCode() != HttpStatus.OK) {
            log.error("API로부터 데이터를 받아오는 데에 실패했습니다. 상태 코드: {}", response.getStatusCode());
            continue;
          }

          JsonNode rows = objectMapper.readTree(response.getBody())
              .path("mealServiceDietInfo")
              .path(1).path("row");

          if (!rows.isArray() || rows.isEmpty()) {
            continue;
          }

          for (JsonNode node : rows) {
            String mealNames = node.path("DDISH_NM").asText();
            LocalDate mealDate = LocalDate.parse(node.path("MLSV_YMD").asText(),DATE_FORMAT);

            // 기존 데이터와 중복을 피하기 위해 중복 검사
            if (!mealRepository.existsBySchoolCodeAndMealDate(school, mealDate)) {
              Meal meal = new Meal();
              meal.setSchoolCode(school);
              meal.setMealNames(mealNames);
              meal.setMealDate(mealDate);
              mealRepository.save(meal);
            }
          }

        } catch (Exception e) {
          log.error("급식 정보를 가져오고 저장하는 중 예외가 발생했습니다. 학교 코드: {}, 날짜: {}"
                    , schoolCode, formattedDate, e);
        }
      }
    }
  }
}