package com.mukkebi.foodfinder.core.api.controller.v1;

import com.mukkebi.foodfinder.core.api.controller.v1.request.RestaurantSearchRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.RestaurantResponse;
import com.mukkebi.foodfinder.core.domain.RestaurantService;
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
public class RestaurantController {
    private final RestaurantService restaurantService;

    @PostMapping("/api/v1/restaurants/search")
    public ApiResult<RestaurantResponse> searchNearbyRestaurants(
            @Validated @RequestBody RestaurantSearchRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        return ApiResult.success(restaurantService.searchNearbyRestaurants(request.latitude(), request.longitude(), request.radius()));
    }
}
