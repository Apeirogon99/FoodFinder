package com.mukkebi.foodfinder.core.api.controller.v1.request;
import lombok.Getter;

@Getter
public class ReviewRequest {
    String content;
    Double rating;
}
