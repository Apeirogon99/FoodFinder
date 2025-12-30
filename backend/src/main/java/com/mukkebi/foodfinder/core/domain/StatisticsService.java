package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.api.controller.v1.response.RecentActivityResponse;
import com.mukkebi.foodfinder.core.api.controller.v1.response.StatisticsResponse;
import com.mukkebi.foodfinder.storage.RecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticsService {

    private final RecommendRepository recommendRepository;

    // 1. 주간 통계 (요일별)
    public List<StatisticsResponse> getWeeklyStats(LocalDate from, LocalDate to, Long userId) {
        return recommendRepository.findWeeklyStats(from, to, userId);
    }

    // 2. 카테고리별 통계
    public List<StatisticsResponse> getCategoryStats(LocalDate from, LocalDate to, Long userId) {
        return recommendRepository.findCategoryStats(from, to, userId);
    }

    // 3. 시간대별 통계
    public List<StatisticsResponse> getHourlyStats(LocalDate from, LocalDate to, Long userId) {
        return recommendRepository.findHourlyStats(from, to, userId);
    }

    // 4. 리뷰 평점 분포 통계
    public List<StatisticsResponse> getReviewStats(LocalDate from, LocalDate to, Long userId) {
        return recommendRepository.findReviewStats(from, to, userId);
    }

    // 5. 평균 이동 거리 통계
    public StatisticsResponse getDistanceStats(LocalDate from, LocalDate to, Long userId) {
        return recommendRepository.findDistanceStats(from, to, userId);
    }

    // 6. 추천 반응 통계
    public List<StatisticsResponse> getReactionStats(LocalDate from, LocalDate to, Long userId) {
        return recommendRepository.findReactionStats(from, to, userId);
    }

    // 7. 최근 활동 내역
    public List<RecentActivityResponse> getRecentStats(Long userId) {
        return recommendRepository.findRecentStats(userId, 5); // 최근 5건 제한
    }
}
