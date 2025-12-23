package com.mukkebi.foodfinder.storage;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/*해시태그 엔티티
사용자가 메뉴 추천 시 선택하는 해시태그 정보를 DB에 저장
해시태그 선택 조합을 저장하고 통계를 내기 위해 JPA 엔티티로 관리 by 관호님
 */
@Entity
@Table(name = "hash_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HashTag extends BaseEntity {
    @Id
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 50)
    private String label;

    @Column(nullable = false, length = 500)
    private String promptMessage;

    // 아침, 점심, 저녁을 쉼표로 구분하여 저장 (예: "아침,점심" 또는 "점심,저녁")
    @Column(nullable = false, length = 50)
    private String mealTypes;

    @Builder
    public HashTag(String code, String label, String promptMessage, String mealTypes) {
        this.code = code;
        this.label = label;
        this.promptMessage = promptMessage;
        this.mealTypes = mealTypes;
    }

    //  mealTypes 문자열을 List로 변환
    public List<String> getMealTypeList() {
        return Arrays.asList(this.mealTypes.split(","));
    }

    // 특정 식사 시간대에 해당하는지 확인

    public boolean isAvailableFor(String mealType) {
        return getMealTypeList().contains(mealType);
    }
}
