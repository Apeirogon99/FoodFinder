package com.mukkebi.foodfinder.storage;

import com.mukkebi.foodfinder.core.api.controller.v1.response.ExampleStatsResponse;
import com.mukkebi.foodfinder.core.domain.Review;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl
        implements ReviewRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Review> findRestaurantReviewsWithCursor(
            Long restaurantId,
            Long cursorId,
            int limit
    ) {
        return em.createQuery("""
            select r
            from Review r
            where r.restaurantId = :restaurantId
              and (:cursorId is null or r.id < :cursorId)
            order by r.id desc
        """, Review.class)
                .setParameter("restaurantId", restaurantId)
                .setParameter("cursorId", cursorId)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public List<Review> findUserReviewsWithCursor(
            Long userId,
            Long cursorId,
            int limit
    ) {
        return em.createQuery("""
            select r
            from Review r
            where r.userId = :userId
              and (:cursorId is null or r.id < :cursorId)
            order by r.id desc
        """, Review.class)
                .setParameter("userId", userId)
                .setParameter("cursorId", cursorId)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public Double findAverageRatingByRestaurantId(Long restaurantId) {
        return em.createQuery("""
            select avg(r.rating)
            from Review r
            where r.restaurantId = :restaurantId
        """, Double.class)
                .setParameter("restaurantId", restaurantId)
                .getSingleResult();
    }

    @Override
    public Double findAverageRatingByUserId(Long userId) {
        return em.createQuery("""
            select avg(r.rating)
            from Review r
            where r.userId = :userId
        """, Double.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }
}
