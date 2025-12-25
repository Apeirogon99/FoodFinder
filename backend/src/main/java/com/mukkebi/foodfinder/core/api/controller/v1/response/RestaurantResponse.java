package com.mukkebi.foodfinder.core.api.controller.v1.response;

import com.mukkebi.foodfinder.core.domain.Restaurant;

import java.util.List;

public record RestaurantResponse(
        List<Restaurant> restaurants
) {

}
