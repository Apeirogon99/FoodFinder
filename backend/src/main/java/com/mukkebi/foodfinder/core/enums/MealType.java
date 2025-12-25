package com.mukkebi.foodfinder.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * 식사 시간대 enum
 * 아침, 점심, 저녁 3가지 고정값을 사용한다.
 * HashTag의 @ElementCollection과 함께 사용
 */
@Getter
@RequiredArgsConstructor
public enum MealType {
    BREAKFAST("아침"),
    LUNCH("점심"),
    DINNER("저녁");

    private final String label;

    /*
     * 한글 라벨로 MealType 찾기
     */
    public static MealType fromLabel(String label) {
        for (MealType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown meal type label: " + label);
    }
}
