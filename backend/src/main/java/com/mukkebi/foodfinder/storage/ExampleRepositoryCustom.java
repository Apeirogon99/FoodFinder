package com.mukkebi.foodfinder.storage;

import com.mukkebi.foodfinder.core.api.controller.v1.response.ExampleResponse;
import com.mukkebi.foodfinder.core.api.controller.v1.response.ExampleStatsResponse;

import java.time.LocalDate;

public interface ExampleRepositoryCustom {
    ExampleStatsResponse getStats(LocalDate from, LocalDate to);
}
