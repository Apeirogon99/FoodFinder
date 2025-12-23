package com.mukkebi.foodfinder.core.api.controller.v1.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

public record KakaoPlaceResponse(
        Meta meta,
        List<Document> documents
) {
    public record Meta(
            @JsonProperty("total_count")
            Integer totalCount,

            @JsonProperty("pageable_count")
            Integer pageableCount,

            @JsonProperty("is_end")
            Boolean isEnd
    ) {
        public boolean canFetch() {
            return totalCount <= pageableCount;
        }

        public boolean isEmpty() {
            return totalCount == 0 || pageableCount == 0;
        }

        public Boolean isEnd() {
            return isEnd;
        }
    }

    public record Document(
            String id,

            @JsonProperty("place_name")
            String placeName,

            @JsonProperty("category_name")
            String categoryName,

            @JsonProperty("phone")
            String phone,

            @JsonProperty("address_name")
            String addressName,

            @JsonProperty("road_address_name")
            String roadAddressName,

            @JsonProperty("x")
            String longitude,

            @JsonProperty("y")
            String latitude,

            @JsonProperty("place_url")
            String placeUrl,

            String distance
    ) {

    }
}