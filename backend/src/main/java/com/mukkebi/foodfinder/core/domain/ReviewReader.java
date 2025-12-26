package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.api.controller.v1.response.ReviewListResponseByRestaurant;
import com.mukkebi.foodfinder.core.api.controller.v1.response.ReviewListResponseByUser;
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

@RequiredArgsConstructor
@Service
public class ReviewReader {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    //음식점 리뷰 조회
    @Transactional(readOnly = true)
    public ReviewListResponseByRestaurant getByRestaurant(Long restaurantId, Long cursorId) {

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

        List<ReviewResponse> responses = reviews.stream()
                .map(this::toResponse)
                .toList();

        Double averageRating =
                reviewRepository.findAverageRatingByRestaurantId(restaurantId);

        return new ReviewListResponseByRestaurant(
                responses,
                averageRating,
                nextCursor,
                hasNext
        );
    }

    //내 리뷰 조회
    @Transactional(readOnly = true)
    public ReviewListResponseByUser getMyReviews(
            OAuth2User oauth2User,
            Long cursorId
    ) {
        String githubId = "180543622"; // TODO 인증 연동

        User user = userRepository.findByGithubId(githubId)
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
                .map(this::toResponse)
                .toList();

        Double averageRating =
                reviewRepository.findAverageRatingByUserId(user.getId());

        return new ReviewListResponseByUser(
                responses,
                nextCursor,
                hasNext
        );
    }

    //AI
//    @Transactional(readOnly = true)
//    public List<Review> getRecentReviewsForAI(Long restaurantId) {
//        return reviewRepository.findRestaurantReviewsWithCursor(
//                restaurantId,
//                null,
//                20
//        );
//    }

    /* =========================
       Entity → Response 변환
       ========================= */
    private ReviewResponse toResponse(Review review) {

        User user = userRepository.findById(review.getUserId())
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        return new ReviewResponse(
                review.getContent(),
                review.getRating(),
                user.getNickname(),
                review.getRestaurantId()
        );
    }
}
