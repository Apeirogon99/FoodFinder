package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "review",
        indexes = {
            @Index(name="idx_review_user",columnList="userId")
        }
)
public class ReviewEntity extends BaseEntity {

    @Column(nullable = true, length = 1000, name = "content")
    private String content;

    @Column(nullable=false, name = "rating")
    private Double rating;

    @Column(nullable=false, name = "user_id")
    private Long userId;

    @Column(nullable=false, name = "recommendation_id")
    private Long reconmmendationId;

}
