package com.zerobase.schoolmealboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {

  @NotNull(message = "날짜는 yyyy-mm-dd로 적어주세요 ex.2024-08-15")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate mealDate; // 급식날짜

  @NotBlank
  @Size(max = 20, message = "20자 이상은 제목으로 쓸 수 없습니다.")
  private String title;       // 리뷰제목

  @NotBlank
  @Size(max = 100, message = "100자만 입력할 수 있습니다.")
  private String content;     // 리뷰 내용

  @NotNull
  @Min(value = 1, message = "별점은 1점부터 줄 수 있습니다.")
  @Max(value = 5, message = "별점은 5점까지만 줄 수 있습니다.")
  private int rating;     // 별점

  private String imgUrl;  // 사진은 선택


  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static  class EditReviewDto {

    @Size(max = 20, message = "20자 이상은 제목으로 쓸 수 없습니다.")
    private String title;       // 리뷰제목

    @Size(max = 100, message = "100자만 입력할 수 있습니다.")
    private String content;     // 리뷰 내용

    @Min(value = 1, message = "별점은 1점부터 줄 수 있습니다.")
    @Max(value = 5, message = "별점은 5점까지만 줄 수 있습니다.")
    private int rating;     // 별점

    private String imgUrl;  // 사진은 선택
  }

}
