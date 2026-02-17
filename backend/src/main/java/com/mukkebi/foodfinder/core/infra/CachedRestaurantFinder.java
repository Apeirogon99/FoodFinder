package com.mukkebi.foodfinder.core.infra;

import com.mukkebi.foodfinder.core.domain.Restaurant;
import com.mukkebi.foodfinder.core.domain.RestaurantFinder;
import com.mukkebi.foodfinder.core.support.utils.CoordinateUtils;
import com.mukkebi.foodfinder.core.support.utils.RectangleBounds;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Primary
@Slf4j
public class CachedRestaurantFinder implements RestaurantFinder {

    private static final int GRID_SIZE_METERS = 100;
    private static final long CACHE_TTL_HOURS = 1;

    private final KakaoMapRestaurantFinder kakaoMapRestaurantFinder;
    private final RedisTemplate<String, Object> redisTemplate;

    public CachedRestaurantFinder(KakaoMapRestaurantFinder kakaoMapRestaurantFinder,
                                  RedisTemplate<String, Object> redisTemplate) {
        this.kakaoMapRestaurantFinder = kakaoMapRestaurantFinder;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Restaurant> findNearBy(Double latitude, Double longitude, Integer radius) {
        RectangleBounds bounds = CoordinateUtils.calculateRectangleBounds(latitude, longitude, radius);

        List<double[]> cells = CoordinateUtils.enumerateGridCells(bounds, GRID_SIZE_METERS);
        log.info("[셀 캐싱] 위치=({},{}), 반경={}m, 셀 수={}개", latitude, longitude, radius, cells.size());

        Map<String, Restaurant> allRestaurants = new HashMap<>();
        int hitCount = 0;
        int missCount = 0;

        for (double[] cell : cells) {
            double cellLat = cell[0];
            double cellLng = cell[1];
            String key = "grid:%f:%f".formatted(cellLat, cellLng);

            List<Restaurant> cached = getFromCache(key);
            if (cached != null) {
                hitCount++;
                for (Restaurant r : cached) {
                    allRestaurants.putIfAbsent(r.id(), r);
                }
            } else {
                missCount++;
                RectangleBounds cellBounds = CoordinateUtils.cellBounds(cellLat, cellLng, GRID_SIZE_METERS);
                List<Restaurant> result = kakaoMapRestaurantFinder.searchCellByRectangle(cellLat, cellLng, cellBounds);
                saveToCache(key, result);
                for (Restaurant r : result) {
                    allRestaurants.putIfAbsent(r.id(), r);
                }
            }
        }

        log.info("[셀 캐싱] HIT={}개, MISS={}개, 총 음식점={}개", hitCount, missCount, allRestaurants.size());
        return recalculateDistances(new ArrayList<>(allRestaurants.values()), latitude, longitude, radius);
    }

    @SuppressWarnings("unchecked")
    private List<Restaurant> getFromCache(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value instanceof List<?> list) {
                // 빈 리스트도 유효한 캐시 히트
                if (list.isEmpty()) {
                    return List.of();
                }
                // GenericJackson2JsonRedisSerializer는 record를 LinkedHashMap으로 역직렬화함
                if (list.get(0) instanceof LinkedHashMap) {
                    return list.stream()
                            .map(item -> {
                                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) item;
                                return Restaurant.builder()
                                        .id((String) map.get("id"))
                                        .name((String) map.get("name"))
                                        .category((String) map.get("category"))
                                        .phone((String) map.get("phone"))
                                        .address((String) map.get("address"))
                                        .roadAddress((String) map.get("roadAddress"))
                                        .latitude(((Number) map.get("latitude")).doubleValue())
                                        .longitude(((Number) map.get("longitude")).doubleValue())
                                        .distance(((Number) map.get("distance")).doubleValue())
                                        .placeUrl((String) map.get("placeUrl"))
                                        .build();
                            })
                            .toList();
                }
                return (List<Restaurant>) list;
            }
            return null;
        } catch (Exception e) {
            log.warn("[캐시 조회 실패] key={}, error={}", key, e.getMessage());
            return null;
        }
    }

    private void saveToCache(String key, List<Restaurant> restaurants) {
        try {
            redisTemplate.opsForValue().set(key, restaurants, CACHE_TTL_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("[캐시 저장 실패] key={}, error={}", key, e.getMessage());
        }
    }

    private List<Restaurant> recalculateDistances(List<Restaurant> restaurants,
                                                   double actualLat, double actualLng, int radius) {
        return restaurants.stream()
                .map(r -> Restaurant.builder()
                        .id(r.id())
                        .name(r.name())
                        .category(r.category())
                        .phone(r.phone())
                        .address(r.address())
                        .roadAddress(r.roadAddress())
                        .latitude(r.latitude())
                        .longitude(r.longitude())
                        .distance(CoordinateUtils.calculateDistance(actualLat, actualLng, r.latitude(), r.longitude()))
                        .placeUrl(r.placeUrl())
                        .build())
                .filter(r -> r.distance() <= radius)
                .sorted(Comparator.comparingDouble(Restaurant::distance))
                .toList();
    }
}
