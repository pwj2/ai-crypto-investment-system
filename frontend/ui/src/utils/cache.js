/**
 * 数据缓存工具类
 * 用于存储API请求的响应数据，减少重复请求
 */

class Cache {
  constructor() {
    this.cache = new Map()
    this.defaultExpireTime = 5 * 60 * 1000 // 默认缓存过期时间：5分钟
  }

  /**
   * 生成缓存键
   * @param {string} url - 请求URL
   * @param {object} params - 请求参数
   * @returns {string} 缓存键
   */
  generateKey(url, params = {}) {
    const paramsStr = JSON.stringify(params)
    return `${url}?${paramsStr}`
  }

  /**
   * 设置缓存
   * @param {string} url - 请求URL
   * @param {object} params - 请求参数
   * @param {any} data - 响应数据
   * @param {number} expireTime - 缓存过期时间（毫秒）
   */
  set(url, params = {}, data, expireTime = this.defaultExpireTime) {
    const key = this.generateKey(url, params)
    const cacheItem = {
      data,
      expireTime: expireTime ? Date.now() + expireTime : null, // null表示永不过期
    }
    this.cache.set(key, cacheItem)

    // 如果设置了过期时间，添加到过期检查列表
    if (expireTime) {
      this.addToExpireCheck(key, expireTime)
    }
  }

  /**
   * 获取缓存
   * @param {string} url - 请求URL
   * @param {object} params - 请求参数
   * @returns {any|null} 缓存数据，如果不存在或已过期则返回null
   */
  get(url, params = {}) {
    const key = this.generateKey(url, params)
    const cacheItem = this.cache.get(key)

    if (!cacheItem) {
      return null
    }

    // 检查是否过期
    if (cacheItem.expireTime && Date.now() > cacheItem.expireTime) {
      this.cache.delete(key)
      return null
    }

    return cacheItem.data
  }

  /**
   * 清除指定URL的缓存
   * @param {string} url - 请求URL
   * @param {object} params - 请求参数（可选），如果不提供则清除该URL的所有缓存
   */
  clear(url, params = null) {
    if (!params) {
      // 清除该URL的所有缓存
      const keysToDelete = []
      for (const key of this.cache.keys()) {
        if (key.startsWith(`${url}?`)) {
          keysToDelete.push(key)
        }
      }

      keysToDelete.forEach(key => this.cache.delete(key))
    } else {
      // 清除指定参数的缓存
      const key = this.generateKey(url, params)
      this.cache.delete(key)
    }
  }

  /**
   * 清除所有缓存
   */
  clearAll() {
    this.cache.clear()
    this.clearExpireTimers()
  }

  /**
   * 获取缓存大小
   * @returns {number} 缓存项数量
   */
  getSize() {
    return this.cache.size
  }

  /**
   * 添加到过期检查
   * @private
   * @param {string} key - 缓存键
   * @param {number} expireTime - 过期时间（毫秒）
   */
  addToExpireCheck(key, expireTime) {
    if (!this.expireTimers) {
      this.expireTimers = new Map()
    }

    // 清除已存在的定时器
    this.clearExpireTimer(key)

    // 创建新的定时器
    const timer = setTimeout(() => {
      this.cache.delete(key)
      this.expireTimers.delete(key)
    }, expireTime)

    this.expireTimers.set(key, timer)
  }

  /**
   * 清除指定键的过期定时器
   * @private
   * @param {string} key - 缓存键
   */
  clearExpireTimer(key) {
    if (this.expireTimers && this.expireTimers.has(key)) {
      clearTimeout(this.expireTimers.get(key))
      this.expireTimers.delete(key)
    }
  }

  /**
   * 清除所有过期定时器
   * @private
   */
  clearExpireTimers() {
    if (this.expireTimers) {
      for (const timer of this.expireTimers.values()) {
        clearTimeout(timer)
      }
      this.expireTimers.clear()
    }
  }
}

// 创建单例实例
const cache = new Cache()

export default cache
