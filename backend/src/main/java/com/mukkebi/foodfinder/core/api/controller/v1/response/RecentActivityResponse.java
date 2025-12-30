package com.mukkebi.foodfinder.core.api.controller.v1.response;

import com.mukkebi.foodfinder.core.enums.RecommendationResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record RecentActivityResponse(
        Long id,
        String name,
        String category,
        String visitDate,
        RecommendationResult result) {
    public RecentActivityResponse(Long id, String name, String category, LocalDateTime createdAt,
            RecommendationResult result) {
        this(id, name, category,
                createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("MM/dd")) : "",
                result);
    }
}
