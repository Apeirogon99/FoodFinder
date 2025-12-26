package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.storage.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "result", nullable = false)
    private String result;

    @Builder
    private Recommend(Long userId, Long restaurantId, String restaurantName, String category,
                      String address, String roadAddress, Double latitude, Double longitude,
                      Double distance, String reason, String menu, String result) {
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
        this.result = result;
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
                .build();
    }
}
