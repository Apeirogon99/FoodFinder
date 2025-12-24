package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.api.controller.v1.request.AiRecommendRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.AiRecommendResponse;
import com.mukkebi.foodfinder.core.api.controller.v1.response.RestaurantDetailResponse;
import com.mukkebi.foodfinder.core.support.error.CoreException;
import com.mukkebi.foodfinder.core.support.error.ErrorType;
import com.mukkebi.foodfinder.storage.HashTag;
import com.mukkebi.foodfinder.storage.HashTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendService {

    private final RestaurantService restaurantService;
    private final HashTagRepository hashTagRepository;
    private final OpenAiClient openAiClient;

    public RestaurantDetailResponse recommend(AiRecommendRequest request) {
        // 1. 해시태그 코드 → 설명(promptMessage) 조회
        List<HashTag> hashTags = hashTagRepository.findAllByCodeIn(request.hashTagCodes());
        if (hashTags.isEmpty()) {
            throw new CoreException(ErrorType.DEFAULT_ERROR, "선택한 해시태그를 찾을 수 없습니다.");
        }

        List<String> hashTagDescriptions = hashTags.stream()
                .map(HashTag::getPromptMessage)
                .toList();

        // 2. 카카오맵 API로 음식점 후보 목록 조회
        List<Restaurant> candidates = restaurantService
                .searchNearbyRestaurants(request.latitude(), request.longitude(), request.radius())
                .restaurants();

        if (candidates.isEmpty()) {
            throw new CoreException(ErrorType.DEFAULT_ERROR, "주변에 추천할 음식점이 없습니다.");
        }

        // 3. 이미 추천된 음식점 제외 (재추천 로직)
        Set<String> excludeIds = request.excludeRestaurantIds() != null
                ? request.excludeRestaurantIds().stream().map(String::valueOf).collect(Collectors.toSet())
                : Set.of();

        List<Restaurant> filteredCandidates = candidates.stream()
                .filter(r -> !excludeIds.contains(r.id()))
                .toList();

        if (filteredCandidates.isEmpty()) {
            throw new CoreException(ErrorType.DEFAULT_ERROR, "모든 음식점이 이미 추천되었습니다. 반경을 넓혀주세요.");
        }

        // 4. 프롬프트 생성
        String prompt = buildPrompt(hashTagDescriptions, filteredCandidates, excludeIds);
        log.debug("AI 프롬프트: {}", prompt);

        // 5. AI API 호출 및 응답 파싱
        return openAiClient.requestRecommendation(prompt);
    }

    private String buildPrompt(List<String> hashTagDescriptions, List<Restaurant> restaurants, Set<String> excludeIds) {
        StringBuilder sb = new StringBuilder();

        sb.append("당신은 음식점 추천 전문가입니다.\n\n");

        // 사용자 취향 (해시태그 설명)
        sb.append("## 사용자 취향\n");
        sb.append("사용자가 원하는 음식점 특징:\n");
        for (int i = 0; i < hashTagDescriptions.size(); i++) {
            sb.append(String.format("%d. %s\n", i + 1, hashTagDescriptions.get(i)));
        }
        sb.append("\n");

        // 음식점 후보 목록
        sb.append("## 음식점 후보 목록 (JSON)\n");
        sb.append("```json\n[\n");
        for (int i = 0; i < restaurants.size(); i++) {
            Restaurant r = restaurants.get(i);
            sb.append(String.format(
                    "  {\"id\": \"%s\", \"name\": \"%s\", \"category\": \"%s\", \"distance\": %.0fm}",
                    r.id(), r.name(), r.category(), r.distance()
            ));
            if (i < restaurants.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]\n```\n\n");

        // 제외 조건
        if (!excludeIds.isEmpty()) {
            sb.append("## 제외 조건\n");
            sb.append("다음 ID의 음식점은 이미 추천되었으므로 제외하세요: ");
            sb.append(String.join(", ", excludeIds));
            sb.append("\n\n");
        }

        // 출력 규칙
        sb.append("## 출력 규칙\n");
        sb.append("1. 위 후보 중에서 사용자 취향에 가장 적합한 음식점 1곳만 추천하세요.\n");
        sb.append("2. 반드시 추천할 가게를 정한이후 출력 규칙을 따라라.\n");
        sb.append("3. 카카오맵 api 호출결과의 placeUrl 의 링크로 들어가서 메뉴를 보고 메뉴를 추천하여라.\n\n");
        sb.append("4. 추천 이유를 1~2문장으로 설명하세요.\n");
        sb.append("5. 반드시 아래 JSON 형식으로만 응답하세요. 다른 텍스트는 포함하지 마세요.\n\n");
        sb.append("```json\n");
        sb.append("{\n");
        sb.append("  \"restaurantId\": \"음식점 ID\",\n");
        sb.append("  \"restaurantName\": \"음식점 이름\",\n");
        sb.append("  \"menu\": \"추천 메뉴 (카테고리 기반 추측)\",\n");
        sb.append("  \"reason\": \"추천 이유\"\n");
        sb.append("}\n");
        sb.append("```");

        return sb.toString();
    }
}
