package com.zerobase.schoolmealboard.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.schoolmealboard.ApiResponse.SchoolResponse;
import com.zerobase.schoolmealboard.ApiResponse.SchoolResponse.CodeAndName;
import com.zerobase.schoolmealboard.entity.School;
import com.zerobase.schoolmealboard.repository.SchoolRepository;
import com.zerobase.schoolmealboard.service.SchoolService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolServiceImpl implements SchoolService {

  private final SchoolRepository schoolRepository;

  private final RestTemplate restTemplate;  // API 요청 위함
  private final ObjectMapper objectMapper;  // JSON 파싱 위함

  @Value("${api.key}")
  private String apiKey;

  private final String API_URL = "https://open.neis.go.kr/hub/schoolInfo";
  private final String EDU_OFFICE_CODE = "J10"; // 경기도교육청 코드

//   학교 정보를 최신 상태로 유지하기 위해
//   애플리케이션 실행시 학교 정보를 가져와 저장
  @PostConstruct
  public void init() {
    fetchAndSaveSchoolInfo();
  }

  /**
   * API로부터 학교코드와 학교명을 가져와 DB에 저장하는 메서드
   */
  @Override
  public void fetchAndSaveSchoolInfo() {

    int pIndex = 1;
    int pSize = 100;

    while (true) {
      String url = UriComponentsBuilder
          .fromHttpUrl(API_URL)
          .queryParam("KEY", apiKey)
          .queryParam("Type", "json")
          .queryParam("pIndex", pIndex)
          .queryParam("pSize", pSize)
          .queryParam("ATPT_OFCDC_SC_CODE", EDU_OFFICE_CODE)
          .toUriString(); // URL 문자열로 반환

      try {
        // API에 GET 요청을 보내고 응답 받기
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // API 호출 실패시 로그 기록 후 종료
        if (response.getStatusCode() != HttpStatus.OK) {
          log.error("API로부터 데이터를 받아오는 데에 실패했습니다. 상태 코드: {}", response.getStatusCode());
          break;
        }

        // JSON에서 Response로 변환
        SchoolResponse schoolResponse = objectMapper.readValue(response.getBody(), SchoolResponse.class);
        // Response에서 학교정보목록 가져옴
        List<SchoolResponse.SchoolInfo> schoolInfoList = schoolResponse.getSchoolInfo();

        if (schoolInfoList == null) {
          break;
        }

        List<CodeAndName> rows = schoolInfoList.get(1).getRow();

        // 데이터를 순회하며 DB에 저장
        for (CodeAndName cn : rows) {
          String schoolCode = cn.getSchoolCode();
          String schoolName = cn.getSchoolName();

          // DB에 학교 코드가 존재하지 않을 경우 새로 저장
          if (!schoolRepository.existsById(schoolCode)) {
            School school = new School();
            school.setSchoolCode(schoolCode);
            school.setSchoolName(schoolName);
            schoolRepository.save(school);
          }
        }

        pIndex++; // 다음 페이지로 이동

      } catch (Exception e) {
        log.error("학교 정보를 가져오고 저장하는 중 예외 발생", e);
        break;
      }
    }
  }
}
