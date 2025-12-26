package com.mukkebi.foodfinder.storage;

import com.mukkebi.foodfinder.core.domain.Review;

import java.util.List;

public interface ReviewRepositoryCustom {
    List<Review> findRestaurantReviewsWithCursor(
            Long restaurantId,
            Long cursorId,
            int limit
    );

    List<Review> findUserReviewsWithCursor(
            Long userId,
            Long cursorId,
            int limit
    );

    Double findAverageRatingByRestaurantId(Long restaurantId);

    Double findAverageRatingByUserId(Long userId);
}
