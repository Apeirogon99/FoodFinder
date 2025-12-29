package com.mukkebi.foodfinder.storage;

import com.mukkebi.foodfinder.core.api.controller.v1.response.StatsResponse;

import java.time.LocalDate;
import java.util.List;

public interface RecommendRepositoryCustom {
    List<StatsResponse> findWeeklyStats(LocalDate from, LocalDate to, Long userId);

    List<StatsResponse> findCategoryStats(LocalDate from, LocalDate to, Long userId);

    List<StatsResponse> findHourlyStats(LocalDate from, LocalDate to, Long userId);

    List<StatsResponse> findReviewStats(LocalDate from, LocalDate to, Long userId);

    StatsResponse findDistanceStats(LocalDate from, LocalDate to, Long userId);

    List<StatsResponse> findReactionStats(LocalDate from, LocalDate to, Long userId);
}
