package com.mukkebi.foodfinder.core.api.controller.v1;

import com.mukkebi.foodfinder.core.api.controller.v1.request.UpdateProfileRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.UserProfileResponse;
import com.mukkebi.foodfinder.core.domain.UserService;
import com.mukkebi.foodfinder.core.support.response.ApiResult;
import com.mukkebi.foodfinder.core.support.security.OAuthUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/api/v1/users/signup")
    public ApiResult<UserProfileResponse> signUp(
            @AuthenticationPrincipal OAuthUserPrincipal principal,
            @RequestBody UpdateProfileRequest request
    ) {
        return ApiResult.success(
                userService.signUp(principal.getUserId(), request)
        );
    }

    // 회원 정보 조회
    @GetMapping("/api/v1/users/profile")
    public ApiResult<UserProfileResponse> getProfile(
            @AuthenticationPrincipal OAuthUserPrincipal principal
    ) {
        return ApiResult.success(userService.getProfile(principal.getUserId()));
    }

    // 회원 정보 수정
    @PatchMapping("/api/v1/users/profile")
    public ApiResult<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal OAuthUserPrincipal principal,
            @RequestBody UpdateProfileRequest request
    ) {
        return ApiResult.success(userService.updateProfile(principal.getUserId(), request));
    }

    // 회원 탈퇴
    @DeleteMapping("/api/v1/users/me")
    public ApiResult<?> withdraw(
            @AuthenticationPrincipal OAuthUserPrincipal principal,
            HttpServletRequest request
    ) {
        userService.withdraw(principal.getUserId());
        request.getSession().invalidate();
        return ApiResult.success();
    }
}
