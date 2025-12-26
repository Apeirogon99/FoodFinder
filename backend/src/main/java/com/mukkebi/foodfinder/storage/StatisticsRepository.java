package com.mukkebi.foodfinder.storage;

import com.mukkebi.foodfinder.core.domain.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticsRepository extends JpaRepository<Recommend, Long>, StatisticsRepositoryCustom {
}
