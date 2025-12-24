# FoodFinder Frontend 개발 가이드

## 📌 AI 프롬프트용 컨텍스트

```
이 프로젝트는 FoodFinder - 위치 기반 음식점 추천 서비스의 프론트엔드입니다.

기술 스택:
- Vue 3 (Composition API + <script setup>)
- Vite (빌드 도구)
- Pinia (상태관리)
- Vue Router (라우팅)
- Element Plus (UI 컴포넌트 라이브러리)
- Axios (HTTP 클라이언트)
- 카카오맵 API (지도)

핵심 규칙:
1. 모든 페이지는 AppLayout 컴포넌트로 감싸야 합니다
2. Composition API의 <script setup> 문법을 사용합니다
3. Element Plus 컴포넌트를 우선 사용합니다 (el-button, el-input 등)
4. API 호출은 /frontend/src/api/ 폴더의 모듈을 사용합니다
5. 카카오맵 사용 시 loadKakaoMap() 유틸을 사용하고 nextTick 후 초기화합니다
```

---

## 🛠️ 기술 스택

| 분류 | 기술 | 버전 |
|------|------|------|
| Framework | Vue | 3.5.x |
| Build Tool | Vite | 6.x |
| State Management | Pinia | 3.x |
| Router | Vue Router | 4.x |
| UI Library | Element Plus | 2.9.x |
| HTTP Client | Axios | 1.9.x |
| Chart | Chart.js + vue-chartjs | 4.x |

---

## 📁 프로젝트 구조

```
frontend/
├── index.html                 # 진입점 (카카오맵 SDK 로드)
├── vite.config.js             # Vite 설정
├── package.json
├── .env                       # 환경변수 (Git 제외)
│
└── src/
    ├── main.js                # Vue 앱 초기화
    ├── App.vue                # 루트 컴포넌트
    │
    ├── api/                   # API 호출 모듈
    │   └── restaurantApi.js
    │
    ├── assets/                # 정적 파일 (이미지, 폰트)
    │   └── logo.png
    │
    ├── components/            # 재사용 컴포넌트
    │   └── layout/
    │       ├── AppLayout.vue  # ⭐ 공통 레이아웃 (필수 사용)
    │       └── AppHeader.vue  # 헤더 컴포넌트
    │
    ├── router/                # 라우터 설정
    │   └── index.js
    │
    ├── stores/                # Pinia 스토어
    │   └── user.js
    │
    ├── utils/                 # 유틸리티 함수
    │   └── kakaoMapLoader.js  # 카카오맵 로더
    │
    └── views/                 # 페이지 컴포넌트
        ├── _ExampleView.vue   # ⭐ 템플릿 예시 (참고용)
        ├── HomeView.vue
        ├── LoginView.vue
        ├── SearchView.vue
        ├── RestaurantView.vue
        ├── RecommendView.vue
        ├── ReviewsView.vue
        ├── BookmarksView.vue
        ├── StatisticsView.vue
        └── ProfileView.vue
```

---

## 🎨 AppLayout 사용법 (필수)

### 기본 구조

모든 페이지는 `AppLayout`으로 감싸야 합니다.

```vue
<template>
  <AppLayout>
    <!-- 메인 컨텐츠 (기본 slot) -->
    <div class="content">
      페이지 내용
    </div>

    <!-- 하단 영역 (선택, 필요시 사용) -->
    <template #footer>
      <div class="footer-content">
        <el-button>버튼</el-button>
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/components/layout/AppLayout.vue'
</script>
```

### 레이아웃 구조

```
┌─────────────────────────────────────┐
│  헤더 (로고) - 자동 포함             │
├─────────────────────────────────────┤
│                                     │
│  메인 컨텐츠 영역                    │
│  <slot></slot>                      │
│                                     │
├─────────────────────────────────────┤
│  하단 영역 (선택)                    │
│  <slot name="footer"></slot>        │
└─────────────────────────────────────┘

- 최대 너비: 430px (모바일 우선)
- 중앙 정렬 + 그림자 효과
```

### 하단 영역 없이 사용

```vue
<template>
  <AppLayout>
    <div class="content">
      컨텐츠만 작성
    </div>
  </AppLayout>
</template>
```

---

## 📝 페이지 작성 템플릿

### 기본 페이지

```vue
<template>
  <AppLayout>
    <div class="page-content">
      <h1>페이지 제목</h1>
      
      <!-- Element Plus 컴포넌트 사용 -->
      <el-button type="primary" @click="handleClick">
        버튼
      </el-button>
      
      <el-input v-model="inputValue" placeholder="입력하세요" />
    </div>
  </AppLayout>
</template>

<script setup>
import { ref } from 'vue'
import AppLayout from '@/components/layout/AppLayout.vue'

const inputValue = ref('')

const handleClick = () => {
  console.log('클릭됨')
}
</script>

<style scoped>
.page-content {
  padding: 20px;
}
</style>
```

### API 호출이 있는 페이지

```vue
<template>
  <AppLayout>
    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="loading">
      <el-skeleton :rows="5" animated />
    </div>
    
    <!-- 데이터 표시 -->
    <div v-else class="content">
      <div v-for="item in items" :key="item.id">
        {{ item.name }}
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import { restaurantApi } from '@/api/restaurantApi'

const isLoading = ref(true)
const items = ref([])

const fetchData = async () => {
  isLoading.value = true
  try {
    const response = await restaurantApi.getRestaurantDetail('id')
    items.value = response.data
  } catch (error) {
    console.error('데이터 로드 실패:', error)
    ElMessage.error('데이터를 불러오는데 실패했습니다.')
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>
```

### 카카오맵이 있는 페이지

```vue
<template>
  <AppLayout>
    <div class="map-section">
      <div ref="mapContainer" class="map-container"></div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import { loadKakaoMap } from '@/utils/kakaoMapLoader'

const mapContainer = ref(null)
let map = null
let marker = null

const initMap = async () => {
  // 1. 컨테이너 확인
  if (!mapContainer.value) {
    console.error('맵 컨테이너가 없습니다')
    return
  }
  
  try {
    // 2. 카카오맵 로드
    const kakao = await loadKakaoMap()
    
    // 3. 맵 생성
    const options = {
      center: new kakao.maps.LatLng(37.5665, 126.9780),
      level: 3
    }
    map = new kakao.maps.Map(mapContainer.value, options)
    
    // 4. 마커 생성 (선택)
    marker = new kakao.maps.Marker({
      position: map.getCenter(),
      map: map
    })
    
  } catch (error) {
    console.error('카카오맵 초기화 실패:', error)
  }
}

onMounted(async () => {
  // ⚠️ 중요: nextTick으로 DOM 렌더링 대기
  await nextTick()
  await initMap()
})

onUnmounted(() => {
  // 정리
  if (marker) marker.setMap(null)
})
</script>

<style scoped>
.map-section {
  width: 100%;
  height: 300px;
}

.map-container {
  width: 100%;
  height: 100%;
}
</style>
```

---

## 🔌 API 호출 가이드

### API 모듈 구조 (src/api/restaurantApi.js)

```javascript
import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1',
  timeout: 10000,
  withCredentials: true
})

export const restaurantApi = {
  // 주변 음식점 검색
  searchNearbyRestaurants(latitude, longitude, radius = 500) {
    return api.post('/restaurants/search', {
      latitude,
      longitude,
      radius
    })
  },

  // AI 음식점 추천
  getRecommendation(latitude, longitude, radius) {
    return api.post('/restaurants/recommend', {
      latitude,
      longitude,
      radius
    })
  },

  // 음식점 상세 조회
  getRestaurantDetail(restaurantId) {
    return api.get(`/restaurants/${restaurantId}`)
  }
}
```

### API 사용 예시

```javascript
import { restaurantApi } from '@/api/restaurantApi'

// 추천 요청
const response = await restaurantApi.getRecommendation(37.5665, 126.9780, 500)
const restaurant = response.data.data // ApiResult 구조
```

---

## 🎯 Element Plus 자주 쓰는 컴포넌트

### 버튼

```vue
<el-button>기본</el-button>
<el-button type="primary">Primary</el-button>
<el-button type="primary" :loading="isLoading">로딩</el-button>
<el-button type="danger">삭제</el-button>
```

### 입력

```vue
<el-input v-model="value" placeholder="입력하세요" />
<el-input v-model="value" type="textarea" :rows="3" />
<el-input-number v-model="num" :min="1" :max="10" />
```

### 선택

```vue
<el-select v-model="selected" placeholder="선택하세요">
  <el-option label="옵션1" value="1" />
  <el-option label="옵션2" value="2" />
</el-select>

<el-radio-group v-model="radio">
  <el-radio value="1">옵션1</el-radio>
  <el-radio value="2">옵션2</el-radio>
</el-radio-group>
```

### 평점

```vue
<el-rate v-model="rating" :max="5" />
<el-rate v-model="rating" disabled show-score />
```

### 메시지 (전역)

```javascript
// 성공
ElMessage.success('저장되었습니다')

// 에러
ElMessage.error('실패했습니다')

// 경고
ElMessage.warning('주의하세요')

// 확인 다이얼로그
ElMessageBox.confirm('삭제하시겠습니까?', '확인', {
  confirmButtonText: '삭제',
  cancelButtonText: '취소',
  type: 'warning'
}).then(() => {
  // 확인 클릭
}).catch(() => {
  // 취소 클릭
})
```

### 로딩 스켈레톤

```vue
<el-skeleton :rows="5" animated />
```

### 카드

```vue
<el-card>
  <template #header>제목</template>
  내용
</el-card>
```

---

## 🗺️ 라우터 설정

### 현재 라우트 목록

| 경로 | 이름 | 인증 필요 | 설명 |
|------|------|:--------:|------|
| `/` | Home | ❌ | 홈 |
| `/login` | Login | ❌ | 로그인 |
| `/search` | Search | ✅ | 검색 (범위 설정) |
| `/restaurant` | Restaurant | ✅ | 추천 결과 |
| `/recommend` | Recommend | ✅ | AI 추천 |
| `/reviews` | Reviews | ✅ | 리뷰 |
| `/bookmarks` | Bookmarks | ✅ | 즐겨찾기 |
| `/statistics` | Statistics | ✅ | 통계 |
| `/profile` | Profile | ✅ | 프로필 |

### 라우터 사용

```javascript
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 페이지 이동
router.push('/search')
router.push({ name: 'Restaurant', query: { id: '123' } })

// 쿼리 파라미터 읽기
const id = route.query.id
```

---

## 🎨 스타일 가이드

### 색상

```css
/* 메인 색상 */
--primary-color: #FF6B6B;      /* 로고, 강조 */
--primary-light: #FFB4B4;      /* 배경 */

/* 버튼 */
--button-dark: #333;           /* 주요 버튼 */
--button-dark-hover: #555;

/* 텍스트 */
--text-primary: #333;
--text-secondary: #666;
--text-muted: #999;

/* 배경 */
--bg-white: #fff;
--bg-light: #f9f9f9;
--bg-gray: #f5f5f5;

/* 테두리 */
--border-color: #eee;
--border-dark: #ddd;
```

### 공통 버튼 스타일

```css
.btn-primary {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  background: #333;
  border-color: #333;
}

.btn-secondary {
  height: 44px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  background: #fff;
  border-color: #ddd;
  color: #333;
}
```

### 공통 카드 스타일

```css
.card {
  padding: 16px;
  background: #f9f9f9;
  border: 1px solid #eee;
  border-radius: 8px;
}
```

---

## ⚠️ 주의사항

### 1. 카카오맵 초기화

```javascript
// ❌ 잘못된 방법 - DOM이 준비되지 않음
onMounted(() => {
  initMap()
})

// ✅ 올바른 방법 - nextTick으로 DOM 대기
onMounted(async () => {
  await nextTick()
  await initMap()
})
```

### 2. v-if와 ref

```vue
<!-- ❌ 잘못된 방법 - v-if로 숨겨진 요소의 ref는 null -->
<div v-if="isLoading">로딩중</div>
<div v-else ref="container"></div>

<!-- 데이터 로드 후 isLoading=false하고 바로 ref 접근하면 null -->
```

```javascript
// ✅ 올바른 방법 - nextTick으로 DOM 업데이트 대기
isLoading.value = false
await nextTick()
// 이제 container.value 접근 가능
```

### 3. API 응답 구조

백엔드 ApiResult 구조:
```json
{
  "result": "SUCCESS",
  "data": { ... },
  "error": null
}
```

```javascript
// 응답 데이터 접근
const response = await api.getData()
const actualData = response.data.data  // axios.data → ApiResult.data
```

---

## 🚀 개발 서버 실행

```bash
cd frontend
npm install
npm run dev
```

http://localhost:5173 에서 확인

---

## 📋 체크리스트

새 페이지 작성 시 확인:

- [ ] `AppLayout` import 및 사용
- [ ] `<script setup>` 사용
- [ ] 로딩 상태 처리 (`v-if="isLoading"`)
- [ ] 에러 처리 (`try-catch`, `ElMessage.error`)
- [ ] 카카오맵 사용 시 `nextTick` 적용
- [ ] 스타일 `scoped` 적용
