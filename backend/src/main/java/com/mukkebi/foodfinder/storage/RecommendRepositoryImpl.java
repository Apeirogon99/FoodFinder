package com.mukkebi.foodfinder.storage;

import com.mukkebi.foodfinder.core.api.controller.v1.response.RecentActivityResponse;
import com.mukkebi.foodfinder.core.api.controller.v1.response.StatisticsResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecommendRepositoryImpl implements RecommendRepositoryCustom {

  private final EntityManager em;

    @Override
    public List<StatisticsResponse> findWeeklyStats(LocalDate from, LocalDate to, Long userId) {
        return em.createQuery("""
                SELECT new com.mukkebi.foodfinder.core.api.controller.v1.response.StatisticsResponse(
                    CASE function('DAYOFWEEK', r.createdAt)
                        WHEN 1 THEN '일' WHEN 2 THEN '월' WHEN 3 THEN '화'
                        WHEN 4 THEN '수' WHEN 5 THEN '목' WHEN 6 THEN '금'
                        WHEN 7 THEN '토' END, COUNT(r))
                FROM Recommend r
                WHERE r.createdAt BETWEEN :from AND :to
                AND (:userId IS NULL OR r.userId = :userId)
                GROUP BY function('DAYOFWEEK', r.createdAt)
                ORDER BY function('DAYOFWEEK', r.createdAt)
                """, StatisticsResponse.class)
                .setParameter("from", from.atStartOfDay())
                .setParameter("to", to.atTime(LocalTime.MAX))
                .setParameter("userId", userId)
                .getResultList();
    }


    @Override
  public List<StatisticsResponse> findCategoryStats(LocalDate from, LocalDate to, Long userId) {
    return em.createQuery("""
        SELECT new com.mukkebi.foodfinder.core.api.controller.v1.response.StatisticsResponse(r.category, COUNT(r))
          FROM Recommend r
         WHERE r.createdAt BETWEEN :from AND :to
           AND (:userId IS NULL OR r.userId = :userId)
         GROUP BY r.category
        """, StatisticsResponse.class)
        .setParameter("from", from.atStartOfDay())
        .setParameter("to", to.atTime(LocalTime.MAX))
        .setParameter("userId", userId)
        .getResultList();
  }

  @Override
  public List<StatisticsResponse> findHourlyStats(LocalDate from, LocalDate to, Long userId) {
    return em.createQuery("""
        SELECT new com.mukkebi.foodfinder.core.api.controller.v1.response.StatisticsResponse(
            CAST(function('date_part', 'hour', r.createdAt) AS integer), COUNT(r))
          FROM Recommend r
         WHERE r.createdAt BETWEEN :from AND :to
           AND (:userId IS NULL OR r.userId = :userId)
         GROUP BY CAST(function('date_part', 'hour', r.createdAt) AS integer)
         ORDER BY CAST(function('date_part', 'hour', r.createdAt) AS integer)
        """, StatisticsResponse.class)
        .setParameter("from", from.atStartOfDay())
        .setParameter("to", to.atTime(LocalTime.MAX))
        .setParameter("userId", userId)
        .getResultList();
  }

  @Override
  public List<StatisticsResponse> findReviewStats(LocalDate from, LocalDate to, Long userId) {
    return em.createQuery("""
        SELECT new com.mukkebi.foodfinder.core.api.controller.v1.response.StatisticsResponse(
            rv.rating, COUNT(rv))
          FROM Review rv
         WHERE rv.createdAt BETWEEN :from AND :to
           AND (:userId IS NULL OR rv.userId = :userId)
         GROUP BY rv.rating
         ORDER BY rv.rating DESC
        """, StatisticsResponse.class)
        .setParameter("from", from.atStartOfDay())
        .setParameter("to", to.atTime(LocalTime.MAX))
        .setParameter("userId", userId)
        .getResultList();
  }

  @Override
  public StatisticsResponse findDistanceStats(LocalDate from, LocalDate to, Long userId) {
    try {
      return em.createQuery("""
          SELECT new com.mukkebi.foodfinder.core.api.controller.v1.response.StatisticsResponse(
              '평균 이동 거리', CAST(AVG(r.distance) AS Long))
            FROM Recommend r
           WHERE r.createdAt BETWEEN :from AND :to
             AND (:userId IS NULL OR r.userId = :userId)
          """, StatisticsResponse.class)
          .setParameter("from", from.atStartOfDay())
          .setParameter("to", to.atTime(LocalTime.MAX))
          .setParameter("userId", userId)
          .getSingleResult();
    } catch (Exception e) {
      return new StatisticsResponse("평균 이동 거리", 0L);
    }
  }

  @Override
  public List<StatisticsResponse> findReactionStats(LocalDate from, LocalDate to, Long userId) {
    return em.createQuery("""
        SELECT new com.mukkebi.foodfinder.core.api.controller.v1.response.StatisticsResponse(
            r.result, COUNT(r))
          FROM Recommend r
         WHERE r.createdAt BETWEEN :from AND :to
           AND (:userId IS NULL OR r.userId = :userId)
         GROUP BY r.result
         ORDER BY r.result
        """, StatisticsResponse.class)
        .setParameter("from", from.atStartOfDay())
        .setParameter("to", to.atTime(LocalTime.MAX))
        .setParameter("userId", userId)
        .getResultList();
  }

  @Override
  public List<RecentActivityResponse> findRecentStats(Long userId, int limit) {
    return em.createQuery("""
        SELECT new com.mukkebi.foodfinder.core.api.controller.v1.response.RecentActivityResponse(
            r.id, r.restaurantName, r.category, r.createdAt, r.result)
          FROM Recommend r
         WHERE (:userId IS NULL OR r.userId = :userId)
         ORDER BY r.createdAt DESC
        """, RecentActivityResponse.class)
        .setParameter("userId", userId)
        .setMaxResults(limit)
        .getResultList();
  }
}
