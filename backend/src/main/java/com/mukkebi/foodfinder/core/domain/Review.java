package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.enums.EntityStatus;
import com.mukkebi.foodfinder.storage.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    @Column(nullable = false)
    private Double rating;


    @Column(nullable=false, name = "user_id")
    private Long userId;

    @Column(nullable=false)
    private String nickname;

    @Column(nullable=false, name = "restaurant_id")
    private Long restaurantId;

    @Column(nullable=false)
    private String restaurantName;

    protected Review(String content, Double rating, Long userId,String nickname, Long restaurantId, String restaurantName) {
        this.content = content;
        this.rating = rating;
        this.userId = userId;
        this.nickname = nickname;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
    }

    public static Review create( String content, Double rating, Long userId,String nickname, Long restaurantId, String restaurantName) {
        return new Review(content, rating, userId,nickname, restaurantId,restaurantName);
    }

    public void update( String content, Double rating) {
        this.content = content;
        this.rating = rating;
    }

}
