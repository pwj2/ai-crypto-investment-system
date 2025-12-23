import axios from 'axios'
import { ref, reactive } from 'vue'
import cache from '../utils/cache.js'

// 创建加载状态管理器
export const loadingState = reactive({
  requests: 0,
  get isLoading() {
    return this.requests > 0
  },
})

const axiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
  // 添加缓存配置
  cache: {
    enabled: true, // 默认启用缓存
    expireTime: 5 * 60 * 1000, // 默认缓存过期时间：5分钟
    excludeMethods: ['post', 'put', 'delete', 'patch'], // 排除的请求方法
  },
})

axiosInstance.interceptors.request.use(
  config => {
    // 增加请求计数
    loadingState.requests++

    // 检查是否需要缓存
    const cacheConfig = config.cache || axiosInstance.defaults.cache
    if (
      cacheConfig.enabled &&
      !cacheConfig.excludeMethods.includes(config.method.toLowerCase())
    ) {
      // 检查缓存
      const cachedData = cache.get(config.url, config.params || config.data)
      if (cachedData) {
        // 如果有缓存，直接返回缓存数据，减少请求计数
        loadingState.requests--
        return Promise.resolve({ data: cachedData })
      }
    }

    return config
  },
  error => {
    // 减少请求计数
    loadingState.requests--
    return Promise.reject(error)
  }
)

axiosInstance.interceptors.response.use(
  response => {
    // 减少请求计数
    loadingState.requests--

    // 检查是否需要缓存响应数据
    const cacheConfig = response.config.cache || axiosInstance.defaults.cache
    if (
      cacheConfig.enabled &&
      !cacheConfig.excludeMethods.includes(response.config.method.toLowerCase())
    ) {
      // 将响应数据存入缓存
      cache.set(
        response.config.url,
        response.config.params || response.config.data,
        response.data,
        cacheConfig.expireTime
      )
    }

    return response.data
  },
  error => {
    // 减少请求计数
    loadingState.requests--

    // 统一错误处理
    let errorMessage = '请求失败'

    if (error.response) {
      // 服务器返回错误状态码
      const { status, data } = error.response

      switch (status) {
        case 400:
          errorMessage = data.message || '请求参数错误'
          break
        case 401:
          errorMessage = '未授权，请重新登录'
          // 可以在这里添加跳转到登录页的逻辑
          break
        case 403:
          errorMessage = '权限不足，无法访问'
          break
        case 404:
          errorMessage = '请求的资源不存在'
          break
        case 500:
          errorMessage = '服务器内部错误'
          break
        default:
          errorMessage = `请求错误 (${status})`
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      errorMessage = '网络错误，无法连接到服务器'
    } else {
      // 请求配置错误
      errorMessage = error.message
    }

    console.error('API Error:', errorMessage, error)
    return Promise.reject({ message: errorMessage, originalError: error })
  }
)

export default axiosInstance
