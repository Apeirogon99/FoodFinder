package com.mukkebi.foodfinder.core.domain;

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
            @Index(name="idx_review_user",columnList="user_id")
        }
)
public class ReviewEntity extends BaseEntity {

    @Column(length = 1000)
    private String content;

    @Column(nullable=false)
    private Double rating;

    @Column(nullable=false, name = "user_id")
    private Long userId;

    @Column(nullable=false, name = "recommendation_id")
    private Long recommendationId;

    protected ReviewEntity(String content, Double rating, Long userId, Long recommendationId) {
        this.content = content;
        this.rating = rating;
        this.userId = userId;
        this.recommendationId = recommendationId;
    }

    public static ReviewEntity create(String content, Double rating, Long userId, Long recommendationId) {
        return new ReviewEntity(content, rating, userId, recommendationId);
    }

    public void update(String content, Double rating) {
        this.content = content;
        this.rating = rating;
    }
}
