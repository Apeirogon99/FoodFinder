package com.mukkebi.foodfinder.core.api.controller.v1;

import com.mukkebi.foodfinder.core.api.controller.v1.request.RecommendRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.RestaurantDetailResponse;
import com.mukkebi.foodfinder.core.domain.RecommendService;
import com.mukkebi.foodfinder.core.support.response.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @PostMapping("/api/v1/ai/recommend")
    public ApiResult<RestaurantDetailResponse> recommend(
            @Validated @RequestBody RecommendRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        return ApiResult.success(recommendService.recommend(request, oauth2User));
    }
}
