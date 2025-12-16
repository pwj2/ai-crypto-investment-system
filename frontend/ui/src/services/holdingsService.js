import axios from './axios'

export const holdingsService = {
  // 获取当前持仓
  getCurrentHoldings: () => {
    return axios.get('/holdings/current')
  },
  
  // 获取历史持仓
  getHoldingsHistory: () => {
    return axios.get('/holdings/history')
  },
  
  // 更新持仓
  updateHoldings: (holdings) => {
    return axios.post('/holdings/update', holdings)
  },
  
  // 根据ID获取持仓详情
  getHoldingsById: (id) => {
    return axios.get(`/holdings/${id}`)
  }
}