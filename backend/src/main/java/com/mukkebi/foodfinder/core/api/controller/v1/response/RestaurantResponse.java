package com.mukkebi.foodfinder.core.api.controller.v1.response;

import com.mukkebi.foodfinder.core.domain.Restaurant;

import java.util.List;
import java.util.stream.Collectors;

public record RestaurantResponse(
        List<Restaurant> restaurants
) {

    public static RestaurantResponse from(List<KakaoPlaceResponse.Document> documents) {
        return new RestaurantResponse(
                documents.stream()
                        .map(Restaurant::from)
                        .collect(Collectors.toList())
        );
    }
}
