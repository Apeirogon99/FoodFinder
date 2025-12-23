package com.mukkebi.foodfinder.core.support.constances;

public class KakaoMapConstance {

    // 카카오맵 음식점 카테고리
    public static final String  CATEGORY_GROUP_CODE = "FD6";

    // 최소 분할 단위 (100 -> 50 -> 25)
    public static final int     MAX_SUBDIVISION_CELL_SIZE = 100;
    public static final int     MIN_SUBDIVISION_CELL_SIZE = 25;

    // 한번에 보여주는 음식점 개수
    public static final int     PAGE_SIZE = 15;

    // 45 = 15 * 3으로 API 가져와야함
    public static final int     MAX_PAGE = 3;

    private KakaoMapConstance() {
    }
}
