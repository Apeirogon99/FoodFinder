package com.mukkebi.foodfinder.core.api.controller.v1;

import com.mukkebi.foodfinder.core.api.controller.v1.response.LoginSuccessResponse;
import com.mukkebi.foodfinder.core.domain.User;
import com.mukkebi.foodfinder.core.support.response.ApiResult;
import com.mukkebi.foodfinder.core.support.security.OAuthUserPrincipal;
import com.mukkebi.foodfinder.storage.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    // 로그인 성공 여부 확인
    @GetMapping("/api/v1/auth/login-success")
    public ApiResult<LoginSuccessResponse> loginSuccess(
            @AuthenticationPrincipal OAuthUserPrincipal principal
    ) {
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new IllegalStateException("회원정보를 찾을 수 없습니다."));

        return ApiResult.success(
                new LoginSuccessResponse(
                        user.getId(),
                        user.getUserStatus()
                )
        );
    }
}
