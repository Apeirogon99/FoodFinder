import api from './index'

/**
 * 사용자 관련 API
 */
export const userApi = {
  /**
   * 회원가입 (프로필 설정)
   * @param {Object} data - {nickname: string, allergies: [{allergyType: string}]}
   * @returns {Promise<UserProfile>}
   */
  async signUp(data) {
    const response = await api.post('/users/signup', data)
    return response.data
  },

  /**
   * 프로필 조회
   * @returns {Promise<{githubId: string, nickname: string, allergies: [{allergyType: string}]}>}
   */
  async getProfile() {
    const response = await api.get('/users/profile')
    return response.data
  },

  /**
   * 프로필 수정
   * @param {Object} data - {nickname: string, allergies: [{allergyType: string}]}
   * @returns {Promise<UserProfile>}
   */
  async updateProfile(data) {
    const response = await api.patch('/users/profile', data)
    return response.data
  },

  /**
   * 회원 탈퇴
   */
  async withdraw() {
    await api.delete('/users/me')
  },
}

export default userApi
