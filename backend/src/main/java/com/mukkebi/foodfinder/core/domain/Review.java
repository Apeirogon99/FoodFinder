package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.enums.EntityStatus;
import com.mukkebi.foodfinder.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "reviews",
        indexes = {
            @Index(name="idx_review_user",columnList="user_id"),
                @Index(name="idx_review_restaurant",columnList="restaurant_id")
        }
)
public class Review extends BaseEntity {

    @Column(length = 1000)
    private String content;

    @Column(nullable=false)
    private Double rating;

    @Column(nullable=false, name = "user_id")
    private Long userId;

    @Column(nullable=false, name = "restaurant_id")
    private Long restaurantId;

    protected Review(String content, Double rating, Long userId, Long restaurantId) {
        this.content = content;
        this.rating = rating;
        this.userId = userId;
        this.restaurantId = restaurantId;
    }

    public static Review create(String content, Double rating, Long userId, Long restaurantId) {
        return new Review(content, rating, userId, restaurantId);
    }

    public void update(String content, Double rating) {
        this.content = content;
        this.rating = rating;
    }

}
