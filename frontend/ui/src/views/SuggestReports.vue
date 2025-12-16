<template>
  <div class="suggest-reports-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>建议报告管理</h2>
          <el-button type="primary" @click="showCreateDialog = true">创建报告</el-button>
        </div>
      </template>
      
      <div class="reports-table">
        <el-table
          :data="reports"
          style="width: 100%"
          stripe
          border
          v-loading="loading"
        >
          <el-table-column prop="id" label="报告ID" width="100"></el-table-column>
          <el-table-column prop="reportName" label="报告名称" width="180"></el-table-column>
          <el-table-column prop="content" label="报告内容" show-overflow-tooltip>
            <template #default="scope">
              {{ scope.row.content.substring(0, 100) }}...
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag
                :type="scope.row.status === 'pending' ? 'warning' : scope.row.status === 'approved' ? 'success' : 'danger'"
              >
                {{ scope.row.status === 'pending' ? '待审核' : scope.row.status === 'approved' ? '已通过' : '已驳回' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdTime" label="创建时间" width="200">
            <template #default="scope">
              {{ formatDate(scope.row.createdTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="updatedTime" label="更新时间" width="200">
            <template #default="scope">
              {{ formatDate(scope.row.updatedTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button type="primary" size="small" @click="viewReport(scope.row)">查看</el-button>
              <el-button type="success" size="small" @click="approveReport(scope.row.id)" v-if="scope.row.status === 'pending'">通过</el-button>
              <el-button type="danger" size="small" @click="rejectReport(scope.row.id)" v-if="scope.row.status === 'pending'">驳回</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination-container">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            :page-size="pageSize"
            :current-page="currentPage"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          ></el-pagination>
        </div>
      </div>
    </el-card>
    
    <!-- 查看报告对话框 -->
    <el-dialog
      v-model="showReportDialog"
      title="报告详情"
      width="80%"
    >
      <div v-if="currentReport" class="report-detail">
        <h3>{{ currentReport.reportName }}</h3>
        <div class="report-meta">
          <div>状态: 
            <el-tag
              :type="currentReport.status === 'pending' ? 'warning' : currentReport.status === 'approved' ? 'success' : 'danger'"
            >
              {{ currentReport.status === 'pending' ? '待审核' : currentReport.status === 'approved' ? '已通过' : '已驳回' }}
            </el-tag>
          </div>
          <div>创建时间: {{ formatDate(currentReport.createdTime) }}</div>
          <div>更新时间: {{ formatDate(currentReport.updatedTime) }}</div>
        </div>
        <div class="report-content">
          {{ currentReport.content }}
        </div>
      </div>
    </el-dialog>
    
    <!-- 创建报告对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="创建建议报告"
      width="60%"
    >
      <el-form :model="createForm" label-width="80px">
        <el-form-item label="报告名称">
          <el-input v-model="createForm.reportName" placeholder="请输入报告名称"></el-input>
        </el-form-item>
        <el-form-item label="报告内容">
          <el-input
            v-model="createForm.content"
            type="textarea"
            :rows="8"
            placeholder="请输入报告内容"
          ></el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" @click="submitCreate">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue'
import { reportService } from '../services/reportService'
import { reviewService } from '../services/reviewService'

export default defineComponent({
  name: 'SuggestReports',
  setup() {
    const reports = ref([])
    const loading = ref(false)
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const showReportDialog = ref(false)
    const showCreateDialog = ref(false)
    const currentReport = ref(null)
    const createForm = ref({
      reportName: '',
      content: ''
    })
    
    // 获取建议报告列表
    const fetchReports = async () => {
      loading.value = true
      try {
        const data = await reportService.getSuggestReports()
        reports.value = data
        total.value = data.length
      } catch (error) {
        console.error('获取建议报告失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleString()
    }
    
    // 查看报告
    const viewReport = (report) => {
      currentReport.value = report
      showReportDialog.value = true
    }
    
    // 审核通过
    const approveReport = async (reportId) => {
      try {
        await reviewService.approveReport(reportId)
        ElMessage.success('报告审核通过')
        fetchReports()
      } catch (error) {
        console.error('审核通过失败:', error)
        ElMessage.error('审核通过失败')
      }
    }
    
    // 审核驳回
    const rejectReport = async (reportId) => {
      try {
        const reason = await ElMessageBox.prompt('请输入驳回原因:', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        })
        
        if (reason.value) {
          await reviewService.rejectReport(reportId, reason.value)
          ElMessage.success('报告已驳回')
          fetchReports()
        }
      } catch (error) {
        console.error('审核驳回失败:', error)
        if (error !== 'cancel') {
          ElMessage.error('审核驳回失败')
        }
      }
    }
    
    // 提交创建报告
    const submitCreate = async () => {
      try {
        await reportService.createSuggestReport(createForm.value)
        showCreateDialog.value = false
        fetchReports()
        ElMessage.success('报告创建成功')
        
        // 重置表单
        createForm.value = {
          reportName: '',
          content: ''
        }
      } catch (error) {
        console.error('创建报告失败:', error)
        ElMessage.error('创建报告失败')
      }
    }
    
    // 处理当前页变化
    const handleCurrentChange = (val) => {
      currentPage.value = val
      // 实现分页逻辑
    }
    
    // 处理每页条数变化
    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
      // 实现分页逻辑
    }
    
    onMounted(() => {
      fetchReports()
    })
    
    return {
      reports,
      loading,
      total,
      currentPage,
      pageSize,
      showReportDialog,
      showCreateDialog,
      currentReport,
      createForm,
      fetchReports,
      formatDate,
      viewReport,
      approveReport,
      rejectReport,
      submitCreate,
      handleCurrentChange,
      handleSizeChange
    }
  }
})
</script>

<style scoped>
.suggest-reports-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.reports-table {
  margin-top: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.report-detail {
  padding: 10px;
}

.report-detail h3 {
  margin-bottom: 15px;
  color: #2c3e50;
}

.report-meta {
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.report-meta div {
  margin-bottom: 5px;
}

.report-content {
  padding: 10px;
  line-height: 1.6;
  white-space: pre-wrap;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>