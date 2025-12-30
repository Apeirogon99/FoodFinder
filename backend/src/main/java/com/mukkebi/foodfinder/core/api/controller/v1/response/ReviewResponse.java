package com.mukkebi.foodfinder.core.api.controller.v1.response;

import com.mukkebi.foodfinder.core.domain.Review;
import com.mukkebi.foodfinder.core.domain.User;
import com.mukkebi.foodfinder.core.support.error.CoreException;
import com.mukkebi.foodfinder.core.support.error.ErrorType;
import com.mukkebi.foodfinder.storage.UserRepository;

public record ReviewResponse(
        String content,
        Double rating,
        String userNickname,
        String restaurantName
) {
    public static ReviewResponse of(Review review, User user) {
        return new ReviewResponse(
                review.getContent(),
                review.getRating(),
                user.getNickname(),
                review.getRestaurantName()
        );
    }
}

