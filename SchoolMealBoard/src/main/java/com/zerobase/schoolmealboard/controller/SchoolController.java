package com.zerobase.schoolmealboard.controller;

import com.zerobase.schoolmealboard.dto.SchoolDto;
import com.zerobase.schoolmealboard.service.SchoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/school")
@RequiredArgsConstructor
public class SchoolController {

  private final SchoolService schoolService;

  @GetMapping("/code")
  public ResponseEntity<String> findSchoolCode(@RequestBody @Valid SchoolDto schoolDto) {
    try {
      String schoolCode = schoolService.getSchoolCode(schoolDto.getSchoolName());
      return new ResponseEntity<>(schoolCode, HttpStatus.OK);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
  }
}
