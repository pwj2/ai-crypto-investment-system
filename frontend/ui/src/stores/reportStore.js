import { defineStore } from 'pinia'
import reportService from '../services/reportService'
import reviewService from '../services/reviewService'

export const useReportStore = defineStore('reports', {
  state: () => ({
    reports: [],
    pendingReports: [],
    loading: false,
    error: null
  }),
  
  actions: {
    async fetchReports() {
      this.loading = true
      this.error = null
      try {
        const response = await reportService.getReports()
        this.reports = response.data
        this.pendingReports = this.reports.filter(report => report.status === 'PENDING')
      } catch (error) {
        this.error = error.message
        console.error('获取报告数据失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    async approveReport(reportId) {
      this.loading = true
      this.error = null
      try {
        await reviewService.approveReport(reportId)
        await this.fetchReports()
        return true
      } catch (error) {
        this.error = error.message
        console.error('批准报告失败:', error)
        return false
      } finally {
        this.loading = false
      }
    },
    
    async rejectReport(reportId, reason) {
      this.loading = true
      this.error = null
      try {
        await reviewService.rejectReport(reportId, reason)
        await this.fetchReports()
        return true
      } catch (error) {
        this.error = error.message
        console.error('拒绝报告失败:', error)
        return false
      } finally {
        this.loading = false
      }
    }
  }
})
