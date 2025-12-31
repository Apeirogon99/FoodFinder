import axios from 'axios'

// 백엔드 API URL - OAuth 세션 쿠키와 동일한 도메인 사용
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// Axios 인스턴스 생성
const api = axios.create({
  baseURL: `${API_BASE_URL}/api`,
  timeout: 30000,
  withCredentials: true, // 세션 쿠키 전송 (중요!)
  headers: {
    'Content-Type': 'application/json',
  },
})

// 요청 인터셉터
api.interceptors.request.use(
  (config) => {
    console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`)
    return config
  },
  (error) => {
    console.error('[API Request Error]', error)
    return Promise.reject(error)
  }
)

// 응답 인터셉터
api.interceptors.response.use(
  (response) => {
    console.log(`[API Response] ${response.config.url}`, response.data)
    // ApiResult 구조에서 data만 반환
    return response.data
  },
  (error) => {
    console.error('[API Response Error]', error.response?.data || error.message)
    
    // 401 Unauthorized - 로그인 페이지로 리다이렉트
    if (error.response?.status === 401) {
      window.location.href = '/login'
    }
    
    return Promise.reject(error)
  }
)

export default api
