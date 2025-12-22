package com.mukkebi.foodfinder.core.api.controller.v1.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public record RestaurantSearchRequest(
        @NotNull(message = "위도는 필수입니다")
        Double latitude,

        @NotNull(message = "경도는 필수입니다")
        Double longitude,

        @Min(value = 100, message = "최소 반경은 100미터입니다")
        @Max(value = 500, message = "최대 반경은 500미터입니다")
        Integer radius
) {

}
