package com.mukkebi.foodfinder.storage;

import com.mukkebi.foodfinder.core.api.controller.v1.response.ExampleResponse;
import com.mukkebi.foodfinder.core.api.controller.v1.response.ExampleStatsResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class ExampleRepositoryCustomImpl implements ExampleRepositoryCustom {
    private final EntityManager em;

    @Override
    public ExampleStatsResponse getStats(LocalDate from, LocalDate to) {
        return em.createQuery("""
                SELECT new com.mukkebi.foodfinder.core.api.controller.v1.response.ExampleStatsResponse(
                    COUNT(e)
                )
                FROM Example e
                WHERE e.createdAt BETWEEN :from AND :to
                """, ExampleStatsResponse.class)
                .setParameter("from", from.atStartOfDay())
                .setParameter("to", to.atTime(23, 59, 59))
                .getSingleResult();
    }
}
