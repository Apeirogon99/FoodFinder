package com.mukkebi.foodfinder.storage;

import com.mukkebi.foodfinder.core.domain.Recommend;
import com.mukkebi.foodfinder.core.enums.RecommendationResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecommendRepository extends JpaRepository<Recommend, Long>, RecommendRepositoryCustom{

    List<Recommend> findByUserId(Long userId);

    List<Recommend> findByRestaurantId(Long restaurantId);

    // 사용자의 특정 상태 추천 목록 조회 (재추천 시 REJECTED 처리용)
    List<Recommend> findByUserIdAndResult(Long userId, RecommendationResult result);

    // 사용자의 특정 음식점 PENDING 추천 조회 (리뷰 작성 시 ACCEPTED 처리용)
    Optional<Recommend> findByUserIdAndRestaurantIdAndResult(Long userId, Long restaurantId, RecommendationResult result);
}
