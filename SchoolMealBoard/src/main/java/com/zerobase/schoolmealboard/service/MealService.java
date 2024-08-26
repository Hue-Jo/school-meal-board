package com.zerobase.schoolmealboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.zerobase.schoolmealboard.entity.School;

public interface MealService {

  void fetchAndSaveMealInfo();

  void processMealData(JsonNode rows, School school, String formattedDate);
}
