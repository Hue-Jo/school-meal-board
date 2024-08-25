package com.zerobase.schoolmealboard.controller;

import com.zerobase.schoolmealboard.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/school")
@RequiredArgsConstructor
public class SchoolController {

  private final SchoolService schoolService;

}
