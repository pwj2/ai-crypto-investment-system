import axios from './axios'

export const taskService = {
  // 获取任务列表
  getTasks: () => {
    return axios.get('/tasks')
  },
}
