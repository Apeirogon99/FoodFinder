package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.api.controller.v1.request.UpdateProfileRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.UserProfileResponse;
import com.mukkebi.foodfinder.storage.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse updateMyProfile(String githubId, UpdateProfileRequest request) {
        User user = userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + githubId));

        // 기본 정보 수정
        user.changeNickname(request.nickname());

        // 기존 선호도/알레르기 초기화
        user.getPreferences().clear();
        user.getAllergies().clear();

        // 선호도 추가
        request.preferences().forEach(pr -> {
            UserPreference pref = UserPreference.builder()
                    .user(user)
                    .preferenceType(pr.preferenceType())
                    .liked(pr.liked())
                    .build();
            user.getPreferences().add(pref);
        });

        // 알레르기 추가
        request.allergies().forEach(ar -> {
            UserAllergy allergy = UserAllergy.builder()
                    .user(user)
                    .allergyType(ar.allergyType())
                    .build();
            user.getAllergies().add(allergy);
        });

        return UserProfileResponse.from(user);
    }
}
