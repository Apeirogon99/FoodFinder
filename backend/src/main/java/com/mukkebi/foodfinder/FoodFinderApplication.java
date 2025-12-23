package com.mukkebi.foodfinder;

import com.mukkebi.foodfinder.core.api.config.KakaoMapProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(KakaoMapProperties.class)
public class FoodFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodFinderApplication.class, args);
    }

}
