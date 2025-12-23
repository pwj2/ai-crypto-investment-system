import axios from './axios'

export const reportService = {
  // 获取建议报告列表（支持分页）
  getSuggestReports: (page = 1, limit = 10) => {
    return axios.get('/suggest-reports', { params: { page, limit } })
  },

  // 创建建议报告
  createSuggestReport: report => {
    return axios.post('/suggest-reports', report)
  },

  // 获取建议报告详情
  getSuggestReportDetail: id => {
    return axios.get(`/suggest-reports/${id}`)
  },

  // 更新建议报告状态
  updateSuggestReportStatus: (id, status) => {
    return axios.put(`/suggest-reports/${id}/status`, { status })
  },

  // 删除建议报告
  deleteSuggestReport: id => {
    return axios.delete(`/suggest-reports/${id}`)
  },

  // 获取消息分析报告
  getMessageAnalysisReports: () => {
    return axios.get('/message-analysis-reports')
  },
}
