<div align="center">
  <img src="./images/logo.png" alt="FoodFinder Logo" width="300"/>
  <h1 style="color:#2F5D7B;">오늘 뭐 먹지?</h1>
  <p style="font-size:1.2em; color:#555;">
    FoodFinder는 AI 기반 음식점 추천 서비스입니다. </br>
    사용자의 취향(해시태그)과 위치 정보를 기반으로 주변 음식점을 검색하여 최적의 음식점을 추천합니다 <br>
  </p>
</div>

<div align="center">
  <h2>먹깨비들</h2>
  <table border="0" style="border: none; width: 80%;">
    <tr>
      <td align="center">
        <img src="./images/이건우.png" style="width: 150px; height: 150px; object-fit: cover; border-radius: 50%;" />
        <br />
        <b>이건우</b>
        <br />
        <a href="https://github.com/guntinue">@guntinue</a>
      </td>
      <td align="center">
        <img src="./images/이관호.png" style="width: 150px; height: 150px; object-fit: cover; border-radius: 50%;" />
        <br />
        <b>이관호</b>
        <br />
        <a href="https://github.com/Apeirogon99">@L-dragon-woo</a>
      </td>
      <td align="center">
        <img src="./images/이용우.jpg" style="width: 150px; height: 150px; object-fit: cover; border-radius: 50%;" />
        <br />
        <b>이용우</b>
        <br />
        <a href="https://github.com/L-dragon-woo">@L-dragon-woo</a>
      </td>
      <td align="center">
        <img src="./images/임성은.png" style="width: 150px; height: 150px; object-fit: cover; border-radius: 50%;" />
        <br />
        <b>임성은</b>
        <br />
        <a href="https://github.com/rlatjddms">@rlatjddms</a>
      </td>
        <td align="center">
        <img src="./images/임재열.png" style="width: 150px; height: 150px; object-fit: cover; border-radius: 50%;" />
        <br />
        <b>임재열</b>
        <br />
        <a href="https://github.com/Jae-yeol1">@Jae-yeol1</a>
      </td>
    </tr>
  </table>
</div>

## 목차
1. [프로젝트 개요](#overview)
3. [개발 환경 및 기술 스택](#tech-stack)
2. [주요기능](#features)
3. [플로우차](#flow-chart)
4. [요구사항 명세서](#requirements)
5. [테스트케이스 명세서](#test-case)
7. [개발 환경 및 기술 스택](#tech-stack)
8. [회고록](#review)


# 🍽️ FoodFinder - AI 기반 맞춤형 음식점 추천 서비스

> 당신의 취향을 분석하여 최적의 음식점을 추천해드립니다

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Vue.js](https://img.shields.io/badge/Vue.js-3.5-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)

---

## 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [개발 환경 및 기술 스택](#2-개발-환경-및-기술-스택)
3. [주요 기능](#3-주요-기능)
4. [플로우차트](#4-플로우차트)
5. [요구사항 명세서](#5-요구사항-명세서)
6. [테스트케이스 명세서](#6-테스트케이스-명세서)
7. [회고록](#8-회고록)

---

## 1. 프로젝트 개요

### 📌 프로젝트 소개

**FoodFinder**는 사용자의 취향과 현재 위치를 기반으로 AI가 최적의 음식점을 추천해주는 서비스입니다.

"오늘 뭐 먹지?"라는 일상적인 고민을 해결하기 위해, 사용자가 선택한 해시태그(분위기, 음식 종류, 상황 등)와 위치 정보를 분석하여 카카오맵 API로 주변 음식점을 검색하고, OpenAI GPT-4o를 활용해 개인 맞춤형 음식점을 추천합니다.

### 🎯 프로젝트 목표

- 사용자 취향 기반의 **개인화된 음식점 추천** 제공
- AI를 활용한 **추천 이유 및 메뉴 제안**으로 선택의 확신 부여
- 직관적인 UI/UX로 **빠르고 간편한 의사결정** 지원
- 리뷰 및 통계 기능을 통한 **데이터 기반 인사이트** 제공

### 👥 팀 구성

| 이름 | 역할 | 담당 기능 |
|------|------|----------|
| 이건우 | Frontend | 통계계 |
| 이관호 | Frontend | 지도 |
| 이용우 | Frontend | 리뷰 |
| 임성은 | Frontend | 회원 |
| 임재열 | Frontend | 추천 |

### 📅 개발 기간

- **2026.1.02 ~ 2026.01.05** (1주)

---

## 2. 개발 환경 및 기술 스택

### 🔧 개발 환경

| 구분 | 환경 |
|------|------|
| IDE | IntelliJ IDEA, VS Code |
| OS | Windows, macOS |
| JDK | Java 17 (OpenJDK) |
| Node.js | 18.x 이상 |
| 형상관리 | Git, GitHub |

### 🛠️ 기술 스택

#### Backend

| 기술 | 버전 | 설명 |
|------|------|------|
| Spring Boot | 3.5.0 | 애플리케이션 프레임워크 |
| Spring Security | 6.x | 인증/인가 처리 |
| Spring Data JPA | 3.5.0 | ORM, 데이터 접근 계층 |
| Spring Data Redis | 3.5.0 | 캐싱 처리 |
| Spring WebFlux | 6.x | 비동기 HTTP 클라이언트 |
| Spring OAuth2 Client | - | GitHub OAuth2 로그인 |
| PostgreSQL | - | 관계형 데이터베이스 |
| Redis | - | 캐시 서버 |
| Lombok | 1.18.x | 보일러플레이트 코드 제거 |

#### Frontend

| 기술 | 버전 | 설명 |
|------|------|------|
| Vue.js | 3.5.x | 프론트엔드 프레임워크 |
| Vite | 6.x | 빌드 도구 |
| Pinia | 3.x | 상태 관리 |
| Vue Router | 4.x | 라우팅 |
| Axios | 1.9.x | HTTP 클라이언트 |
| Element Plus | 2.9.x | UI 컴포넌트 라이브러리 |
| Chart.js | 4.x | 차트 시각화 |

#### 외부 API

| API | 용도 |
|-----|------|
| 카카오맵 API | 주변 음식점 검색, 위치 기반 서비스 |
| OpenAI GPT-4o | AI 음식점 추천, 메뉴 추천 |
| Git| GitHub OAuth | 소셜 로그인 |

## 3. 주요 기능

### 🔐 회원 관리

**GitHub OAuth2 로그인**
- GitHub 계정을 통한 간편 로그인
- 신규 사용자 자동 회원가입 처리
- JWT 기반 세션 관리

**프로필 관리**
- 닉네임 변경
- 알레르기 정보 등록/수정
- 회원 탈퇴

### 🎯 AI 음식점 추천

**해시태그 기반 취향 선택**
- 분위기: #조용한, #활기찬, #데이트, #혼밥 등
- 음식 종류: #한식, #중식, #일식, #양식 등
- 상황: #점심식사, #저녁회식, #간단히, #푸짐하게 등

**AI 추천 프로세스**
1. 사용자 위치 + 선호 반경 설정
2. 해시태그로 취향 선택
3. 카카오맵 API로 주변 음식점 후보 검색
4. GPT-4o가 취향 분석 후 최적 음식점 선정
5. 추천 이유 + 추천 메뉴 함께 제공

**재추천 기능**
- 마음에 들지 않으면 다른 음식점 재추천 요청
- 이전 추천 음식점은 자동 제외

### 📝 리뷰 시스템

**리뷰 작성**
- 별점 (1~5점)
- 텍스트 리뷰
- 추천 음식점에 대한 피드백

**리뷰 조회**
- 음식점별 리뷰 목록
- 내가 작성한 리뷰 모아보기

### 📊 통계 대시보드

**카테고리별 통계**
- 가장 많이 추천된 음식 카테고리
- 카테고리별 만족도 분석

**시간대별 통계**
- 시간대별 추천 요청 패턴
- 요일별 이용 현황

### 🗺️ 지도 연동

- 카카오맵 기반 음식점 위치 표시
- 현재 위치에서 음식점까지 거리 표시
- 음식점 상세 정보 (주소, 전화번호 등)

---
## 4. 플로우차트

<img width="18700" height="13730" alt="Food Finder" src="https://github.com/user-attachments/assets/8925e9f9-cd16-4430-ab6e-b44d9b7a1e58" />

## 5. 요구사항 명세서

## 6. 테스트케이스 명세서

## 7. 회고록

| 이름 | 회고록 |
|-----|------|
| 김성은 | 이번 프론트엔드 프로젝트에서는 유저 파트를 담당하며 세션 기반 깃허브 OAuth 로그인을 구현했습니다. 로그인 성공 후 사용자 상태에 따라 화면을 분기하고, 인증 흐름에 맞게 라우팅을 제어하는 과정에서 프론트엔드에서의 인증 처리 방식을 직접 경험할 수 있었습니다. 특히 백엔드에서 구현한 API를 프론트엔드에서 직접 연동하며 응답 구조와 상태 값을 기준으로 화면 로직을 구성했을 때, 그동안 로직으로만 다뤘던 기능들이 실제 화면에서 동작하는 것이 보여 성취감이 크게 느껴졌습니다. 화면 흐름을 설계하는 과정 또한 인상 깊었고, 프론트엔드 개발의 역할을 보다 명확하게 이해하게 되었습니다. 또한 좋은 팀원들 덕분에 막히는 부분도 부담 없이 해결할 수 있었고, 전반적으로 프로젝트를 즐기며 진행할 수 있었습니다. 이번 경험을 통해 프론트엔드 개발의 매력을 확실히 느낄 수 있었습니다. |
| 이용우 | 본 프로젝트에서는 Vue를 활용하여 프론트엔드를 구성하고, 기존에 학습해 온 백엔드 API를 실제 서비스 형태로 연동하는 작업을 처음으로 수행하였다. 단순한 화면 구현을 넘어, 프론트엔드와 백엔드 간의 데이터 흐름을 이해하고 이를 기반으로 기능을 완성하는 것이 주요 목표였다. 프로젝트 초반에는 API에서 데이터를 가져오는 방식, URL 및 라우팅 설정, 그리고 View 구조를 어떻게 설계해야 하는지에 대한 명확한 기준이 없어 많은 어려움을 겪었다. 특히 어떤 화면이 필요하며, 각 화면이 어떤 역할을 가져야 하는지 스스로 정의하는 과정이 쉽지 않았다. 이 과정에서 다양한 기술 블로그와 참고 자료를 조사하며 문제를 해결해 나갔고, 이를 통해 Vue와 백엔드 API 간의 연동 구조에 대한 이해를 높일 수 있었다. 또한 세션 기반으로 유저 정보를 관리하고, 이를 프론트엔드에서 활용하는 방법을 직접 구현하며 인증 및 사용자 상태 관리에 대한 경험을 쌓았다. 아울러 유지보수를 고려한 컨트롤러와 서비스 계층 분리, 명확한 네이밍 규칙의 중요성을 체감하게 되었다. 기능 구현뿐만 아니라, 코드의 가독성과 확장성을 고려한 구조 설계가 실제 개발 환경에서 얼마나 중요한지 알 수 있는 계기가 되었다. 특히 프로젝트 전반에 걸쳐 실제 실무와 유사한 방식으로 개발 로드맵을 제시해 주시고 방향성을 잡아 주신 관호님의 도움을 통해, 단순 과제 수행을 넘어 실무 관점에서 프로젝트를 바라볼 수 있게 되었다. 이번 프로젝트를 통해 프론트엔드와 백엔드를 연결하는 전반적인 개발 흐름을 경험할 수 있었으며, 이후 유사한 프로젝트에서는 보다 체계적이고 효율적인 방식으로 개발을 진행할 수 있을 것이라 기대한다. |
| 임재열 | 프론트엔드 프로젝트를 진행하면서 "백엔드와 어떻게 연결될까?"라는 궁금증이 있었는데, 이번에 그 부분이 명확하게 해결되었다. 그 과정에서 전체적인 웹 개발 구조와 흐름을 이해할 수 있어서 좋았다. 또한 AI 추천 기능을 구현하면서 설정해야 할 것들이 많아 어려움도 컸지만, 하나씩 문제를 해결해 나가며 "왜 이렇게 동작하지?"라고 의문이 들었던 부분들이 점점 정리되고 이해되었다. 문제의 원인을 찾아 해결했을 때 성취감이 컸고, 결과적으로 기분 좋게 마무리할 수 있었다. |
| 이관호 | 이번 프로젝트에서는 카카오맵 API를 활용한 위치 기반 서비스를 담당하며, 사용자의 현재 위치를 기반으로 주변 음식점을 조회하는 기능을 구현했습니다. 그동안 백엔드 개발을 하면서 API를 제공하는 입장이었는데, 이번에는 외부 API를 소비하는 입장에서 개발하다 보니 응답 구조 파악과 에러 핸들링의 중요성을 다시 한번 체감했습니다. 또한 지도 위에 실시간으로 데이터가 렌더링되는 것을 직접 눈으로 확인했을 때, 백엔드에서 JSON 응답만 확인하던 것과는 다른 종류의 성취감을 느낄 수 있었습니다. 이번 경험을 통해 프론트엔드에서 외부 API를 연동하는 전반적인 흐름을 익혔고, 위치 기반 서비스 구현에 필요한 기술적 기반을 다질 수 있었습니다. |
