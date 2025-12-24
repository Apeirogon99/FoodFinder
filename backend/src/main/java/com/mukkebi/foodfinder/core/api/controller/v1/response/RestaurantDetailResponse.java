package com.mukkebi.foodfinder.core.api.controller.v1.response;

import com.mukkebi.foodfinder.core.domain.Restaurant;

public record RestaurantDetailResponse(
        String id,
        String name,
        String category,
        String phone,
        String address,
        String roadAddress,
        Double latitude,
        Double longitude,
        Double distance,
        String placeUrl,

        String recommend,
        Double rating
) {

    public static RestaurantDetailResponse of(Restaurant restaurant, String recommend, Double rating) {
        return new RestaurantDetailResponse(
                restaurant.id(),
                restaurant.name(),
                restaurant.category(),
                restaurant.phone(),
                restaurant.address(),
                restaurant.roadAddress(),
                restaurant.latitude(),
                restaurant.longitude(),
                restaurant.distance(),
                restaurant.placeUrl(),

                recommend,
                rating
        );
    }
}
