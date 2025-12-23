package com.mukkebi.foodfinder.core.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum RecommendKeyword {

    // 태그를 선택하면 ai 에게 promptMessage 를 전송해서 ai가 해시태그대로 메뉴 추천 . 프론트에서는 가변인자...type을 사용하여서 아점저로 나누기
    // ================= 아침 =================
    // 아침은 주로 속을 풀거나, 가볍게 시작하는 키워드 위주
    HANGOVER("해장 필요", "전날 마신 술이 깰 수 있도록 국물이 시원하거나 속이 편안한", "아침", "점심"),
    LIGHT("가볍게!", "아침에 먹어도 부담스럽지 않고 소화가 잘 되는 깔끔한", "아침", "점심", "저녁"),
    BAKERY("빵&커피", "갓 구운 빵 냄새와 향긋한 커피를 함께 즐길 수 있는 브런치 스타일의", "아침", "점심"),
    PORRIDGE("속 편한 죽", "부드럽고 따뜻해서 빈속에도 부담 없이 먹기 좋은", "아침"),

    // ================= 점심  =================
    // 점심은 직장인/학생 위주로 빠르고, 효율적이고, 든든한 키워드 위주
    QUICK("빠르게!", "주문하면 금방 나오고 회전율이 빨라 소중한 점심시간을 아낄 수 있는", "아침", "점심"),
    SOLO("혼밥 가능", "혼자 가도 눈치 보지 않고 편안하게 식사할 수 있는 1인석이 있거나 조용한", "아침", "점심", "저녁"),
    COST_EFFECTIVE("가성비 갑", "주머니 사정이 가벼워도 배부르고 맛있게 먹을 수 있는 혜자로운", "점심", "저녁"),
    HEARTY("든든하게", "오후 일정을 버틸 수 있도록 고기나 밥 위주의 양이 푸짐한", "점심", "저녁"),
    DIET("다이어트", "칼로리가 낮고 샐러드나 건강식 위주로 구성된 신선한", "아침", "점심", "저녁"),

    // ================= 저녁 =================
    // 저녁은 감성, 술, 스트레스 해소, 모임 위주
    SPICY("매운게 땡겨", "하루의 스트레스가 확 풀릴 정도로 입안이 얼얼하고 자극적인", "점심", "저녁"),
    FLEX("FLEX!", "오늘 하루,혹은 긴시간동안 고생한 나에게 보상해주는 고급스럽고 가격대가 있는", "저녁"),
    DATE("데이트", "조명이 은은하고 인테리어가 예뻐서 대화 나누기 좋은 로맨틱한", "점심", "저녁"),
    ALCOHOL("술 한잔어때요?", "식사와 함께 가볍게 반주를 곁들이거나 안주가 맛있는", "저녁"),
    NOISY("시끌벅적한 노포!", "친구들과 모여서 크게 떠들며 회포를 풀 수 있는 활기찬 분위기의노포", "저녁"),
    RAINY("비오는 날", "빗소리를 들으며 파전에 막걸리, 혹은 따뜻한 국물이 생각나는", "점심", "저녁"),
    MSG("자극적인 맛", "매콤하고 단짠단짠의 정석으로 입맛을 확 당기는 중독성 있는", "점심", "저녁"),
    YASIK("야식에 어울리는","야식에 걸맞은 칼로리가 높고 맥주가 땡기는 메뉴", "저녁");

    private final String label;         // 화면 표시용
    private final String promptMessage; // AI 지시용
    private final List<String> availableMealTypes; // 노출될 식사 시간대 (아침, 점심, 저녁)


    // 가변 인자(...types)를 사용하여 여러 시간대를 쉽게 입력하게 냅두기
    RecommendKeyword(String label, String promptMessage, String... types) {
        this.label = label;
        this.promptMessage = promptMessage;
        this.availableMealTypes = Arrays.asList(types);
    }
}