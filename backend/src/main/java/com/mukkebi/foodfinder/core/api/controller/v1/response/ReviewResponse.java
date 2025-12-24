package com.mukkebi.foodfinder.core.api.controller.v1.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewResponse {
    String content;
    Double rating;
    Long userId;
    Long recommendationId;
}
