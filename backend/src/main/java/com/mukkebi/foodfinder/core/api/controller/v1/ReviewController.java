package com.mukkebi.foodfinder.core.api.controller.v1;

import com.mukkebi.foodfinder.core.api.controller.v1.request.ReviewRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.ReviewListResponseByRestaurant;
import com.mukkebi.foodfinder.core.api.controller.v1.response.ReviewListResponseByUser;
import com.mukkebi.foodfinder.core.domain.ReviewReader;
import com.mukkebi.foodfinder.core.domain.ReviewService;
import com.mukkebi.foodfinder.core.support.response.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewReader reviewReader;

    //리뷰 작성
    @PostMapping("/api/v1/reviews/{restaurantId}")
    public ApiResult<?> postReview(
            @RequestBody ReviewRequest reviewRequest,
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        reviewService.saveReview(reviewRequest, restaurantId, oauth2User);
        return ApiResult.success();
    }

    //리뷰 수정
    @PutMapping("/api/v1/reviews/{reviewId}")
    public ApiResult<?> updateReview(
            @RequestBody ReviewRequest reviewRequest,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        reviewService.updateReview(reviewRequest, reviewId, oauth2User);
        return ApiResult.success();
    }

    //리뷰 삭제
    @DeleteMapping("/api/v1/reviews/{reviewId}")
    public ApiResult<?> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        reviewService.deleteReview(reviewId, oauth2User);
        return ApiResult.success();
    }

   //내 리뷰 조회
    @GetMapping("/api/v1/reviews/me")
    public ApiResult<ReviewListResponseByUser> getMyReviews(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RequestParam(required = false) Long cursorId
    ) {
        return ApiResult.success(
                reviewReader.getMyReviews(oauth2User, cursorId)
        );
    }


    //음식점 리뷰 조회
    @GetMapping("/api/v1/restaurants/review/{restaurantId}")
    public ApiResult<ReviewListResponseByRestaurant> getReviewsByRestaurant(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) Long cursorId
    ) {
        return ApiResult.success(
                reviewReader.getByRestaurant(restaurantId, cursorId)
        );
    }



}
