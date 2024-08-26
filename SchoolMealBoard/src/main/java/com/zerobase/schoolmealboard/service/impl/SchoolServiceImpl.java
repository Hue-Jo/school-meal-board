package com.zerobase.schoolmealboard.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.schoolmealboard.entity.School;
import com.zerobase.schoolmealboard.repository.SchoolRepository;
import com.zerobase.schoolmealboard.service.SchoolService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SchoolServiceImpl implements SchoolService {

  private final SchoolRepository schoolRepository;

  private final RestTemplate restTemplate;  // API 요청 위함
  private final ObjectMapper objectMapper;  // JSON 파싱 위함

  @Value("${api.key}")
  private String apiKey;

  private final String API_URL = "https://open.neis.go.kr/hub/schoolInfo";
  private final String EDUCATION_OFFICE_CODE = "J10"; // 경기도교육청 코드

  private static final Logger logger = LoggerFactory.getLogger(SchoolServiceImpl.class);

  // 학교 정보를 최신 상태로 유지하기 위해
  // 애플리케이션 실행시 학교 정보를 가져와 저장
  @PostConstruct
  public void init() {
    fetchAndSaveSchoolInfo();
  }

  /**
   * API로부터 학교코드와 학교명을 가져와 DB에 저장하는 메서드
   */
  @Override
  @Transactional
  public void fetchAndSaveSchoolInfo() {
    int pIndex = 1;
    int pSize = 100;

    while (true) {
      String url = String.format("%s?KEY=%s&Type=json&pIndex=%d&pSize=%d&ATPT_OFCDC_SC_CODE=%s",
          API_URL, apiKey, pIndex, pSize, EDUCATION_OFFICE_CODE);

      try {
        // API 호출하여 응답 데이터 받아옴
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // API 호출 실패시 로그 기록 후 종료
        if (response.getStatusCode() != HttpStatus.OK) {
          logger.error("API로부터 데이터를 받아오는 데에 실패했습니다. 상태 코드: {}", response.getStatusCode());
          break;
        }

        // 응답 데이터를 JSON으로 파싱하여 "row" 배열에 접근
        JsonNode rows = objectMapper.readTree(response.getBody())
            .path("schoolInfo")
            .path(1).path("row");

        // "row" 배열이 비어있거나 남은 데이터가 없으면 반복 종료
        if (!rows.isArray() || rows.isEmpty()) {
          break;
        }

        // 데이터를 순회하며 DB에 저장
        for (JsonNode node : rows) {
          String schoolCode = node.path("SD_SCHUL_CODE").asText();
          String schoolName = node.path("SCHUL_NM").asText();

          // DB에 학교 코드가 존재하지 않을 경우 저장
          if (!schoolRepository.existsById(schoolCode)) {
            School school = new School();
            school.setSchoolCode(schoolCode);
            school.setSchoolName(schoolName);
            schoolRepository.save(school);
          }
        }
        pIndex++; // 다음 페이지로 이동

      } catch (Exception e) {
        logger.error("학교 정보를 가져오고 저장하는 중 예외가 발생했습니다", e);
        break;
      }
    }
  }
}