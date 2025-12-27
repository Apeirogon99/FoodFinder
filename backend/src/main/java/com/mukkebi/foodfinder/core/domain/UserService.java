package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.api.controller.v1.request.UpdateProfileRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.UserProfileResponse;
import com.mukkebi.foodfinder.core.enums.UserStatus;
import com.mukkebi.foodfinder.storage.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 회원 가입
    public UserProfileResponse signUp(Long userId, UpdateProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원정보를 찾을 수 없습니다: " + userId));

        if (user.getUserStatus() != UserStatus.PENDING) {
            throw new IllegalStateException("이미 회원가입이 완료된 사용자입니다.");
        }

        user.completeSignup();

        return UserProfileResponse.from(user);
    }
}
