package com.mukkebi.foodfinder.core.api.controller.v1;

import com.mukkebi.foodfinder.core.api.controller.v1.request.AiRecommendRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.AiRecommendResponse;
import com.mukkebi.foodfinder.core.api.controller.v1.response.RestaurantDetailResponse;
import com.mukkebi.foodfinder.core.domain.RecommendService;
import com.mukkebi.foodfinder.core.support.response.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService aiRecommendService;

    @PostMapping("/api/v1/ai/recommend")
    public ApiResult<RestaurantDetailResponse> recommend(
            @Validated @RequestBody AiRecommendRequest request
    ) {
        return ApiResult.success(aiRecommendService.recommend(request));
    }
}
