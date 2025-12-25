package com.mukkebi.foodfinder.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mukkebi.foodfinder.core.api.config.OpenAiProperties;
import com.mukkebi.foodfinder.core.support.error.CoreException;
import com.mukkebi.foodfinder.core.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/*
 * AI 기반 음식점 추천 클라이언트
 * OpenAI API를 호출하여 사용자 취향에 맞는 음식점을 추천
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendAiClient {

    private final WebClient openAiWebClient;
    private final OpenAiProperties openAiProperties;
    private final ObjectMapper objectMapper;

    private static final int MAX_RETRY = 2;

    /*
     * AI에게 음식점 추천 요청
     * @param prompt 프롬프트 (해시태그 설명 + 음식점 후보 목록)
     * @return AI 추천 결과 (restaurantId, restaurantName, menu, reason)
     */
    public AiRecommendResult requestRecommendation(String prompt) {
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                String response = callOpenAi(prompt);
                return parseResponse(response);
            } catch (CoreException e) {
                if (attempt == MAX_RETRY) {
                    throw e;
                }
                log.warn("AI 응답 파싱 실패, 재시도 중... (시도 {}/{})", attempt, MAX_RETRY);
            }
        }
        throw new CoreException(ErrorType.DEFAULT_ERROR, "AI 응답 파싱에 실패했습니다.");
    }

    private String callOpenAi(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", openAiProperties.model(),
                "messages", List.of(
                        Map.of("role", "system", "content", "당신은 음식점 추천 전문가입니다. 반드시 JSON 형식으로만 응답하세요."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7,
                "max_tokens", 500
        );

        try {
            String response = openAiWebClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("OpenAI API 호출 실패: {}", e.getMessage());
            throw new CoreException(ErrorType.DEFAULT_ERROR, "AI 서비스 호출에 실패했습니다.");
        }
    }

    private AiRecommendResult parseResponse(String content) {
        try {
            String jsonContent = extractJson(content);
            return objectMapper.readValue(jsonContent, AiRecommendResult.class);
        } catch (Exception e) {
            log.error("AI 응답 파싱 실패: {}", content);
            throw new CoreException(ErrorType.DEFAULT_ERROR, "AI 응답 파싱에 실패했습니다.");
        }
    }

    private String extractJson(String content) {
        content = content.trim();
        if (content.startsWith("```json")) {
            content = content.substring(7);
        }
        if (content.startsWith("```")) {
            content = content.substring(3);
        }
        if (content.endsWith("```")) {
            content = content.substring(0, content.length() - 3);
        }
        return content.trim();
    }

    /**
     * AI 추천 결과 (내부용)
     */
    public record AiRecommendResult(
            String restaurantId,
            String restaurantName,
            String menu,
            String reason
    ) {
    }
}
