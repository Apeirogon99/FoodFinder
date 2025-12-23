package com.mukkebi.foodfinder.core.api.controller.v1;

import com.mukkebi.foodfinder.core.support.response.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

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
}
