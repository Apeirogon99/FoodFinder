package com.mukkebi.foodfinder.core.api.controller.v1.request;

import com.mukkebi.foodfinder.core.enums.AllergyType;

import java.util.List;

public record UpdateProfileRequest(
        String nickname,
        List<AllergyItem> allergies
) {

    public record AllergyItem(
            AllergyType allergyType
    ) {}
}
