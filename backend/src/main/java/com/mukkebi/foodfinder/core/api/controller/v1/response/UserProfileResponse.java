package com.mukkebi.foodfinder.core.api.controller.v1.response;

import com.mukkebi.foodfinder.core.domain.User;
import com.mukkebi.foodfinder.core.domain.UserAllergy;

import java.util.List;

public record UserProfileResponse(
        String githubId,
        String nickname,
        List<AllergyItem> allergies
) {

    public static UserProfileResponse from(
            User user,
            List<UserAllergy> allergies
    ) {
        return new UserProfileResponse(
                user.getGithubId(),
                user.getNickname(),
                allergies.stream()
                        .map(AllergyItem::from)
                        .toList()
        );
    }

    public record AllergyItem(
            String allergyType
    ) {
        public static AllergyItem from(UserAllergy allergy) {
            return new AllergyItem(allergy.getAllergyType().name());
        }
    }
}
