import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomeView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { guestOnly: true },
  },
  {
    path: '/signup',
    name: 'SignUp',
    component: () => import('@/views/SignUpView.vue'),
    meta: { requiresAuth: true, requiresPending: true },
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/SearchView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/ProfileView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/restaurant/:id?',
    name: 'Restaurant',
    component: () => import('@/views/RestaurantView.vue'),
    meta: { requiresAuth: true },
    props: true,
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
    path: '/review/write',
    name: 'PostReview',
    component: () => import('@/views/PostReviewView.vue'),
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
  {
    path: '/oauth/callback',
    name: 'OAuthCallback',
    component: () => import('@/views/OAuthCallbackView.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 네비게이션 가드
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()

  // 인증이 필요한 페이지
  if (to.meta.requiresAuth) {
    // 인증 상태 확인이 안 된 경우 체크
    if (!userStore.isAuthenticated) {
      try {
        await userStore.checkLoginStatus()
      } catch (error) {
        // 인증 실패 - 로그인 페이지로
        return next('/login')
      }
    }

    // PENDING 상태인 경우 회원가입 페이지로 (signup 페이지 제외)
    if (userStore.isNewUser && to.name !== 'SignUp') {
      return next('/signup')
    }

    // requiresPending이 true인 페이지는 PENDING 상태에서만 접근 가능
    if (to.meta.requiresPending && !userStore.isNewUser) {
      return next('/')
    }
  }

  // 게스트 전용 페이지 (로그인 페이지 등)
  if (to.meta.guestOnly && userStore.isAuthenticated) {
    return next('/')
  }

  next()
})

export default router
