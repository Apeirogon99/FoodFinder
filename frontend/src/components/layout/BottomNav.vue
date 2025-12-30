<template>
  <nav class="bottom-nav">
    <router-link
      v-for="item in navItems"
      :key="item.path"
      :to="item.path"
      class="nav-item"
      :class="{ active: isActive(item.path) }"
    >
      <el-icon :size="24">
        <component :is="item.icon" />
      </el-icon>
      <span class="nav-label">{{ item.label }}</span>
    </router-link>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { 
  HomeFilled, 
  Promotion, 
  Document, 
  User 
} from '@element-plus/icons-vue'

const route = useRoute()

const navItems = [
  { path: '/', label: '홈', icon: HomeFilled },
  { path: '/recommend', label: '추천', icon: Promotion },
  { path: '/reviews', label: '리뷰', icon: Document },
  { path: '/profile', label: '프로필', icon: User },
]

const isActive = (path) => {
  if (path === '/') {
    return route.path === '/'
  }
  // recommend, search, restaurant 모두 추천 탭으로
  if (path === '/recommend') {
    return route.path.startsWith('/recommend') || 
           route.path.startsWith('/search') || 
           route.path.startsWith('/restaurant')
  }
  return route.path.startsWith(path)
}
</script>

<style scoped>
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 64px;
  background: white;
  display: flex;
  justify-content: space-around;
  align-items: center;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  padding-bottom: env(safe-area-inset-bottom, 0);
  max-width: 430px;
  margin: 0 auto;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  text-decoration: none;
  color: #999;
  transition: color 0.2s;
  padding: 8px 20px;
  min-width: 72px;
}

.nav-item.active {
  color: #667eea;
}

.nav-item:hover {
  color: #667eea;
}

.nav-label {
  font-size: 11px;
  font-weight: 500;
}
</style>
