package com.mukkebi.foodfinder.core.api.controller.v1;

import com.mukkebi.foodfinder.core.api.controller.v1.request.ReviewRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.ReviewResponse;
import com.mukkebi.foodfinder.core.domain.ReviewService;
import com.mukkebi.foodfinder.core.support.response.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    //후기 작성
    @PostMapping("/api/v1/reviews/{restaurantId}")
    public ApiResult<?> postReview(
            @RequestBody ReviewRequest reviewRequest,
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        reviewService.saveReview(reviewRequest, restaurantId, oauth2User);
        return ApiResult.success();
    }

    //후기 수정
    @PutMapping("/api/v1/reviews/{reviewId}")
    public ApiResult<?> updateReview(
            @RequestBody ReviewRequest reviewRequest,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        reviewService.updateReview(reviewRequest, reviewId, oauth2User);
        return ApiResult.success();
    }

    //후기 삭제
    @DeleteMapping("/api/v1/reviews/{reviewId}")
    public ApiResult<?> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        reviewService.deleteReview(reviewId, oauth2User);
        return ApiResult.success();
    }

    // 내 후기 조회
    @GetMapping("/api/v1/reviews/me")
    public ApiResult<List<ReviewResponse>> getReviewsByUser(
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        return ApiResult.success(reviewService.getMyReviews(oauth2User));
    }

    //음식점별 후기 조회
    @GetMapping("/api/v1/reviews/restaurants/{restaurantId}")
    public ApiResult<List<ReviewResponse>> getReviewsByRestaurant(@PathVariable Long restaurantId) {
        return ApiResult.success(reviewService.getByRestaurant(restaurantId));
    }

}
