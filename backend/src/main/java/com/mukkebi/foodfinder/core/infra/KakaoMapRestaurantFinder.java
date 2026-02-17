package com.mukkebi.foodfinder.core.infra;

import com.mukkebi.foodfinder.core.api.config.KakaoMapProperties;
import com.mukkebi.foodfinder.core.api.controller.v1.response.KakaoPlaceResponse;
import com.mukkebi.foodfinder.core.domain.Restaurant;
import com.mukkebi.foodfinder.core.domain.RestaurantFinder;
import com.mukkebi.foodfinder.core.support.constances.KakaoMapConstance;
import com.mukkebi.foodfinder.core.support.error.CoreException;
import com.mukkebi.foodfinder.core.support.error.ErrorType;
import com.mukkebi.foodfinder.core.support.utils.CoordinateUtils;
import com.mukkebi.foodfinder.core.support.utils.RectangleBounds;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class KakaoMapRestaurantFinder implements RestaurantFinder {
    private final RestTemplate kakaoRestTemplate;
    private final KakaoMapProperties kakaoMapProperties;
    private static final ThreadLocal<Integer> apiCallCounter = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Integer> lastApiCallCount = ThreadLocal.withInitial(() -> 0);

    @Override
    public List<Restaurant> findNearBy(Double latitude, Double longitude, Integer radius) {
        apiCallCounter.set(0);
        long startTime = System.nanoTime();

        try {
            Map<String, Restaurant> restaurants = new HashMap<>();

            // Radius로 초기 검색
            KakaoPlaceResponse initResponse = searchByRadius(latitude, longitude, radius);
            if (initResponse.meta().canFetch()) {
                List<Restaurant> result = searchByRadiusRestaurants(latitude, longitude, radius);
                return filterAndSortByDistance(result, radius);
            }

            // Rect로 추가 검색
            RectangleBounds initialBounds = CoordinateUtils.calculateRectangleBounds(latitude, longitude, radius);
            searchByRectangleRecursive(latitude, longitude, initialBounds, restaurants, KakaoMapConstance.MAX_SUBDIVISION_CELL_SIZE);

            return filterAndSortByDistance(restaurants.values().stream().toList(), radius);
        } finally {
            int totalApiCalls = apiCallCounter.get();
            long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;
            double estimatedCost = totalApiCalls * 0.1;
            log.info("[계측] 위치=({},{}), 반경={}m, API호출={}회, 소요시간={}ms, 예상비용={}원",
                    latitude, longitude, radius, totalApiCalls, elapsedMs, estimatedCost);
            lastApiCallCount.set(totalApiCalls);
            apiCallCounter.remove();
        }
    }

    /**
     * 마지막 findNearBy() 호출에서 발생한 API 호출 횟수를 반환한다 (벤치마크 테스트용).
     */
    public static int getLastApiCallCount() {
        return lastApiCallCount.get();
    }

    /**
     * 마지막 API 호출 횟수 카운터를 초기화한다 (벤치마크 테스트용).
     */
    public static void resetLastApiCallCount() {
        lastApiCallCount.set(0);
    }

    /**
     * 단일 셀의 사각형 영역 내 음식점을 검색한다.
     * API 호출 수를 추적하여 벤치마크에서 측정 가능하도록 한다.
     */
    public List<Restaurant> searchCellByRectangle(double cellLat, double cellLng, RectangleBounds bounds) {
        apiCallCounter.set(0);
        List<Restaurant> result = searchByRectangleRestaurants(cellLat, cellLng, bounds);
        lastApiCallCount.set(lastApiCallCount.get() + apiCallCounter.get());
        apiCallCounter.remove();
        return result;
    }

    /**
     * 1페이지만 주변 탐색 및 범위로 정렬
     *
     * @param latitude  위도
     * @param longitude 경도
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
     * @param latitude  위도
     * @param longitude 경도
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
     * @param latitude  위도
     * @param longitude 경도
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
     * @param latitude  위도
     * @param longitude 경도
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
                .toList();
    }
}