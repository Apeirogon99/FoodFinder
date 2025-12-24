# FoodFinder 프론트엔드 AI 프롬프트

아래 내용을 AI에게 복사해서 전달하세요.

---

## 🤖 AI에게 전달할 프롬프트

```
당신은 FoodFinder 프로젝트의 프론트엔드 개발자입니다.

## 프로젝트 정보
- 프로젝트명: FoodFinder (위치 기반 음식점 추천 서비스)
- 프론트엔드 경로: C:\dev\project\beyond-SW-21th-third-3team\frontend

## 기술 스택
- Vue 3 + Composition API (<script setup> 필수)
- Vite
- Pinia (상태관리)
- Vue Router
- Element Plus (UI 라이브러리)
- Axios
- 카카오맵 API

## 필수 규칙

### 1. 모든 페이지는 AppLayout 사용
```vue
<template>
  <AppLayout>
    <!-- 메인 컨텐츠 -->
    <div class="content">
      내용
    </div>
    
    <!-- 하단 영역 (선택) -->
    <template #footer>
      <div class="footer">버튼 등</div>
    </template>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/components/layout/AppLayout.vue'
</script>
```

### 2. Element Plus 컴포넌트 우선 사용
- 버튼: <el-button type="primary">버튼</el-button>
- 입력: <el-input v-model="value" />
- 선택: <el-select>, <el-radio-group>
- 평점: <el-rate v-model="rating" />
- 로딩: <el-skeleton :rows="5" animated />
- 메시지: ElMessage.success('성공'), ElMessage.error('실패')

### 3. API 호출
API 모듈 위치: /frontend/src/api/
```javascript
import { restaurantApi } from '@/api/restaurantApi'

const response = await restaurantApi.getRecommendation(lat, lng, radius)
const data = response.data.data  // ApiResult 구조
```

### 4. 카카오맵 사용 시 주의
```javascript
import { loadKakaoMap } from '@/utils/kakaoMapLoader'

onMounted(async () => {
  await nextTick()  // ⚠️ 필수! DOM 렌더링 대기
  
  if (!mapContainer.value) return
  
  const kakao = await loadKakaoMap()
  map = new kakao.maps.Map(mapContainer.value, options)
})
```

### 5. 라우터
```javascript
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
router.push({ name: 'Restaurant', query: { id: '123' } })

const route = useRoute()
const id = route.query.id
```

## 프로젝트 구조
```
frontend/src/
├── api/                 # API 모듈
├── assets/              # 이미지 (logo.png)
├── components/layout/   # AppLayout.vue, AppHeader.vue
├── router/              # 라우터 설정
├── stores/              # Pinia 스토어
├── utils/               # kakaoMapLoader.js
└── views/               # 페이지 컴포넌트
    └── _ExampleView.vue # 템플릿 예시
```

## 라우트 목록
| 경로 | 이름 | 설명 |
|------|------|------|
| / | Home | 홈 |
| /login | Login | 로그인 |
| /search | Search | 검색 |
| /restaurant | Restaurant | 추천 결과 |
| /reviews | Reviews | 리뷰 |
| /bookmarks | Bookmarks | 즐겨찾기 |
| /statistics | Statistics | 통계 |
| /profile | Profile | 프로필 |

## 스타일 가이드
- 메인 색상: #FF6B6B (로고)
- 버튼 색상: #333 (어두운 버튼)
- 모바일 우선: max-width 430px

## 백엔드 API 응답 구조
```json
{
  "result": "SUCCESS",
  "data": { 실제 데이터 },
  "error": null
}
```

이 규칙들을 따라서 코드를 작성해주세요.
참고할 예시 파일: /frontend/src/views/_ExampleView.vue
```

---

## 📝 작업별 추가 프롬프트

### 새 페이지 생성 요청 시

```
[페이지명]View.vue 페이지를 만들어주세요.

요구사항:
- [기능 설명]
- [필요한 데이터]
- [UI 요소]

AppLayout을 사용하고, _ExampleView.vue를 참고해서 작성해주세요.
```

### API 연동 요청 시

```
[기능명] API를 연동해주세요.

백엔드 API:
- Method: POST
- URL: /api/v1/[엔드포인트]
- Request Body: { ... }
- Response: { result, data, error }

/frontend/src/api/ 폴더에 API 모듈을 추가하고,
페이지에서 사용하도록 구현해주세요.
```

### 카카오맵 기능 요청 시

```
카카오맵을 사용하는 [기능]을 구현해주세요.

주의사항:
1. onMounted에서 await nextTick() 필수
2. mapContainer.value null 체크 필수
3. loadKakaoMap() 유틸 사용
4. onUnmounted에서 마커/오버레이 정리
```

### 폼 페이지 요청 시

```
[기능명] 폼 페이지를 만들어주세요.

필드:
- [필드1]: [타입] (필수/선택)
- [필드2]: [타입] (필수/선택)

Element Plus 폼 컴포넌트 사용:
- el-input, el-select, el-rate 등
- el-button으로 제출
- ElMessage로 결과 알림
```

---

## 🔧 문제 해결 프롬프트

### 카카오맵 안 보일 때

```
카카오맵이 로드되지 않습니다.

확인사항:
1. index.html에 카카오맵 SDK 스크립트가 있는지
2. onMounted에서 await nextTick() 사용했는지
3. mapContainer ref가 DOM에 연결되었는지
4. 브라우저 콘솔 에러 메시지

콘솔 로그: [에러 메시지]
```

### 빈 화면일 때

```
페이지가 빈 화면입니다.

확인사항:
1. App.vue에 <router-view /> 있는지
2. 라우터에 해당 경로 등록되었는지
3. 컴포넌트 import 경로가 맞는지
4. 브라우저 콘솔 에러

콘솔 로그: [에러 메시지]
```

---

## 📌 빠른 참조

### import 목록

```javascript
// Vue
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'

// Router
import { useRouter, useRoute } from 'vue-router'

// 레이아웃
import AppLayout from '@/components/layout/AppLayout.vue'

// 카카오맵
import { loadKakaoMap } from '@/utils/kakaoMapLoader'

// API
import { restaurantApi } from '@/api/restaurantApi'

// Pinia (필요시)
import { useUserStore } from '@/stores/user'
```

### Element Plus 전역 사용 (import 불필요)

```javascript
// 메시지
ElMessage.success('성공')
ElMessage.error('실패')
ElMessage.warning('경고')

// 확인 다이얼로그
ElMessageBox.confirm('메시지', '제목')

// 로딩
ElLoading.service({ fullscreen: true })
```
