package com.mukkebi.foodfinder.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {

    Optional<HashTag> findByCode(String code);

    /**
     * 특정 식사 시간대를 포함하는 해시태그 목록 조회
     * ElementCollection JOIN으로 효율적인 쿼리 가능
     */
    @Query("SELECT DISTINCT h FROM HashTag h JOIN h.mealTypes mt WHERE mt = :mealType")
    List<HashTag> findAllByMealType(@Param("mealType") MealType mealType);

    List<HashTag> findAllByCodeIn(List<String> codes);
}
