package com.mukkebi.foodfinder.core.api.controller.v1;

import com.mukkebi.foodfinder.core.api.controller.v1.request.ReviewRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.ReviewResponse;
import com.mukkebi.foodfinder.core.domain.ReviewService;
import com.mukkebi.foodfinder.core.support.error.CoreException;
import com.mukkebi.foodfinder.core.support.error.ErrorType;
import com.mukkebi.foodfinder.core.support.response.ApiResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    //후기 작성
    @PostMapping("/api/recommendations/{id}/review")
    public ApiResult<?> postReview(
            @RequestBody ReviewRequest reviewRequest,
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {

        System.out.println("🔥 controller 진입");
        System.out.println("oauth2User = " + oauth2User);

        if (oauth2User == null) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        String githubId = oauth2User.getAttribute("login");
        reviewService.saveReview(reviewRequest, id, githubId);
        return ApiResult.success();
    }

    //후기 수정
    @PutMapping("/api/reviews/{id}")
    public ApiResult<?> updateReview(
            @RequestBody ReviewRequest reviewRequest,
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        String githubId = oauth2User.getAttribute("login");
        reviewService.updateReview(reviewRequest, id, githubId);
        return ApiResult.success();
    }

    //후기 삭제
    @DeleteMapping("/api/review/{id}")
    public ApiResult<?> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        String githubId = oauth2User.getAttribute("login");
        reviewService.deleteReview(id, githubId);
        return ApiResult.success();
    }

    // 후기 조회
    @GetMapping("/api/users/me/reviews")
    public ApiResult<List<ReviewResponse>> getReviewsByUser(
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        String githubId = oauth2User.getAttribute("login");
        return ApiResult.success(reviewService.getMyReviews(githubId));
    }

}
