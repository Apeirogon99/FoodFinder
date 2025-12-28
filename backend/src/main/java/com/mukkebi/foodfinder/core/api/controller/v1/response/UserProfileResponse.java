package com.mukkebi.foodfinder.core.api.controller.v1.response;

import com.mukkebi.foodfinder.core.domain.User;
import com.mukkebi.foodfinder.core.domain.UserAllergy;
import com.mukkebi.foodfinder.core.domain.UserPreference;

import java.util.List;

public record UserProfileResponse(
        String githubId,
        String nickname,
        List<PreferenceItem> preferences,
        List<AllergyItem> allergies
) {

    public static UserProfileResponse from(User user) {
        List<PreferenceItem> prefs = user.getPreferences().stream()
                .map(PreferenceItem::from)
                .toList();

        List<AllergyItem> alls = user.getAllergies().stream()
                .map(AllergyItem::from)
                .toList();

        return new UserProfileResponse(
                user.getGithubId(),
                user.getNickname(),
                prefs,
                alls
        );
    }

    public record PreferenceItem(
            String preferenceType
    ) {
        public static PreferenceItem from(UserPreference pref) {
            return new PreferenceItem(pref.getPreferenceType().name());
        }
    }

    public record AllergyItem(
            String allergyType
    ) {
        public static AllergyItem from(UserAllergy allergy) {
            return new AllergyItem(allergy.getAllergyType().name());
        }
    }
}
