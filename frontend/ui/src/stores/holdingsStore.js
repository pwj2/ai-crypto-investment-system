import { defineStore } from 'pinia'
import holdingsService from '../services/holdingsService'

export const useHoldingsStore = defineStore('holdings', {
  state: () => ({
    holdings: [],
    totalHoldings: 0,
    loading: false,
    error: null,
  }),

  actions: {
    async fetchHoldings() {
      this.loading = true
      this.error = null
      try {
        const response = await holdingsService.getHoldings()
        this.holdings = response.data
        this.calculateTotalHoldings()
      } catch (error) {
        this.error = error.message
        console.error('获取持仓数据失败:', error)
      } finally {
        this.loading = false
      }
    },

    async updateHoldings(holdings) {
      this.loading = true
      this.error = null
      try {
        const response = await holdingsService.updateHoldings(holdings)
        this.holdings = response.data
        this.calculateTotalHoldings()
        return true
      } catch (error) {
        this.error = error.message
        console.error('更新持仓数据失败:', error)
        return false
      } finally {
        this.loading = false
      }
    },

    calculateTotalHoldings() {
      this.totalHoldings = this.holdings.reduce((total, holding) => {
        return total + holding.price * holding.quantity
      }, 0)
    },
  },
})
