package com.mukkebi.foodfinder.core.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class KakaoMapConfig {
    private final KakaoMapProperties kakaoMapProperties;

    @Bean
    public RestTemplate kakaoRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(List.of((request, body, execution) -> {
            request.getHeaders().set("Authorization", "KakaoAK " + kakaoMapProperties.key());
            return execution.execute(request, body);
        }));
        return restTemplate;
    }
}
