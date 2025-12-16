<template>
  <div class="tasks-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>系统任务管理</h2>
        </div>
      </template>
      
      <div class="tasks-content">
        <div class="tasks-filters">
          <el-select v-model="statusFilter" placeholder="选择任务状态" style="width: 150px;">
            <el-option label="全部" value=""></el-option>
            <el-option label="运行中" value="running"></el-option>
            <el-option label="已完成" value="completed"></el-option>
            <el-option label="失败" value="failed"></el-option>
            <el-option label="待执行" value="pending"></el-option>
          </el-select>
          <el-select v-model="typeFilter" placeholder="选择任务类型" style="width: 150px; margin-left: 10px;">
            <el-option label="全部" value=""></el-option>
            <el-option label="数据采集" value="data_collection"></el-option>
            <el-option label="数据分析" value="data_analysis"></el-option>
            <el-option label="报告生成" value="report_generation"></el-option>
          </el-select>
        </div>
        
        <div class="tasks-table">
          <el-table
            :data="filteredTasks"
            style="width: 100%"
            stripe
            border
          >
            <el-table-column prop="id" label="任务ID" width="100"></el-table-column>
            <el-table-column prop="taskName" label="任务名称" width="180"></el-table-column>
            <el-table-column prop="taskType" label="任务类型" width="150">
              <template #default="scope">
                <el-tag
                  :type="getTaskTypeColor(scope.row.taskType)"
                >
                  {{ getTaskTypeLabel(scope.row.taskType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="任务状态" width="120">
              <template #default="scope">
                <el-tag
                  :type="getStatusColor(scope.row.status)"
                >
                  {{ getStatusLabel(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="progress" label="进度" width="200">
              <template #default="scope">
                <el-progress
                  :percentage="scope.row.progress"
                  :status="scope.row.status === 'completed' ? 'success' : scope.row.status === 'failed' ? 'exception' : ''"
                  :stroke-width="10"
                ></el-progress>
              </template>
            </el-table-column>
            <el-table-column prop="startTime" label="开始时间" width="200">
              <template #default="scope">
                {{ scope.row.startTime ? formatDate(scope.row.startTime) : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="endTime" label="结束时间" width="200">
              <template #default="scope">
                {{ scope.row.endTime ? formatDate(scope.row.endTime) : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="duration" label="执行时长" width="120">
              <template #default="scope">
                {{ scope.row.duration ? scope.row.duration + '秒' : '-' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button
                  type="primary"
                  size="small"
                  @click="viewTaskDetails(scope.row)"
                >
                  查看详情
                </el-button>
                <el-button
                  v-if="scope.row.status === 'failed' || scope.row.status === 'completed'"
                  type="warning"
                  size="small"
                  @click="restartTask(scope.row.id)"
                >
                  重新执行
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-card>
    
    <!-- 任务详情对话框 -->
    <el-dialog
      v-model="showTaskDialog"
      title="任务详情"
      width="70%"
    >
      <div v-if="currentTask" class="task-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="任务ID">{{ currentTask.id }}</el-descriptions-item>
          <el-descriptions-item label="任务名称">{{ currentTask.taskName }}</el-descriptions-item>
          <el-descriptions-item label="任务类型">{{ getTaskTypeLabel(currentTask.taskType) }}</el-descriptions-item>
          <el-descriptions-item label="任务状态">{{ getStatusLabel(currentTask.status) }}</el-descriptions-item>
          <el-descriptions-item label="进度">{{ currentTask.progress }}%</el-descriptions-item>
          <el-descriptions-item label="执行时长">{{ currentTask.duration ? currentTask.duration + '秒' : '-' }}</el-descriptions-item>
          <el-descriptions-item label="开始时间" :span="2">
            {{ currentTask.startTime ? formatDate(currentTask.startTime) : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="结束时间" :span="2">
            {{ currentTask.endTime ? formatDate(currentTask.endTime) : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="任务参数" :span="2">
            <pre>{{ JSON.stringify(currentTask.parameters, null, 2) }}</pre>
          </el-descriptions-item>
          <el-descriptions-item label="任务结果" :span="2">
            <pre>{{ JSON.stringify(currentTask.result, null, 2) }}</pre>
          </el-descriptions-item>
          <el-descriptions-item label="错误信息" :span="2">
            <pre v-if="currentTask.errorMessage">{{ currentTask.errorMessage }}</pre>
            <span v-else>无</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { defineComponent, ref, computed, onMounted } from 'vue'
import { taskService } from '../services/taskService'

export default defineComponent({
  name: 'Tasks',
  setup() {
    const tasks = ref([])
    const statusFilter = ref('')
    const typeFilter = ref('')
    const showTaskDialog = ref(false)
    const currentTask = ref(null)
    
    // 模拟任务数据
    tasks.value = [
      {
        id: 1,
        taskName: '消息采集任务',
        taskType: 'data_collection',
        status: 'running',
        progress: 65,
        startTime: '2025-12-16T10:00:00',
        endTime: null,
        duration: null,
        parameters: { source: 'twitter', keywords: ['BTC', 'ETH'], limit: 100 },
        result: null,
        errorMessage: null
      },
      {
        id: 2,
        taskName: '数据分析任务',
        taskType: 'data_analysis',
        status: 'completed',
        progress: 100,
        startTime: '2025-12-16T09:00:00',
        endTime: '2025-12-16T09:30:00',
        duration: 1800,
        parameters: { analysisType: 'sentiment', dataRange: '7d' },
        result: { positive: 85, neutral: 45, negative: 20 },
        errorMessage: null
      },
      {
        id: 3,
        taskName: '报告生成任务',
        taskType: 'report_generation',
        status: 'failed',
        progress: 0,
        startTime: '2025-12-16T08:00:00',
        endTime: '2025-12-16T08:05:00',
        duration: 300,
        parameters: { reportType: 'daily', date: '2025-12-16' },
        result: null,
        errorMessage: '数据库连接失败'
      },
      {
        id: 4,
        taskName: '数据采集任务',
        taskType: 'data_collection',
        status: 'pending',
        progress: 0,
        startTime: null,
        endTime: null,
        duration: null,
        parameters: { source: 'reddit', keywords: ['SOL', 'AVAX'], limit: 50 },
        result: null,
        errorMessage: null
      }
    ]
    
    // 筛选后的任务列表
    const filteredTasks = computed(() => {
      return tasks.value.filter(task => {
        const statusMatch = statusFilter.value ? task.status === statusFilter.value : true
        const typeMatch = typeFilter.value ? task.taskType === typeFilter.value : true
        return statusMatch && typeMatch
      })
    })
    
    // 获取任务列表
    const fetchTasks = async () => {
      try {
        const data = await taskService.getTasks()
        tasks.value = data
      } catch (error) {
        console.error('获取任务列表失败:', error)
      }
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleString()
    }
    
    // 获取任务类型标签
    const getTaskTypeLabel = (type) => {
      const labels = {
        data_collection: '数据采集',
        data_analysis: '数据分析',
        report_generation: '报告生成'
      }
      return labels[type] || type
    }
    
    // 获取任务类型颜色
    const getTaskTypeColor = (type) => {
      const colors = {
        data_collection: 'primary',
        data_analysis: 'success',
        report_generation: 'warning'
      }
      return colors[type] || 'info'
    }
    
    // 获取状态标签
    const getStatusLabel = (status) => {
      const labels = {
        running: '运行中',
        completed: '已完成',
        failed: '失败',
        pending: '待执行'
      }
      return labels[status] || status
    }
    
    // 获取状态颜色
    const getStatusColor = (status) => {
      const colors = {
        running: 'primary',
        completed: 'success',
        failed: 'danger',
        pending: 'info'
      }
      return colors[status] || 'info'
    }
    
    // 查看任务详情
    const viewTaskDetails = (task) => {
      currentTask.value = task
      showTaskDialog.value = true
    }
    
    // 重新执行任务
    const restartTask = (taskId) => {
      console.log('重新执行任务:', taskId)
      ElMessage.success('任务已重新执行')
    }
    
    onMounted(() => {
      fetchTasks()
    })
    
    return {
      tasks,
      statusFilter,
      typeFilter,
      filteredTasks,
      showTaskDialog,
      currentTask,
      fetchTasks,
      formatDate,
      getTaskTypeLabel,
      getTaskTypeColor,
      getStatusLabel,
      getStatusColor,
      viewTaskDetails,
      restartTask
    }
  }
})
</script>

<style scoped>
.tasks-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tasks-content {
  margin-top: 20px;
}

.tasks-filters {
  margin-bottom: 20px;
}

.task-detail pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  margin: 0;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  max-height: 300px;
  overflow-y: auto;
}
</style>