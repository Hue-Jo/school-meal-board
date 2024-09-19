package com.zerobase.schoolmealboard.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.schoolmealboard.ApiResponse.MealResponse;
import com.zerobase.schoolmealboard.entity.Meal;
import com.zerobase.schoolmealboard.entity.School;
import com.zerobase.schoolmealboard.exceptions.custom.MealNotFoundException;
import com.zerobase.schoolmealboard.exceptions.custom.ReviewNotFoundException;
import com.zerobase.schoolmealboard.repository.MealRepository;
import com.zerobase.schoolmealboard.repository.ReviewRepository;
import com.zerobase.schoolmealboard.repository.SchoolRepository;
import com.zerobase.schoolmealboard.service.MealService;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
  private final MealRepository mealRepository;
  private final SchoolRepository schoolRepository;
  private final ReviewRepository reviewRepository;

  @Value("${api.key}")
  private String apiKey;

  private final String API_URL = "https://open.neis.go.kr/hub/mealServiceDietInfo";
  private final String EDU_OFFICE_CODE = "J10"; // 경기도교육청 코드
  private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

//  @PostConstruct
//  public void init() {
//    fetchAndSaveMealInfo();
//  }

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

      // 각 페이지의 학교 목록 처리
      for (School school : page.getContent()) {
        String schoolCode = school.getSchoolCode();  // 학교코드 가져옴

        // 날짜 범위 내 반복하며 데이터 가져옴
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
          String formattedDate = date.format(DATE_FORMAT);

          String url = UriComponentsBuilder
              .fromHttpUrl(API_URL)
              .queryParam("KEY", apiKey)
              .queryParam("Type", "json")
              .queryParam("pIndex", 1)
              .queryParam("pSize", 100)
              .queryParam("ATPT_OFCDC_SC_CODE", EDU_OFFICE_CODE)
              .queryParam("SD_SCHUL_CODE", schoolCode)
              .queryParam("MLSV_YMD", formattedDate)
              .toUriString(); // URL 문자열로 반환

          try {
            // API 호출
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String responseBody = response.getBody(); // 응답 본문을 문자열로

            // JSON 응답을 MealResponse 객체로 파싱
            MealResponse mealResponse = parseMealResponse(responseBody);
            if (mealResponse != null && mealResponse.getMealServiceDietInfo() != null) {
              for (MealResponse.MealServiceDietInfo info : mealResponse.getMealServiceDietInfo()) {
                if (info.getRow() != null) {
                  for (MealResponse.MealRow row : info.getRow()) {
                    saveOrUpdateMeal(school, row);  // 급식 정보 저장/업데이트
                  }
                }
              }
            }
          } catch (Exception e) {
            log.error("데이터를 가져오고 저장하는 데에 실패. 날짜: {} and 학교코드: {}", formattedDate, schoolCode, e);
          }
        }
      }

      indexPage++;
    } while (page.hasNext());

  }

  // JSON 응답을 MealResponse 객체로 파싱
  public MealResponse parseMealResponse(String responseBody) {
    try {
      return new ObjectMapper().readValue(responseBody, MealResponse.class);
    } catch (Exception e) {
      log.error("Failed to parse meal response: {}", responseBody, e);
      return null;
    }
  }

  // 급식 정보 저장/업데이트
  public void saveOrUpdateMeal(School school, MealResponse.MealRow row) {
    LocalDate mealDate = LocalDate.parse(row.getMealDate(), DATE_FORMAT); // 급식 날짜를 LocalDate로 변환
    String mealName = row.getMealName();  // 급식 메뉴명

    // 학교와 날짜에 대한 기존 급식 조회
    Meal existingMeal = mealRepository.findBySchoolCodeAndMealDate(school, mealDate).orElse(null);

    if (existingMeal != null) {
      // 데이터 있을 경우, 업데이트
      existingMeal.setMealNames(mealName);
      mealRepository.save(existingMeal);

    } else {
      // 데이터 없을 경우, 새로운 급식 정보를 생성하여 저장
      Meal newMeal = new Meal();
      newMeal.setSchoolCode(school);
      newMeal.setMealDate(mealDate);
      newMeal.setMealNames(mealName);
      mealRepository.save(newMeal);
    }
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


  // 특정 학교코드, 날짜 기입시 해당 급식 정보 반환
  @Override
  public List<Meal> getMealBySchoolCodeAndDate(String schoolCode, LocalDate date) {

    School school = schoolRepository.findById(schoolCode)
        .orElseThrow(() -> new MealNotFoundException("해당 학교가 존재하지 않습니다. 학교코드를 다시 한번 확인해주세요"));

    // 한 학교에 조식 중식 석식이 있는 경우도 있으므로 List로 반환
    List<Meal> meals = mealRepository.findMealBySchoolCodeAndMealDate(school, date);

    if (meals.isEmpty()) {
      throw new MealNotFoundException("해당 날짜에 해당하는 급식 정보가 없습니다.");
    }

    return meals;
  }


  // 특정 학교코드, 날짜 기입시 해당 급식의 평균별점 반환
  @Override
  public double getAverageRating(String schoolCode, LocalDate mealDate) {
    School school = schoolRepository.findById(schoolCode)
        .orElseThrow(() -> new MealNotFoundException("해당 학교가 존재하지 않습니다. 학교코드를 다시 한번 확인해주세요"));

    Double averageRating = reviewRepository.findAverageRatingBySchoolCodeAndMealDate(school, mealDate);

    if (averageRating == null) {
      throw new ReviewNotFoundException("해당 급식은 리뷰가 존재하지 않습니다.");
    }

    return averageRating;

  }
}