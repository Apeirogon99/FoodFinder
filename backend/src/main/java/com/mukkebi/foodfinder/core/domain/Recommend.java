package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.storage.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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

    @Column(name = "reason", nullable = false)
    private String reason;
}
