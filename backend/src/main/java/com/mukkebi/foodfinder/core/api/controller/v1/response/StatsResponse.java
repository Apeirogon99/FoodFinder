package com.mukkebi.foodfinder.core.api.controller.v1.response;

public record StatsResponse(String label, Long value) {

    // 시간대별 통계 생성자
    public StatsResponse(Integer hour, Long value) {
        this((hour != null) ? hour + "시" : "미지정", value);
    }

    // 리뷰 점수 통계 생성자
    public StatsResponse(Double rating, Long value) {
        this((rating != null) ? rating + "점" : "0점", value);
    }

    // 추천 반응 통계 생성자
    public StatsResponse(com.mukkebi.foodfinder.core.enums.RecommendationResult result, Long value) {
        this((result != null) ? result.name() : "미판정", value);
    }
}
