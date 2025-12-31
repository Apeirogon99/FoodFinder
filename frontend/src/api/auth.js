import api from './index'

// 백엔드 기본 URL (window.location.href 용)
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

/**
 * 인증 관련 API
 */
export const authApi = {
  /**
   * GitHub OAuth 로그인 URL
   * 브라우저에서 직접 이동해야 하므로 전체 URL 반환
   */
  getGithubLoginUrl() {
    return `${API_BASE_URL}/oauth2/authorization/github`
  },

  /**
   * 로그인 성공 확인
   * @returns {Promise<{userId: number, status: 'PENDING' | 'ACTIVE'}>}
   */
  async checkLoginSuccess() {
    const response = await api.get('/v1/auth/login-success')
    return response.data
  },

  /**
   * 로그아웃
   */
  async logout() {
    await api.get('/auth/logout')
  },
}

export default authApi
