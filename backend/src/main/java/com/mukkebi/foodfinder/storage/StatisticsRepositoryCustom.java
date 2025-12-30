package com.mukkebi.foodfinder.storage;

import com.mukkebi.foodfinder.core.api.controller.v1.response.StatisticsResponse;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsRepositoryCustom {
    List<StatisticsResponse> findWeeklyStats(LocalDate from, LocalDate to, Long userId);

    List<StatisticsResponse> findCategoryStats(LocalDate from, LocalDate to, Long userId);

    List<StatisticsResponse> findHourlyStats(LocalDate from, LocalDate to, Long userId);

    List<StatisticsResponse> findReviewStats(LocalDate from, LocalDate to, Long userId);

    StatisticsResponse findDistanceStats(LocalDate from, LocalDate to, Long userId);

    List<StatisticsResponse> findReactionStats(LocalDate from, LocalDate to, Long userId);
}
