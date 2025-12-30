<template>
  <div class="oauth-callback">
    <div class="loading-container">
      <el-icon class="loading-icon" :size="48">
        <Loading />
      </el-icon>
      <p>로그인 처리 중...</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Loading } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

onMounted(async () => {
  try {
    // 로그인 상태 확인
    await userStore.checkLoginStatus()
    
    // 신규 사용자면 회원가입 페이지로
    if (userStore.isNewUser) {
      router.replace('/signup')
    } else {
      // 기존 사용자면 홈으로
      router.replace('/')
    }
  } catch (error) {
    console.error('OAuth 콜백 처리 실패:', error)
    router.replace('/login')
  }
})
</script>

<style scoped>
.oauth-callback {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.loading-container {
  text-align: center;
}

.loading-icon {
  animation: rotate 1s linear infinite;
  color: #409eff;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.loading-container p {
  margin-top: 16px;
  color: #666;
  font-size: 14px;
}
</style>
