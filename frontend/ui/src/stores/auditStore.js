import { defineStore } from 'pinia'
import auditService from '../services/auditService'

export const useAuditStore = defineStore('audits', {
  state: () => ({
    audits: [],
    loading: false,
    error: null,
    total: 0,
  }),

  actions: {
    async fetchAudits(page = 1, pageSize = 10) {
      this.loading = true
      this.error = null
      try {
        const response = await auditService.getAudits(page, pageSize)
        this.audits = response.data.content
        this.total = response.data.totalElements
      } catch (error) {
        this.error = error.message
        console.error('获取审计记录失败:', error)
      } finally {
        this.loading = false
      }
    },

    async fetchAuditsByDateRange(startDate, endDate) {
      this.loading = true
      this.error = null
      try {
        const response = await auditService.getAuditsByDateRange(
          startDate,
          endDate
        )
        this.audits = response.data
      } catch (error) {
        this.error = error.message
        console.error('按日期范围获取审计记录失败:', error)
      } finally {
        this.loading = false
      }
    },
  },
})
