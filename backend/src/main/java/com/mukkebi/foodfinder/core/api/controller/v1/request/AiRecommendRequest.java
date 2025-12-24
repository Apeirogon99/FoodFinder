package com.mukkebi.foodfinder.core.api.controller.v1.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AiRecommendRequest(
        @NotNull(message = "위도는 필수입니다")
        Double latitude,

        @NotNull(message = "경도는 필수입니다")
        Double longitude,

        @NotNull(message = "반경은 필수입니다")
        @Min(value = 100, message = "반경은 최소 100m 이상이어야 합니다")
        @Max(value = 500, message = "반경은 최대 500m 이하여야 합니다")
        Integer radius,

        @NotEmpty(message = "해시태그는 최소 1개 이상 선택해야 합니다")
        List<String> hashTagCodes,

        List<Long> excludeRestaurantIds
) {
}
