import axios from './axios'

export const auditService = {
  // 获取审计记录
  getAuditRecords: (page = 1, limit = 10) => {
    return axios.get('/audit-records', { params: { page, limit } })
  },
}
