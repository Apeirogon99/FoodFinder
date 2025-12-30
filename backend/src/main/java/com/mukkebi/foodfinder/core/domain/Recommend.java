package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.enums.RecommendationResult;
import com.mukkebi.foodfinder.storage.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "recommends")
public class Recommend extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(name = "restaurant_name", nullable = false)
    private String restaurantName;

    @Column(name = "category", length = 50, nullable = false)
    private String category;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    @Column(name = "road_address", length = 255, nullable = false)
    private String roadAddress;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "distance", nullable = false)
    private Double distance;

    @Column(name = "reason", nullable = false, length = 1000)
    private String reason;

    @Column(name = "menu", length = 255)
    private String menu;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false)
    private RecommendationResult result = RecommendationResult.PENDING;

    // --- [ Snapshot Fields ] ---

    @Column(nullable = false)
    private String restaurantNameAtTime;

    @Column(nullable = false)
    private String categoryAtTime;

    @Column(nullable = false)
    private Double distanceAtTime;

    @Column(nullable = false)
    private Double preferredDistanceAtTime;

    @Builder
    private Recommend(Long userId, Long restaurantId, String restaurantName, String category,
            String address, String roadAddress, Double latitude, Double longitude,
            Double distance, String reason, String menu, RecommendationResult result,
            String restaurantNameAtTime, String categoryAtTime,
            Double distanceAtTime, Double preferredDistanceAtTime) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.category = category;
        this.address = address;
        this.roadAddress = roadAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.reason = reason;
        this.menu = menu;
        this.result = result != null ? result : RecommendationResult.PENDING;
        this.restaurantNameAtTime = restaurantNameAtTime;
        this.categoryAtTime = categoryAtTime;
        this.distanceAtTime = distanceAtTime;
        this.preferredDistanceAtTime = preferredDistanceAtTime;
    }

    public static Recommend create(Long userId, Long restaurantId, String restaurantName,
            String category, String address, String roadAddress,
            Double latitude, Double longitude, Double distance,
            String reason, String menu) {
        return Recommend.builder()
                .userId(userId)
                .restaurantId(restaurantId)
                .restaurantName(restaurantName)
                .category(category)
                .address(address)
                .roadAddress(roadAddress)
                .latitude(latitude)
                .longitude(longitude)
                .distance(distance)
                .reason(reason)
                .menu(menu)
                .result(RecommendationResult.PENDING)  // 기본값: PENDING
                // 스냅샷 필드 자동 주입
                .restaurantNameAtTime(restaurantName)
                .categoryAtTime(category)
                .distanceAtTime(distance)
                .preferredDistanceAtTime(0.0)
                .build();
    }

    /**
     * 추천 결과 업데이트
     */
    public void updateResult(RecommendationResult result) {
        this.result = result;
    }
}
