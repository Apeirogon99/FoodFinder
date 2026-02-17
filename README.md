# 참고
- 해당 저장소는 저의 주요 역할 및 트러블 슈팅을 정리하기 위한 요약본입니다.
- 전체적인 정보를 위해 원본을 보고 싶으시다면 [FoodFinder](https://github.com/20250918-beyond-SW-Camp-21th/beyond-SW-21th-third-3team)를 클릭해주세요.

---

## 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [기술 스택](#2-기술-스택)
3. [주요 기능](#3-주요-기능)
4. [트러블 슈팅](#4-트러블-슈팅)

---

## 1. 프로젝트 개요

### 소개

**FoodFinder**는 사용자의 취향과 현재 위치를 기반으로 AI가 최적의 음식점을 추천해주는 서비스입니다.

"오늘 뭐 먹지?"라는 일상적인 고민을 해결하기 위해, 사용자가 선택한 해시태그(분위기, 음식 종류, 상황 등)와 위치 정보를 분석하여 카카오맵 API로 주변 음식점을 검색하고, OpenAI GPT-4o를 활용해 개인 맞춤형 음식점을 추천합니다.

### 목표

- 외부 API 사용 및 활용, 문서에 대한 이해

### 인원

- 인원 5명 (백엔드 5)

### 기간

- **2026.1.02 ~ 2026.01.05** (1주)

---

## 2. 기술 스택

### 🛠️ 기술 스택

#### Backend
![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue) ![Redis](https://img.shields.io/badge/Redis-7-red)

#### Frontend
 ![Vue.js](https://img.shields.io/badge/Vue.js-3.5-4FC08D) ![Vite](https://img.shields.io/badge/Vite-6-646CFF) ![Pinia](https://img.shields.io/badge/Pinia-3-yellow) ![ElementPlus](https://img.shields.io/badge/Element%20Plus-2.9-409EFF)

#### 외부 API
 ![Kakao Map](https://img.shields.io/badge/Kakao%20Map-API-FFCD00)

## 3. 주요 기능

1. **주변 음식점 조회**
    - 카카오맵 API를 이용해 주변(100~500m)음식점 정보를 모두 조회하기
---

## 4. 트러블 슈팅

1. **카카오맵 API 조회 제한 문제**
    - 카카오맵 API는 조회에 제한(45건)이 있어 범위에 있는 음식점 전체를 조회하지 못하는 문제 발생했습니다.
    - 범위로는 300이상의 결과가 있지만 전부 조회하지 못하는 제한사항이 있었습니다.
    - 또한 조회시 45개는 15 * 3 페이지 형식이기에 API를 추가로 호출해야 조회가 가능했습니다.
      <img width="1536" height="692" alt="45개 찾는거 까지 가능" src="https://github.com/user-attachments/assets/11abcd6c-e663-442c-9da2-31c756eaa7b0" />

    - 사각형을 4분할 재귀 검색하는 방식을 적용해 조회 제한에서도 모든 음식점 탐색 가능 하게하는 전략을 사용했습니다.
    - 이는 일종의 쿼드트리와 유사한 형식이며 한 그리드에 조회 제한(45)건보다 많다면 영역을 100m까지 재귀적으로 줄였습니다.
      <img width="617" height="473" alt="카카오맵 탐색 전략" src="https://github.com/user-attachments/assets/036f3f8a-7c86-4573-ace1-3f4c9816b1cd" />

    - 모든 음식점을 조회할 수 있었지만 API 호출량 증가와 응답속도 증가 문제가 발생하였습니다.
      - 모든 음식점 조회 결과
        <img width="798" height="28" alt="image" src="https://github.com/user-attachments/assets/bb51e1ac-9f9a-43b8-bd23-d341bc4b3cab" />
        <img width="1354" height="744" alt="image" src="https://github.com/user-attachments/assets/d0c7e888-2a9a-45d1-af34-616cadaa0bd3" />
      
1. **Redis 캐싱 중심 아키텍처 선택 이유**
    - API 호출량 증가는 비용과 관련된 치명적인 문제였기에 해결 방법을 찾아야 했습니다.
    - 다른 방안 중 하나는 공공데이터 API를 활용하여 Postgres에 저장 및 스케줄링을 통해 하루마다 지속적으로 관리하는 방식이였습니다.
    - 하지만 직접 음식점을 관리하는 것은 서비스 필요 이상의 복잡성 증가가 우려되어 다른 방안을 찾아야 했습니다.
    - 현재 문제는 높은 API 호출이기에 비용 최적화를 고려하여 Redis로 캐싱하기로 결정
      
2. **증가된 API 호출량 및 응답속도 개선**
    - 사각형을 고정된 그리드 위치를 100m 간격으로 모두 구하여 위도, 경도를 key로 사용
    - 만약에 캐시가 HIT한다면 API를 호출하지 않을 것이고 MISS라면 카카오맵API를 이용해 조회하였습니다.
      <img width="756" height="424" alt="겹쳤을 경우" src="https://github.com/user-attachments/assets/925ceb5a-8e4c-49b7-a250-189b888d5f09" />

    
    - 강남역에서 100명의 인원이 500m 간격으로 조회를 하는 테스트를 진행하였습니다.
    - 결과적으로는 밀집 상권, 반복 요청이 많은 구간에서는 API 호출량 및 비용 96% 감소·응답속도 98% 개선되어 큰 효과를 얻을 수 있었습니다.

      - 밀집상권 결과
        
        | 항목 | 캐시 없음 | 캐싱 | 효과 |
        |------|--------------|-------------|------|
        | API 호출 | 11,992회 | 458회 | **11,534회 (96.2%)** |
        | 소요시간 | 1,786,803ms (~29.8분) | 32,525ms (~32.5초) | **1,754,278ms (98.2%)** |
        | 예상비용 | 1,199.2원 | 45.8원 | **1,153.4원** |

      - 같은 자리에서 반복 요청한 경우
        
        <img width="464" height="114" alt="image" src="https://github.com/user-attachments/assets/b9d63917-b9a0-405f-90b7-f8c6d0ad0087" />

    
3. **저밀도 지역 문제 (계획)**
   - 다음 문제는 밀집도가 낮은 곳에서는 캐싱이 비교적 발생하지 않는 문제점이 있었습니다.
   - 또한 음식점이 100m에 여러개가 있는 것이 아닌 더 높은 범위에서도 45개가 넘지 않는 곳도 다수 있습니다.
   - 발생빈도가 낮다는 점에서 괜찮을 수 있지만 여기서 좀 더 개선을 고려하였을 때 그리드를 병합하는 좀 더 동적인 형식을 계획하고 있습니다.

     - 저밀도 상권 결과
       
        | 항목 | 캐시 없음 | 셀 캐싱 | 절감 |
        |------|--------------|-------------|------|
        | API 호출 | 1,548회 | 1,103회 | **445회 (28.7%)** |
        | 소요시간 | 87,100ms | 63,867ms | **23,233ms (26.7%)** |
        | 예상비용 | 154.8원 | 110.3원 | **44.5원** |
