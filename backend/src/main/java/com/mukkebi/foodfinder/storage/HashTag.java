package com.mukkebi.foodfinder.storage;

import com.mukkebi.foodfinder.core.enums.MealType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/*
 * 해시태그 엔티티
 * - 사용자가 메뉴 추천 시 선택하는 해시태그 정보를 DB에 저장
 * - 해시태그 선택 조합을 저장하고 통계를 내기 위해 JPA 엔티티로 관리
 */
@Entity
@Table(name = "hash_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HashTag extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 50)
    private String label;

    @Column(nullable = false, length = 500)
    private String promptMessage;

    /*
     * 식사 시간대 (아침, 점심, 저녁)
     * - @ElementCollection으로 별도 테이블(hash_tag_meal_types) 자동 생성
     * - enum으로 정합성 보장, 인덱스 활용 가능
     */
    @ElementCollection
    @CollectionTable(
            name = "hash_tag_meal_types",
            joinColumns = @JoinColumn(name = "hash_tag_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type")
    private Set<MealType> mealTypes = new HashSet<>();

    @Builder
    public HashTag(String code, String label, String promptMessage, Set<MealType> mealTypes) {
        this.code = code;
        this.label = label;
        this.promptMessage = promptMessage;
        this.mealTypes = mealTypes != null ? mealTypes : new HashSet<>();
    }

    /*
     * 특정 식사 시간대에 해당하는지 확인
     */
    public boolean isAvailableFor(MealType mealType) {
        return this.mealTypes.contains(mealType);
    }

    /*
     * 식사 시간대 추가
     */
    public void addMealType(MealType mealType) {
        this.mealTypes.add(mealType);
    }
}
