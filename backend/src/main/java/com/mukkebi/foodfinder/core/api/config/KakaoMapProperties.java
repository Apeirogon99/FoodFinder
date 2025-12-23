package com.mukkebi.foodfinder.core.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao.api")
public record KakaoMapProperties(
        String key,
        String baseUrl
) {

}
