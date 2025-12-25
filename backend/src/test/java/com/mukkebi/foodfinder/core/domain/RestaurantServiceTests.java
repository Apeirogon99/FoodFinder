package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.api.controller.v1.response.RestaurantResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTests {

    @Mock
    RestaurantFinder restaurantFinder;

    @InjectMocks
    RestaurantService restaurantService;

    @Test
    @DisplayName("주변 음식점을 조회")
    void findNearByTest() {
        //given
        List<Restaurant> restaurants = List.of(
                new Restaurant("1", "음식점1", "음식점 > 큰 카테고리 > 작은 카테고리", "010-1234-4567", "주소1", "도로명1", 37.123, 127.456, 100.0, "https://음식점1"),
                new Restaurant("2", "음식점2", "음식점 > 큰 카테고리 > 작은 카테고리", "010-4567-1234", "주소2", "도로명2", 37.124, 127.457, 110.0, "https://음식점2")
        );
        given(restaurantFinder.findNearBy(37.0, 127.0, 500)).willReturn(restaurants);

        //when
        RestaurantResponse response = restaurantService.searchNearbyRestaurants(37.0, 127.0, 500);

        //then
        assertThat(response.restaurants()).hasSize(2);
    }
}