import axios from './axios'

export const auditService = {
  // 获取审计记录
  getAuditRecords: () => {
    return axios.get('/audit-records')
  }
}