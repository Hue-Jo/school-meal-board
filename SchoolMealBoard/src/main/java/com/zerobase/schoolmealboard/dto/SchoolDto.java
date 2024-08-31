package com.zerobase.schoolmealboard.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SchoolDto {

  @NotBlank(message = "학교이름을 공백없이 작성해주세요")
  private String schoolName;

}
