package com.mukkebi.foodfinder.core.integration;

import com.mukkebi.foodfinder.core.infra.KakaoMapRestaurantFinder;
import com.mukkebi.foodfinder.core.domain.Restaurant;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
@Disabled
public class RestaurantIntegrationTests {

    @Autowired
    KakaoMapRestaurantFinder kakaoMapRestaurantFinder;

    @Test
    @DisplayName("카카오맵 API 호출 테스트")
    void callKakaoMapRestaurantFinder() {
        Double latitude = 37.48645493735768;
        Double longitude = 127.02069449028099;
        Integer radius = 100;

        List<Restaurant> result = kakaoMapRestaurantFinder.findNearBy(latitude, longitude, radius);

        assertThat(result).isNotEmpty();
    }
}
