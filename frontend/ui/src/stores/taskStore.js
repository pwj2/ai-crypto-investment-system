import { defineStore } from 'pinia'
import taskService from '../services/taskService'

export const useTaskStore = defineStore('tasks', {
  state: () => ({
    tasks: [],
    loading: false,
    error: null,
  }),

  actions: {
    async fetchTasks() {
      this.loading = true
      this.error = null
      try {
        const response = await taskService.getTasks()
        this.tasks = response.data
      } catch (error) {
        this.error = error.message
        console.error('获取任务数据失败:', error)
      } finally {
        this.loading = false
      }
    },

    async restartTask(taskId) {
      this.loading = true
      this.error = null
      try {
        const response = await taskService.restartTask(taskId)
        // 更新任务状态
        const index = this.tasks.findIndex(task => task.id === taskId)
        if (index !== -1) {
          this.tasks[index] = response.data
        }
        return true
      } catch (error) {
        this.error = error.message
        console.error('重启任务失败:', error)
        return false
      } finally {
        this.loading = false
      }
    },
  },
})
