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

    // 회원가입
    public UserProfileResponse signUp(Long userId, UpdateProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원정보를 찾을 수 없습니다: " + userId));

        if (user.getUserStatus() != UserStatus.PENDING) {
            throw new IllegalStateException("이미 회원가입이 완료된 사용자입니다.");
        }

        applyProfile(user, request);
        user.completeSignup();

        return UserProfileResponse.from(user);
    }

    // 회원 정보 조회
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원정보를 찾을 수 없습니다: " + userId));

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("회원가입이 완료되지 않은 사용자입니다.");
        }

        return UserProfileResponse.from(user);
    }

    // 회원 정보 수정
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원정보를 찾을 수 없습니다: " + userId));

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("회원가입이 완료되지 않은 사용자입니다.");
        }

        applyProfile(user, request);

        return UserProfileResponse.from(user);
    }

    // 회원 탈퇴
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원정보를 찾을 수 없습니다."));

        userRepository.delete(user);
    }

    // 회원 정보 등록 및 수정 로직
    private void applyProfile(User user, UpdateProfileRequest request) {

        if (request.nickname() != null && !request.nickname().isBlank()) {
            user.changeNickname(request.nickname());
        }

        if (request.preferences() != null) {
            user.getPreferences().clear();
            request.preferences().forEach(pr -> {
                user.getPreferences().add(
                        UserPreference.builder()
                                .user(user)
                                .preferenceType(pr.preferenceType())
                                .build()
                );
            });
        }

        if (request.allergies() != null) {
            user.getAllergies().clear();
            request.allergies().forEach(ar -> {
                user.getAllergies().add(
                        UserAllergy.builder()
                                .user(user)
                                .allergyType(ar.allergyType())
                                .build()
                );
            });
        }
    }
}
