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
        name = "examples",
        indexes = {
                @Index(name = "idx_example", columnList = "example", unique = true)
        }
)
public class Example extends BaseEntity {

    @Column(name = "example")
    private String example;
}
