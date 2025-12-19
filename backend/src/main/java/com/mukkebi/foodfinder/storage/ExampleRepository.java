package com.mukkebi.foodfinder.storage;

import com.mukkebi.foodfinder.core.domain.Example;
import com.mukkebi.foodfinder.core.enums.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExampleRepository extends JpaRepository<Example, Long>, ExampleRepositoryCustom {
    boolean existsByExampleAndStatus(String example, EntityStatus status);
}
