package com.mukkebi.foodfinder.core.api.controller.v1.response;


import com.mukkebi.foodfinder.core.domain.Review;

public record ReviewResponse(
        String content,
        Double rating,
        Long userId,
        Long restaurantId
) {
    public static ReviewResponse from(Review r) {
        return new ReviewResponse(
                r.getContent(),
                r.getRating(),
                r.getUserId(),
                r.getRestaurantId()
        );
    }
}