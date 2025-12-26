package com.mukkebi.foodfinder.core.api.controller.v1.request;
import com.mukkebi.foodfinder.core.enums.EntityStatus;
import lombok.Getter;

@Getter
public class ReviewRequest {
    String content;
    Double rating;
    EntityStatus status;
}
