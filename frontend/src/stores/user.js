import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // ========== State ==========
  const isAuthenticated = ref(false)
  const userId = ref(null)
  const userStatus = ref(null) // 'PENDING' | 'ACTIVE'
  const profile = ref(null) // { githubId, nickname, allergies }
  const isLoading = ref(false)

  // ========== Getters ==========
  const isNewUser = computed(() => userStatus.value === 'PENDING')
  const isActiveUser = computed(() => userStatus.value === 'ACTIVE')
  const nickname = computed(() => profile.value?.nickname || '')
  const allergies = computed(() => profile.value?.allergies || [])

  // ========== Actions ==========
  
  /**
   * 로그인 상태 확인
   */
  async function checkLoginStatus() {
    try {
      isLoading.value = true
      const data = await authApi.checkLoginSuccess()
      isAuthenticated.value = true
      userId.value = data.userId
      userStatus.value = data.status
      return data
    } catch (error) {
      isAuthenticated.value = false
      userId.value = null
      userStatus.value = null
      throw error
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 프로필 조회
   */
  async function fetchProfile() {
    try {
      isLoading.value = true
      const data = await userApi.getProfile()
      profile.value = data
      return data
    } catch (error) {
      console.error('프로필 조회 실패:', error)
      throw error
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 회원가입 (프로필 설정)
   */
  async function signUp(data) {
    try {
      isLoading.value = true
      const result = await userApi.signUp(data)
      profile.value = result
      userStatus.value = 'ACTIVE'
      return result
    } catch (error) {
      console.error('회원가입 실패:', error)
      throw error
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 프로필 수정
   */
  async function updateProfile(data) {
    try {
      isLoading.value = true
      const result = await userApi.updateProfile(data)
      profile.value = result
      return result
    } catch (error) {
      console.error('프로필 수정 실패:', error)
      throw error
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 로그아웃
   */
  async function logout() {
    try {
      await authApi.logout()
    } finally {
      // 상태 초기화
      isAuthenticated.value = false
      userId.value = null
      userStatus.value = null
      profile.value = null
    }
  }

  /**
   * 회원 탈퇴
   */
  async function withdraw() {
    try {
      isLoading.value = true
      await userApi.withdraw()
      // 상태 초기화
      isAuthenticated.value = false
      userId.value = null
      userStatus.value = null
      profile.value = null
    } catch (error) {
      console.error('회원 탈퇴 실패:', error)
      throw error
    } finally {
      isLoading.value = false
    }
  }

  return {
    // State
    isAuthenticated,
    userId,
    userStatus,
    profile,
    isLoading,
    
    // Getters
    isNewUser,
    isActiveUser,
    nickname,
    allergies,
    
    // Actions
    checkLoginStatus,
    fetchProfile,
    signUp,
    updateProfile,
    logout,
    withdraw,
  }
})
