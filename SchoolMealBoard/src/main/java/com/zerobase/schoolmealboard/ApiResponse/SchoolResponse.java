package com.zerobase.schoolmealboard.ApiResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchoolResponse {

  @JsonProperty("schoolInfo")
  private List<SchoolInfo> schoolInfo;


  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class SchoolInfo {

    @JsonProperty("row")
    private List<CodeAndName> row;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class CodeAndName {

    @JsonProperty("SD_SCHUL_CODE")
    private String schoolCode;

    @JsonProperty("SCHUL_NM")
    private String schoolName;
  }
}