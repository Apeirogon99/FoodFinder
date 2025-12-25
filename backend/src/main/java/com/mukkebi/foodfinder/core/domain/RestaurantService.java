package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.api.controller.v1.response.RestaurantResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantFinder restaurantFinder;

    /**
     * 위도, 경도 기준 주변에 있는 음식점 검색
     *
     * @param latitude  위도
     * @param longitude 경도
     * @param radius    범위
     */
    public RestaurantResponse searchNearbyRestaurants(Double latitude, Double longitude, Integer radius) {

        // 주변에 있는 음식점 조회
        List<Restaurant> restaurants = restaurantFinder.findNearBy(latitude, longitude, radius);
        return new RestaurantResponse(restaurants);
    }
}