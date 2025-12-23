package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.api.controller.v1.response.KakaoPlaceResponse;
import lombok.Builder;

@Builder
public record Restaurant(
        String id,
        String name,
        String category,
        String phone,
        String address,
        String roadAddress,
        Double latitude,
        Double longitude,
        Double distance,
        String placeUrl
) {
    public static Restaurant from(KakaoPlaceResponse.Document document) {
        return Restaurant.builder()
                .id(document.id())
                .name(document.placeName())
                .category(document.categoryName())
                .phone(document.phone())
                .address(document.addressName())
                .roadAddress(document.roadAddressName())
                .latitude(Double.parseDouble(document.latitude()))
                .longitude(Double.parseDouble(document.longitude()))
                .distance(Double.parseDouble(document.distance()))
                .placeUrl(document.placeUrl())
                .build();
    }
}