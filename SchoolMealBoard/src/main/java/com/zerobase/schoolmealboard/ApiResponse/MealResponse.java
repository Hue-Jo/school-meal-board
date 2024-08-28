package com.zerobase.schoolmealboard.ApiResponse;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MealResponse {

  @JsonProperty("mealServiceDietInfo")
  private List<MealServiceDietInfo> mealServiceDietInfo;


  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class MealServiceDietInfo {

    private List<MealRow> row;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class MealRow {

    @JsonProperty("MLSV_YMD")
    private String mealDate;

    @JsonProperty("DDISH_NM")
    private String mealName;
  }
}