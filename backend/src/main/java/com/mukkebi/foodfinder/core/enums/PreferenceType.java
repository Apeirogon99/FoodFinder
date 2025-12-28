package com.mukkebi.foodfinder.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PreferenceType {

    // 음식 종류 기반
    KOREAN("종류", "한식"),
    WESTERN("종류", "양식"),
    CHINESE("종류", "중식"),
    JAPANESE("종류", "일식"),
    SOUTHEAST_ASIAN("종류", "동남아 음식"),
    ASIAN_FUSION("종류", "아시아 퓨전"),
    FAST_FOOD("종류", "패스트푸드"),
    DESSERT_BAKERY("종류", "디저트/베이커리"),
    VEGETARIAN_VEGAN("종류", "채식/비건"),
    SEAFOOD("종류", "해산물"),
    BBQ_GRILL("종류", "바비큐/구이류"),
    BUNSIK("종류", "분식"),
    SOUP_STEW("종류", "국/탕/찌개류"),
    NOODLES("종류", "면요리"),
    RICE_BOWL("종류", "밥/비빔류"),

    // 취향/스타일 기반
    SPICY("스타일", "매운 음식"),
    SWEET("스타일", "달달한 음식"),
    SALTY("스타일", "짠 음식"),
    LIGHT("스타일", "담백한 음식"),
    HEALTHY("스타일", "건강식"),
    LATE_NIGHT("스타일", "야식");

    private final String group;       // "종류", "스타일"
    private final String displayName; // 화면에 보여줄 이름 (한글)
}
