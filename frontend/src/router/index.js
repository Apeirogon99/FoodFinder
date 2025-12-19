import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomeView.vue'),
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/ProfileView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/map',
    name: 'Map',
    component: () => import('@/views/MapView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/recommend',
    name: 'Recommend',
    component: () => import('@/views/RecommendView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/reviews',
    name: 'Reviews',
    component: () => import('@/views/ReviewsView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/bookmarks',
    name: 'Bookmarks',
    component: () => import('@/views/BookmarksView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import('@/views/StatisticsView.vue'),
    meta: { requiresAuth: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 네비게이션 가드 (인증 체크)
router.beforeEach((to, from, next) => {
  // TODO: 실제 인증 상태 체크 로직 구현
  // const isAuthenticated = store.getters.isAuthenticated
  const isAuthenticated = true // 개발 중 임시로 true

  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router
