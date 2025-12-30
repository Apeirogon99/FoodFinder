package com.mukkebi.foodfinder.core.api.controller.v1;

import com.mukkebi.foodfinder.core.api.controller.v1.request.StatisticsRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.RecentActivityResponse;
import com.mukkebi.foodfinder.core.api.controller.v1.response.StatisticsResponse;
import com.mukkebi.foodfinder.core.domain.StatisticsService;
import com.mukkebi.foodfinder.core.domain.User;
import com.mukkebi.foodfinder.core.support.error.CoreException;
import com.mukkebi.foodfinder.core.support.error.ErrorType;
import com.mukkebi.foodfinder.core.support.response.ApiResult;
import com.mukkebi.foodfinder.storage.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final UserRepository userRepository;

    // --- [ User Endpoints: 내 통계 ] ---

    @GetMapping("/api/v1/stats/user/weekly")
    public ApiResult<List<StatisticsResponse>> getMyWeeklyStats(
            StatisticsRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {
        Long userId = resolveUserId(oauth2User);
        return ApiResult.success(statisticsService.getWeeklyStats(request.from(), request.to(), userId));
    }

    @GetMapping("/api/v1/stats/user/category")
    public ApiResult<List<StatisticsResponse>> getMyCategoryStats(
            StatisticsRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {
        Long userId = resolveUserId(oauth2User);
        return ApiResult.success(statisticsService.getCategoryStats(request.from(), request.to(), userId));
    }

    @GetMapping("/api/v1/stats/user/hourly")
    public ApiResult<List<StatisticsResponse>> getMyHourlyStats(
            StatisticsRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {
        Long userId = resolveUserId(oauth2User);
        return ApiResult.success(statisticsService.getHourlyStats(request.from(), request.to(), userId));
    }

    @GetMapping("/api/v1/stats/user/review")
    public ApiResult<List<StatisticsResponse>> getMyReviewStats(
            StatisticsRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {
        Long userId = resolveUserId(oauth2User);
        return ApiResult.success(statisticsService.getReviewStats(request.from(), request.to(), userId));
    }

    @GetMapping("/api/v1/stats/user/distance")
    public ApiResult<StatisticsResponse> getMyDistanceStats(
            StatisticsRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {
        Long userId = resolveUserId(oauth2User);
        return ApiResult.success(statisticsService.getDistanceStats(request.from(), request.to(), userId));
    }

    @GetMapping("/api/v1/stats/user/reaction")
    public ApiResult<List<StatisticsResponse>> getMyReactionStats(
            StatisticsRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {
        Long userId = resolveUserId(oauth2User);
        return ApiResult.success(statisticsService.getReactionStats(request.from(), request.to(), userId));
    }

    @GetMapping("/api/v1/stats/user/recent")
    public ApiResult<List<RecentActivityResponse>> getMyRecentStats(
            @AuthenticationPrincipal OAuth2User oauth2User) {
        Long userId = resolveUserId(oauth2User);
        return ApiResult.success(statisticsService.getRecentStats(userId));
    }

    // --- [ Admin Endpoints: 전체/타인 통계 ] ---

    @GetMapping("/api/v1/stats/admin/weekly")
    public ApiResult<List<StatisticsResponse>> getWeeklyStats(
            StatisticsRequest request,
            @RequestParam(required = false) Long userId) {
        return ApiResult.success(statisticsService.getWeeklyStats(request.from(), request.to(), userId));
    }

    @GetMapping("/api/v1/stats/admin/category")
    public ApiResult<List<StatisticsResponse>> getCategoryStats(
            StatisticsRequest request,
            @RequestParam(required = false) Long userId) {
        return ApiResult.success(statisticsService.getCategoryStats(request.from(), request.to(), userId));
    }

    @GetMapping("/api/v1/stats/admin/hourly")
    public ApiResult<List<StatisticsResponse>> getHourlyStats(
            StatisticsRequest request,
            @RequestParam(required = false) Long userId) {
        return ApiResult.success(statisticsService.getHourlyStats(request.from(), request.to(), userId));
    }

    @GetMapping("/api/v1/stats/admin/review")
    public ApiResult<List<StatisticsResponse>> getReviewStats(
            StatisticsRequest request,
            @RequestParam(required = false) Long userId) {
        return ApiResult.success(statisticsService.getReviewStats(request.from(), request.to(), userId));
    }

    @GetMapping("/api/v1/stats/admin/distance")
    public ApiResult<StatisticsResponse> getDistanceStats(
            StatisticsRequest request,
            @RequestParam(required = false) Long userId) {
        return ApiResult.success(statisticsService.getDistanceStats(request.from(), request.to(), userId));
    }

    @GetMapping("/api/v1/stats/admin/reaction")
    public ApiResult<List<StatisticsResponse>> getReactionStats(
            StatisticsRequest request,
            @RequestParam(required = false) Long userId) {
        return ApiResult.success(statisticsService.getReactionStats(request.from(), request.to(), userId));
    }

    // Helper Method
    private Long resolveUserId(OAuth2User oauth2User) {
        if (oauth2User == null) {
            throw new CoreException(ErrorType.UNAUTHORIZED, "로그인이 필요한 서비스입니다.");
        }
        Object idAttribute = oauth2User.getAttribute("id");
        if (idAttribute == null) {
            throw new CoreException(ErrorType.UNAUTHORIZED, "유효하지 않은 인증 정보입니다.");
        }
        String githubId = idAttribute.toString();
        User user = userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR, "사용자를 찾을 수 없습니다."));
        return user.getId();
    }
}