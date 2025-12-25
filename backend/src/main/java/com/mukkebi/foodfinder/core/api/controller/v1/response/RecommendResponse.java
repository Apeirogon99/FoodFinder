package com.mukkebi.foodfinder.core.api.controller.v1.response;

import com.mukkebi.foodfinder.core.domain.Recommend;

public record RecommendResponse(
        Long recommendId,
        String restaurantId,
        String restaurantName,
        String category,
        String menu,
        String reason,
        Double distance
) {
    public static RecommendResponse from(Recommend recommend, String menu) {
        return new RecommendResponse(
                recommend.getId(),
                String.valueOf(recommend.getRestaurantId()),
                recommend.getRestaurantName(),
                recommend.getCategory(),
                menu,
                recommend.getReason(),
                recommend.getDistance()
        );
    }
}
