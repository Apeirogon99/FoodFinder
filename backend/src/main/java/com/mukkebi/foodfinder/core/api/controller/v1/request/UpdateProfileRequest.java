package com.mukkebi.foodfinder.core.api.controller.v1.request;

import com.mukkebi.foodfinder.core.enums.AllergyType;
import com.mukkebi.foodfinder.core.enums.PreferenceType;

import java.util.List;

public record UpdateProfileRequest(
        String nickname,
        List<PreferenceItem> preferences,
        List<AllergyItem> allergies
) {
    public record PreferenceItem(
            PreferenceType preferenceType,
            boolean liked
    ) {}

    public record AllergyItem(
            AllergyType allergyType
    ) {}
}
