package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.type.descriptor.jdbc.TinyIntJdbcType;

import java.math.BigInteger;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "review",
        indexes = {
                @Index(name = "idx_review", columnList = "review", unique = true)
        }
)
public class ReviewEntity extends BaseEntity {

    @Column(nullable = true, length = 1000)
    private String content;

    @Column(nullable=false)
    private TinyIntJdbcType rating;

    @Column(nullable=false)
    private BigInteger user_id;

    @Column(nullable=false)
    private BigInteger recommendation_id;

}
