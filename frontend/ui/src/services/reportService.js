import axios from './axios'

export const reportService = {
  // 获取建议报告列表
  getSuggestReports: () => {
    return axios.get('/suggest-reports')
  },
  
  // 创建建议报告
  createSuggestReport: (report) => {
    return axios.post('/suggest-reports', report)
  },
  
  // 获取消息分析报告
  getMessageAnalysisReports: () => {
    return axios.get('/message-analysis-reports')
  }
}