package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.api.controller.v1.response.RestaurantReviewListResponse;
import com.mukkebi.foodfinder.core.api.controller.v1.response.UserReviewListResponse;
import com.mukkebi.foodfinder.core.api.controller.v1.response.ReviewResponse;
import com.mukkebi.foodfinder.core.support.error.CoreException;
import com.mukkebi.foodfinder.core.support.error.ErrorType;
import com.mukkebi.foodfinder.storage.ReviewRepository;
import com.mukkebi.foodfinder.storage.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewReader {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    // 음식점 리뷰 조회
    @Transactional(readOnly = true)
    public RestaurantReviewListResponse getByRestaurant(Long restaurantId, Long cursorId) {

        int limit = 20;

        List<Review> reviews =
                reviewRepository.findRestaurantReviewsWithCursor(
                        restaurantId,
                        cursorId,
                        limit + 1
                );

        boolean hasNext = reviews.size() > limit;
        if (hasNext) {
            reviews = reviews.subList(0, limit);
        }

        Long nextCursor = reviews.isEmpty()
                ? null
                : reviews.get(reviews.size() - 1).getId();

        //  userId 수집
        List<Long> userIds = reviews.stream()
                .map(Review::getUserId)
                .distinct()
                .toList();

        //  User 일괄 조회
        Map<Long, User> userMap =
                userRepository.findAllById(userIds).stream()
                        .collect(Collectors.toMap(User::getId, Function.identity()));

        // Review → ReviewResponse 변환
        List<ReviewResponse> responses = reviews.stream()
                .map(review ->
                        ReviewResponse.of(
                                review,
                                userMap.get(review.getUserId())
                        )
                )
                .toList();

        Double averageRating =
                reviewRepository.findAverageRatingByRestaurantId(restaurantId);

        return new RestaurantReviewListResponse(
                responses,
                averageRating,
                nextCursor,
                hasNext
        );
    }

    // 내 리뷰 조회
    @Transactional(readOnly = true)
    public UserReviewListResponse getMyReviews(
            Long userId,
            Long cursorId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        int limit = 20;

        List<Review> reviews =
                reviewRepository.findUserReviewsWithCursor(
                        user.getId(),
                        cursorId,
                        limit + 1
                );

        boolean hasNext = reviews.size() > limit;
        if (hasNext) {
            reviews = reviews.subList(0, limit);
        }

        Long nextCursor = reviews.isEmpty()
                ? null
                : reviews.get(reviews.size() - 1).getId();

        List<ReviewResponse> responses = reviews.stream()
                .map(review -> ReviewResponse.of(review, user))
                .toList();

        return new UserReviewListResponse(
                responses,
                nextCursor,
                hasNext
        );
    }
}
