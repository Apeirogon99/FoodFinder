package com.mukkebi.foodfinder.core.integration;

import com.mukkebi.foodfinder.core.domain.Restaurant;
import com.mukkebi.foodfinder.core.infra.KakaoMapRestaurantFinder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
//@Disabled("실제 카카오맵 API를 호출하는 벤치마크 테스트 — 수동 실행 전용")
public class RestaurantBenchmarkTests {

    private static final Logger log = LoggerFactory.getLogger(RestaurantBenchmarkTests.class);

    @Autowired
    KakaoMapRestaurantFinder kakaoMapRestaurantFinder;

    record Scenario(String name, double latitude, double longitude, int radius) {}

    record BenchmarkResult(String scenario, int callNumber, int apiCalls, long elapsedMs, int restaurantCount, double estimatedCost) {}

    @Test
    @DisplayName("카카오맵 API 벤치마크: 다양한 상권 × 반경별 계측")
    void benchmarkKakaoMapApiCalls() {
        List<Scenario> scenarios = List.of(
                // 밀집 상권 — 강남역
                new Scenario("강남역 100m", 37.4979, 127.0276, 100),
                new Scenario("강남역 300m", 37.4979, 127.0276, 300),
                new Scenario("강남역 500m", 37.4979, 127.0276, 500),
                // 일반 상권 — 선릉역
                new Scenario("선릉역 100m", 37.5045, 127.0490, 100),
                new Scenario("선릉역 300m", 37.5045, 127.0490, 300),
                new Scenario("선릉역 500m", 37.5045, 127.0490, 500),
                // 저밀도 — 양재시민의숲
                new Scenario("양재시민의숲 100m", 37.4698, 127.0385, 100),
                new Scenario("양재시민의숲 300m", 37.4698, 127.0385, 300),
                new Scenario("양재시민의숲 500m", 37.4698, 127.0385, 500)
        );

        List<BenchmarkResult> results = new ArrayList<>();

        for (Scenario scenario : scenarios) {
            // 동일 조건 2회 연속 호출 — 캐싱 도입 시 비교 기준
            for (int call = 1; call <= 2; call++) {
                long startTime = System.nanoTime();
                List<Restaurant> restaurants = kakaoMapRestaurantFinder.findNearBy(
                        scenario.latitude(), scenario.longitude(), scenario.radius());
                long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

                int apiCalls = KakaoMapRestaurantFinder.getLastApiCallCount();
                double cost = apiCalls * 0.1;

                results.add(new BenchmarkResult(
                        scenario.name(), call, apiCalls, elapsedMs, restaurants.size(), cost));

                assertThat(restaurants).isNotNull();
            }
        }

        // 결과 표 출력
        log.info("\n========== 카카오맵 API 벤치마크 결과 ==========");
        log.info(String.format("%-20s | %s | %8s | %8s | %6s | %8s",
                "시나리오", "호출#", "API횟수", "소요시간", "음식점수", "예상비용"));
        log.info("-".repeat(75));

        for (BenchmarkResult r : results) {
            log.info(String.format("%-20s | %3d   | %6d회 | %6dms | %5d개 | %6.1f원",
                    r.scenario(), r.callNumber(), r.apiCalls(), r.elapsedMs(), r.restaurantCount(), r.estimatedCost()));
        }

        log.info("=".repeat(50));

        // 요약 통계
        int totalApiCalls = results.stream().mapToInt(BenchmarkResult::apiCalls).sum();
        double totalCost = results.stream().mapToDouble(BenchmarkResult::estimatedCost).sum();
        log.info("전체 API 호출 합계: {}회, 전체 예상비용: {}원", totalApiCalls, String.format("%.1f", totalCost));
    }
}
