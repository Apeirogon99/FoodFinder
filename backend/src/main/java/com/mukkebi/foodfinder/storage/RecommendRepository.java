package com.mukkebi.foodfinder.storage;

import com.mukkebi.foodfinder.core.domain.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendRepository extends JpaRepository<Recommend, Long>, RecommendRepositoryCustom{

    List<Recommend> findByUserId(Long userId);

    List<Recommend> findByRestaurantId(Long restaurantId);
}
