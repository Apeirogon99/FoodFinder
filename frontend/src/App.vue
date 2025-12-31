<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
</script>

<style>
/* CSS 커스텀 프로퍼티 (전역 변수) */
:root {
  /* 색상 */
  --color-primary: #007AFF;
  --color-primary-hover: #0056CC;
  --color-danger: #ff3b30;
  --color-warning: #ff9500;
  --color-success: #34c759;
  
  /* 배경색 */
  --color-bg-primary: #ffffff;
  --color-bg-secondary: #f2f2f7;
  --color-bg-grouped: #f2f2f7;
  --color-bg-tertiary: #e5e5ea;
  
  /* 텍스트 색상 */
  --color-text-primary: #1c1c1e;
  --color-text-secondary: #3c3c43;
  --color-text-tertiary: #8e8e93;
  
  /* 구분선 */
  --color-separator: #e5e5ea;
  --color-border: #e5e7eb;
  
  /* 그림자 */
  --ios-card-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  --ios-card-shadow-hover: 0 4px 12px rgba(0, 0, 0, 0.12);
}

/* 전역 스타일 리셋 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  height: 100%;
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: var(--color-bg-grouped);
}

#app {
  width: 100%;
  min-height: 100vh;
}
</style>
