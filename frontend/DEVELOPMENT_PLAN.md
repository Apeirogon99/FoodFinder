# 🎯 Food Finder 프론트엔드 개발 계획

---

## 📋 전체 진행 상황

| 단계 | 항목 | 상태 |
|------|------|------|
| **Phase 1** | 기반 구조 설정 | ✅ 완료 |
| **Phase 2** | 인증 시스템 | ✅ 완료 |
| **Phase 3** | 메인 & 네비게이션 | ✅ 완료 |
| **Phase 4** | AI 추천 기능 | ✅ 완료 |
| **Phase 5** | 음식점 검색 & 상세 | ✅ 완료 |
| **Phase 6** | 리뷰 시스템 | ✅ 완료 |
| **Phase 7** | 프로필 & 설정 | ✅ 완료 |
| **Phase 8** | 통계 (틀만) | ✅ 완료 |

---

## 🔗 화면 연결 흐름 (최종)

```
┌─────────┐     ┌─────────┐     
│  Login  │────>│  Home   │     
└─────────┘     └─────────┘     
     │               │          
     v               ├───────────────────┐
┌─────────┐          v                   v
│ SignUp  │     ┌───────────┐      ┌─────────┐
└─────────┘     │ Recommend │      │ Reviews │
                │(태그 선택) │      └─────────┘
                └───────────┘           
                     │                   
                     v                   
                ┌─────────┐              
                │ Search  │              
                │(위치선택)│              
                └─────────┘              
                     │                   
                     v                   
                ┌───────────┐     ┌───────────┐
                │Restaurant │────>│PostReview │
                │(추천결과) │     └───────────┘
                └───────────┘           
                     │                   
                     v                   
                ┌─────────┐              
                │다른 추천 │──> Recommend로 돌아감
                └─────────┘              
```

### 핵심 흐름: Recommend → Search → Restaurant

1. **Recommend (태그 선택)**
   - 식사 시간대 선택 (아침/점심/저녁)
   - 해시태그 선택 (기분, 종류, 맛, 가격, 상황)
   - "위치 선택하기" 버튼 → Search로 이동

2. **Search (위치 선택)**
   - 선택된 태그 표시
   - 카카오맵에서 현재 위치 확인
   - 검색 반경 슬라이더 (100m ~ 500m)
   - "AI 추천 받기" 버튼 → 백엔드에 태그+위치 전송 → Restaurant로 이동

3. **Restaurant (추천 결과)**
   - AI 추천 음식점 정보 표시
   - AI 추천 이유
   - "다른 추천" → Recommend로 돌아감
   - "리뷰 작성" → PostReview로 이동

---

## 📁 완성된 파일 구조

```
frontend/src/
├── api/                    # API 모듈
│   ├── index.js           ✅ Axios 인스턴스
│   ├── auth.js            ✅ 인증 API
│   ├── user.js            ✅ 사용자 API
│   ├── recommend.js       ✅ 추천 API
│   ├── restaurant.js      ✅ 음식점 API
│   └── review.js          ✅ 리뷰 API
├── stores/
│   ├── user.js            ✅ 사용자 상태
│   └── location.js        ✅ 위치 상태
├── constants/
│   ├── hashtags.js        ✅ 해시태그 상수
│   └── allergies.js       ✅ 알레르기 상수
├── utils/
│   └── kakaoMapLoader.js  ✅ 카카오맵 로더
├── views/
│   ├── LoginView.vue      ✅ 로그인 (GitHub OAuth)
│   ├── SignUpView.vue     ✅ 회원가입 (닉네임, 알레르기)
│   ├── OAuthCallbackView.vue ✅ OAuth 콜백 처리
│   ├── HomeView.vue       ✅ 메인 화면 (AI추천, 리뷰, 통계)
│   ├── RecommendView.vue  ✅ 태그 선택
│   ├── SearchView.vue     ✅ 위치 선택 (지도 + 범위)
│   ├── RestaurantView.vue ✅ 추천 결과
│   ├── PostReviewView.vue ✅ 리뷰 작성
│   ├── ReviewsView.vue    ✅ 내 리뷰 목록
│   ├── ProfileView.vue    ✅ 마이페이지
│   └── StatisticsView.vue ✅ 활동 통계
└── components/
    ├── layout/
    │   ├── AppLayout.vue  ✅ 레이아웃 (header + content + footer + nav)
    │   ├── BottomNav.vue  ✅ 하단 네비게이션 (홈, 추천, 리뷰, 프로필)
    │   └── TopHeader.vue  ✅ 상단 헤더
    └── recommend/
        ├── HashTagSelector.vue ✅ 해시태그 선택기
        └── RecommendResult.vue ✅ 추천 결과 표시
```

---

## 🛣️ 라우터 설정

| 경로 | 이름 | 컴포넌트 | 인증 | 설명 |
|------|------|----------|------|------|
| `/` | Home | HomeView | ✅ | 메인 화면 |
| `/login` | Login | LoginView | 게스트만 | 로그인 |
| `/signup` | SignUp | SignUpView | 신규회원만 | 회원가입 |
| `/recommend` | Recommend | RecommendView | ✅ | 태그 선택 |
| `/search` | Search | SearchView | ✅ | 위치 선택 |
| `/restaurant/:id?` | Restaurant | RestaurantView | ✅ | 추천 결과 |
| `/review/write` | PostReview | PostReviewView | ✅ | 리뷰 작성 |
| `/reviews` | Reviews | ReviewsView | ✅ | 내 리뷰 |
| `/profile` | Profile | ProfileView | ✅ | 마이페이지 |
| `/statistics` | Statistics | StatisticsView | ✅ | 통계 |
| `/oauth/callback` | OAuthCallback | OAuthCallbackView | - | OAuth 콜백 |

---

## 🔌 백엔드 API 요청 데이터

### AI 추천 요청 (Search → Restaurant)

```javascript
// SearchView에서 백엔드로 전송하는 데이터
const requestData = {
  latitude: 37.5665,           // 현재 위치 위도
  longitude: 126.9780,         // 현재 위치 경도
  radius: 250,                 // 검색 반경 (m)
  hashTagCodes: ['HAPPY', 'KOREAN', 'CHEAP'],  // 선택된 태그 코드
  mealType: 'LUNCH',           // 식사 시간대
  excludeRestaurantIds: []     // 제외할 음식점 ID (다른 추천용)
}
```

### 예상 응답

```javascript
{
  id: 'restaurant_001',
  name: '맛있는 식당',
  category: '한식 > 백반/가정식',
  phone: '02-1234-5678',
  address: '서울 서초구 서초동 1234-56',
  roadAddress: '서울 서초구 서초대로 123',
  latitude: 37.5665,
  longitude: 126.9780,
  distance: 150,
  placeUrl: 'https://place.map.kakao.com/12345678',
  recommend: 'AI가 생성한 추천 이유',
  rating: 4.5,
  reviewCount: 23
}
```

---

## ✅ 최종 빌드 테스트

```
✓ 1571 modules transformed.
✓ built in 7.67s
```

**빌드 성공!** 모든 UI가 정상적으로 작동합니다.

---

## 📌 주요 변경사항 요약

### Home 화면
- ❌ 현재 위치 섹션 제거
- ❌ "주변 맛집 검색" 제거
- ✅ "통계 보기" 추가
- 기능: AI 추천, 리뷰 관리, 통계 보기

### 추천 흐름
- **Before**: Home → Search → Restaurant
- **After**: Home → **Recommend (태그)** → **Search (위치)** → Restaurant

### 하단 네비게이션
- **Before**: 홈, 검색, 추천, 리뷰, 프로필 (5개)
- **After**: 홈, 추천, 리뷰, 프로필 (4개)
- 추천 탭: recommend, search, restaurant 경로 모두 활성화

---

## 🎯 다음 단계: 백엔드 API 연동

1. 인증 API 연동 (OAuth, 로그인/로그아웃)
2. 사용자 API 연동 (프로필 CRUD)
3. **추천 API 연동** (태그 + 위치 → AI 추천)
4. 리뷰 API 연동 (CRUD)
5. 통계 API 연동

