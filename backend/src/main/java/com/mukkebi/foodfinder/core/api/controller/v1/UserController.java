package com.mukkebi.foodfinder.core.api.controller.v1;

import com.mukkebi.foodfinder.core.api.controller.v1.request.UpdateProfileRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.UserProfileResponse;
import com.mukkebi.foodfinder.core.domain.UserService;
import com.mukkebi.foodfinder.core.support.response.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 로그인 성공 메세지 API
    @GetMapping("/api/users/login-success")
    public ApiResult<String> loginSuccess() {
        return ApiResult.success("GitHub 로그인 성공!");
    }

    // 로그인한 사용자의 GitHub 정보 조회 API
    @GetMapping("/github-profile")
    public ApiResult<Map<String, Object>> githubProfile(
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        return ApiResult.success(oauth2User.getAttributes());
    }

    // 회원가입 시 프로필 입력 API
    @PostMapping("/profile")
    public ApiResult<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RequestBody UpdateProfileRequest request
    ) {
        String githubId = oauth2User.getAttribute("login");
        UserProfileResponse response = userService.updateMyProfile(githubId, request);

        return ApiResult.success(response);
    }
}
