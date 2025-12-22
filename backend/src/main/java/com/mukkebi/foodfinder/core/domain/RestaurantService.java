package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.api.config.KakaoMapProperties;
import com.mukkebi.foodfinder.core.api.controller.v1.response.KakaoPlaceResponse;
import com.mukkebi.foodfinder.core.api.controller.v1.response.RestaurantResponse;
import com.mukkebi.foodfinder.core.support.constances.KakaoMapConstance;
import com.mukkebi.foodfinder.core.support.error.CoreException;
import com.mukkebi.foodfinder.core.support.error.ErrorType;
import com.mukkebi.foodfinder.core.support.utils.CoordinateUtils;
import com.mukkebi.foodfinder.core.support.utils.RectangleBounds;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestTemplate kakaoRestTemplate;
    private final KakaoMapProperties kakaoMapProperties;
    private static final ThreadLocal<Integer> apiCallCounter = ThreadLocal.withInitial(() -> 0);

    /**
     * 위도, 경도 기준 주변에 있는 음식점 검색
     *
     * @param latitude  위도
     * @param longitude 경도
     * @param radius    범위
     */
    public RestaurantResponse searchNearbyRestaurants(Double latitude, Double longitude, Integer radius) {
        apiCallCounter.set(0);

        try {
            Map<String, Restaurant> restaurants = new HashMap<>();

            // Radius로 초기 검색
            KakaoPlaceResponse initResponse = searchByRadius(latitude, longitude, radius);
            if (initResponse.meta().canFetch()) {
                List<Restaurant> result = searchByRadiusRestaurants(latitude, longitude, radius);
                return new RestaurantResponse(filterAndSortByDistance(result, radius));
            }

            // Rect로 추가 검색
            RectangleBounds initialBounds = CoordinateUtils.calculateRectangleBounds(latitude, longitude, radius);
            searchByRectangleRecursive(latitude, longitude, initialBounds, restaurants, KakaoMapConstance.MAX_SUBDIVISION_CELL_SIZE);

            return new RestaurantResponse(filterAndSortByDistance(restaurants.values().stream().toList(), radius));
        } finally {
            int totalApiCalls = apiCallCounter.get();
            log.info("총 API 호출 횟수: {}회", totalApiCalls);
            apiCallCounter.remove();
        }

    }

    /**
     * 1페이지만 주변 탐색 및 범위로 정렬
     *
     * @param latitude  경도
     * @param longitude 위도
     * @param radius    범위
     */
    private KakaoPlaceResponse searchByRadius(Double latitude, Double longitude, Integer radius) {
        String url = UriComponentsBuilder
                .fromHttpUrl(kakaoMapProperties.baseUrl() + "/v2/local/search/category.json")
                .queryParam("category_group_code", KakaoMapConstance.CATEGORY_GROUP_CODE)
                .queryParam("x", longitude)
                .queryParam("y", latitude)
                .queryParam("radius", radius)
                .queryParam("size", KakaoMapConstance.PAGE_SIZE)
                .queryParam("page", 1)
                .queryParam("sort", "distance")
                .build()
                .toUriString();

        return executeApiCall(url);
    }

    /**
     * 주변에 있는 음식점 전부 조회
     *
     * @param latitude  경도
     * @param longitude 위도
     * @param radius    범위
     */
    private List<Restaurant> searchByRadiusRestaurants(Double latitude, Double longitude, Integer radius) {
        List<Restaurant> restaurants = new ArrayList<>();

        for (int page = 1; page <= KakaoMapConstance.PAGE_SIZE; page++) {
            String url = UriComponentsBuilder
                    .fromHttpUrl(kakaoMapProperties.baseUrl() + "/v2/local/search/category.json")
                    .queryParam("category_group_code", KakaoMapConstance.CATEGORY_GROUP_CODE)
                    .queryParam("x", longitude)
                    .queryParam("y", latitude)
                    .queryParam("radius", radius)
                    .queryParam("size", KakaoMapConstance.PAGE_SIZE)
                    .queryParam("page", page)
                    .queryParam("sort", "distance")
                    .build()
                    .toUriString();

            KakaoPlaceResponse response = executeApiCall(url);
            restaurants.addAll(
                    response.documents().stream()
                            .map(Restaurant::from)
                            .toList()
            );

            if (response.meta().isEnd()) {
                break;
            }
        }

        return restaurants;
    }

    /**
     * 재귀 적으로 사각형을 4등분하여 검색
     *
     * @param latitude    위도
     * @param longitude   경도
     * @param bounds      사각형 크기 (위도, 경도 기반)
     * @param restaurants 음식점 저장 Map(PlaceId, 음식점)
     * @param size        이전 사각형 크기
     */
    private void searchByRectangleRecursive(Double latitude, Double longitude, RectangleBounds bounds, Map<String, Restaurant> restaurants, int size) {

        KakaoPlaceResponse response = searchByRectangle(latitude, longitude, bounds);
        if (response.meta().canFetch()) {
            searchByRectangleRestaurants(latitude, longitude, bounds).forEach(restaurant -> restaurants.putIfAbsent(restaurant.id(), restaurant));
            return;
        }

        if (response.meta().isEmpty()) {
            return;
        }

        if (size < KakaoMapConstance.MIN_SUBDIVISION_CELL_SIZE) {
            searchByRectangleRestaurants(latitude, longitude, bounds).forEach(restaurant -> restaurants.putIfAbsent(restaurant.id(), restaurant));
            return;
        }

        RectangleBounds[] subRectangles = CoordinateUtils.subdivideRectangle(bounds);
        for (RectangleBounds subRect : subRectangles) {
            searchByRectangleRecursive(latitude, longitude, subRect, restaurants, size / 2);
        }
    }

    /**
     * 1페이지만 사각형안에 있는 음식점 조회
     *
     * @param latitude  경도
     * @param longitude 위도
     * @param bounds    사각형 크기 (위도, 경도 기반)
     */
    private KakaoPlaceResponse searchByRectangle(Double latitude, Double longitude, RectangleBounds bounds) {
        String url = UriComponentsBuilder
                .fromHttpUrl(kakaoMapProperties.baseUrl() + "/v2/local/search/category.json")
                .queryParam("category_group_code", KakaoMapConstance.CATEGORY_GROUP_CODE)
                .queryParam("x", longitude)
                .queryParam("y", latitude)
                .queryParam("rect", bounds.toRectParameter())
                .queryParam("size", KakaoMapConstance.PAGE_SIZE)
                .queryParam("page", 1)
                .queryParam("sort", "distance")
                .build()
                .toUriString();

        return executeApiCall(url);
    }

    /**
     * 사각형안에 있는 모든 음식점 조회
     *
     * @param latitude  경도
     * @param longitude 위도
     * @param bounds    사각형 크기 (위도, 경도 기반)
     */
    private List<Restaurant> searchByRectangleRestaurants(Double latitude, Double longitude, RectangleBounds bounds) {
        List<Restaurant> restaurants = new ArrayList<>();

        for (int page = 1; page <= KakaoMapConstance.PAGE_SIZE; page++) {
            String url = UriComponentsBuilder
                    .fromHttpUrl(kakaoMapProperties.baseUrl() + "/v2/local/search/category.json")
                    .queryParam("category_group_code", KakaoMapConstance.CATEGORY_GROUP_CODE)
                    .queryParam("x", longitude)
                    .queryParam("y", latitude)
                    .queryParam("rect", bounds.toRectParameter())
                    .queryParam("size", KakaoMapConstance.PAGE_SIZE)
                    .queryParam("page", page)
                    .queryParam("sort", "distance")
                    .build()
                    .toUriString();

            KakaoPlaceResponse response = executeApiCall(url);
            restaurants.addAll(
                    response.documents().stream()
                            .map(Restaurant::from)
                            .toList()
            );

            if (response.meta().isEnd()) {
                break;
            }
        }

        return restaurants;
    }

    /**
     * Kakao Map API 호출
     *
     * @param url 호출할 url
     */
    private KakaoPlaceResponse executeApiCall(String url) {
        try {
            apiCallCounter.set(apiCallCounter.get() + 1);
            ResponseEntity<KakaoPlaceResponse> response = kakaoRestTemplate.getForEntity(url, KakaoPlaceResponse.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("카카오 API 호출 실패: {}", e.getMessage(), e);
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
    }

    /**
     * 범위보다 긴 거리는 제외하고 거리순으로 정렬
     *
     * @param restaurants 음식점들
     * @param radius      범위
     */
    private List<Restaurant> filterAndSortByDistance(List<Restaurant> restaurants, int radius) {
        return restaurants.stream()
                .filter(r -> r.distance() <= radius)
                .sorted(Comparator.comparingDouble(Restaurant::distance))
                .collect(Collectors.toList());
    }
}
