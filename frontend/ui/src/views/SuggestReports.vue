<template>
  <div class="suggest-reports-container">
    <div class="page-header">
      <h1>建议报告管理</h1>
      <el-button type="primary" size="large" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon> 创建报告
      </el-button>
    </div>
    
    <el-card shadow="hover" class="main-card">
      <div class="table-controls">
        <el-input
          v-model="searchQuery"
          placeholder="搜索报告名称或内容"
          clearable
          prefix-icon="Search"
          class="search-input"
          @keyup.enter="handleSearch"
        ></el-input>
        
        <el-select
          v-model="statusFilter"
          placeholder="筛选状态"
          clearable
          class="status-filter"
          @change="handleFilterChange"
        >
          <el-option label="全部" value=""></el-option>
          <el-option label="待审核" value="pending"></el-option>
          <el-option label="已通过" value="approved"></el-option>
          <el-option label="已驳回" value="rejected"></el-option>
        </el-select>
      </div>
      
      <div class="reports-table">
        <el-table
          :data="filteredReports"
          style="width: 100%"
          stripe
          border
          v-loading="loading"
          empty-text="暂无报告数据"
          highlight-current-row
        >
          <el-table-column prop="id" label="报告ID" width="100" align="center"></el-table-column>
          <el-table-column prop="reportName" label="报告名称" width="200">
            <template #default="scope">
              <div class="report-name">{{ scope.row.reportName }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="content" label="报告内容" show-overflow-tooltip min-width="300">
            <template #default="scope">
              <div class="report-preview">{{ scope.row.content.substring(0, 120) }}...</div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="130" align="center">
            <template #default="scope">
              <el-tag
                :type="scope.row.status === 'pending' ? 'warning' : scope.row.status === 'approved' ? 'success' : 'danger'"
                effect="dark"
              >
                {{ scope.row.status === 'pending' ? '待审核' : scope.row.status === 'approved' ? '已通过' : '已驳回' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdTime" label="创建时间" width="200" align="center">
            <template #default="scope">
              <div class="time-text">{{ formatDate(scope.row.createdTime) }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="updatedTime" label="更新时间" width="200" align="center">
            <template #default="scope">
              <div class="time-text">{{ formatDate(scope.row.updatedTime) }}</div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" align="center">
            <template #default="scope">
              <el-button type="primary" size="small" @click="viewReport(scope.row)">
                <el-icon><View /></el-icon> 查看
              </el-button>
              <el-button type="success" size="small" @click="approveReport(scope.row)" v-if="scope.row.status === 'pending'">
                <el-icon><CircleCheck /></el-icon> 通过
              </el-button>
              <el-button type="danger" size="small" @click="rejectReport(scope.row)" v-if="scope.row.status === 'pending'">
                <el-icon><CircleClose /></el-icon> 驳回
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination-container">
          <div class="table-info">
            <span>共 {{ total }} 条记录</span>
          </div>
          <el-pagination
            background
            layout="sizes, prev, pager, next, jumper"
            :total="total"
            :page-size="pageSize"
            :current-page="currentPage"
            :page-sizes="[5, 10, 20, 50]"
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
      :close-on-click-modal="false"
      center
    >
      <div v-if="currentReport" class="report-detail">
        <div class="report-header">
          <h3>{{ currentReport.reportName }}</h3>
          <el-tag
            :type="currentReport.status === 'pending' ? 'warning' : currentReport.status === 'approved' ? 'success' : 'danger'"
            effect="dark"
            size="large"
          >
            {{ currentReport.status === 'pending' ? '待审核' : currentReport.status === 'approved' ? '已通过' : '已驳回' }}
          </el-tag>
        </div>
        
        <div class="report-meta">
          <div class="meta-item">
            <el-icon class="meta-icon"><Document /></el-icon>
            <span class="meta-label">报告ID:</span>
            <span>{{ currentReport.id }}</span>
          </div>
          <div class="meta-item">
            <el-icon class="meta-icon"><Clock /></el-icon>
            <span class="meta-label">创建时间:</span>
            <span>{{ formatDate(currentReport.createdTime) }}</span>
          </div>
          <div class="meta-item">
            <el-icon class="meta-icon"><RefreshRight /></el-icon>
            <span class="meta-label">更新时间:</span>
            <span>{{ formatDate(currentReport.updatedTime) }}</span>
          </div>
        </div>
        
        <div class="report-content">
          <h4>报告内容:</h4>
          <div class="content-text" v-html="formatContent(currentReport.content)"></div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showReportDialog = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 创建报告对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="创建建议报告"
      width="70%"
      :close-on-click-modal="false"
      center
    >
      <el-form :model="createForm" :rules="formRules" ref="createFormRef" label-width="100px">
        <el-form-item label="报告名称" prop="reportName">
          <el-input 
            v-model="createForm.reportName" 
            placeholder="请输入报告名称"
            maxlength="100"
            show-word-limit
          ></el-input>
        </el-form-item>
        <el-form-item label="报告内容" prop="content">
          <el-input
            v-model="createForm.content"
            type="textarea"
            :rows="12"
            placeholder="请输入报告详细内容"
            resize="vertical"
            maxlength="5000"
            show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="resetCreateForm">取消</el-button>
          <el-button type="primary" @click="submitCreate" :loading="submitting">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, computed } from 'vue'
import { reportService } from '../services/reportService'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, View, CircleCheck, CircleClose, Document, Clock, RefreshRight, Search } from '@element-plus/icons-vue'

export default defineComponent({
  name: 'SuggestReports',
  components: {
    Plus,
    View,
    CircleCheck,
    CircleClose,
    Document,
    Clock,
    RefreshRight,
    Search
  },
  setup() {
    const reports = ref([])
    const loading = ref(false)
    const submitting = ref(false)
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const showReportDialog = ref(false)
    const showCreateDialog = ref(false)
    const currentReport = ref(null)
    const searchQuery = ref('')
    const statusFilter = ref('')
    const createFormRef = ref(null)
    const createForm = ref({
      reportName: '',
      content: ''
    })
    
    // 表单验证规则
    const formRules = ref({
      reportName: [
        { required: true, message: '请输入报告名称', trigger: 'blur' },
        { min: 3, max: 100, message: '报告名称长度在 3 到 100 个字符', trigger: 'blur' }
      ],
      content: [
        { required: true, message: '请输入报告内容', trigger: 'blur' },
        { min: 10, max: 5000, message: '报告内容长度在 10 到 5000 个字符', trigger: 'blur' }
      ]
    })
    
    // 获取建议报告列表
    const fetchReports = async () => {
      loading.value = true
      try {
        const response = await reportService.getSuggestReports(currentPage.value, pageSize.value)
        // 映射后端返回的字段名到前端期望的格式
        reports.value = response.data.map(item => ({
          id: item.id,
          reportName: item.report_name,
          content: item.report_content,
          status: item.status,
          createdTime: item.create_time,
          updatedTime: item.update_time
        }))
        total.value = response.pagination.total
      } catch (error) {
        console.error('获取建议报告失败:', error)
        ElMessage.error('获取建议报告失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleString()
    }
    
    // 格式化报告内容（处理换行）
    const formatContent = (content) => {
      if (!content) return ''
      return content.replace(/\n/g, '<br>')
    }
    
    // 查看报告
    const viewReport = (report) => {
      currentReport.value = report
      showReportDialog.value = true
    }
    
    // 审核通过
    const approveReport = async (report) => {
      try {
        await ElMessageBox.confirm('确定要审核通过该报告吗？', '审核确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'success'
        })
        
        await reportService.updateSuggestReportStatus(report.id, 'approved')
        ElMessage.success('报告审核通过')
        fetchReports()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('审核通过失败:', error)
          ElMessage.error('审核通过失败，请稍后重试')
        }
      }
    }
    
    // 审核驳回
    const rejectReport = async (report) => {
      try {
        await ElMessageBox.confirm('确定要驳回该报告吗？', '审核确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await reportService.updateSuggestReportStatus(report.id, 'rejected')
        ElMessage.success('报告已驳回')
        fetchReports()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('审核驳回失败:', error)
          ElMessage.error('审核驳回失败，请稍后重试')
        }
      }
    }
    
    // 提交创建报告
    const submitCreate = async () => {
      if (!createFormRef.value) return
      
      try {
        await createFormRef.value.validate()
        submitting.value = true
        
        await reportService.createSuggestReport(createForm.value)
        showCreateDialog.value = false
        fetchReports()
        ElMessage.success('报告创建成功')
        
        // 重置表单
        resetCreateForm()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('创建报告失败:', error)
          if (error.name !== 'Error') {
            ElMessage.error('创建报告失败，请稍后重试')
          }
        }
      } finally {
        submitting.value = false
      }
    }
    
    // 重置创建表单
    const resetCreateForm = () => {
      if (createFormRef.value) {
        createFormRef.value.resetFields()
      }
      createForm.value = {
        reportName: '',
        content: ''
      }
    }
    
    // 处理搜索
    const handleSearch = () => {
      currentPage.value = 1
      fetchReports()
    }
    
    // 处理筛选变化
    const handleFilterChange = () => {
      currentPage.value = 1
      fetchReports()
    }
    
    // 处理当前页变化
    const handleCurrentChange = (val) => {
      currentPage.value = val
      fetchReports()
    }
    
    // 处理每页条数变化
    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
      fetchReports()
    }
    
    // 筛选后的报告数据
    const filteredReports = computed(() => {
      let result = [...reports.value]
      
      // 搜索过滤
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        result = result.filter(report => 
          report.reportName.toLowerCase().includes(query) || 
          report.content.toLowerCase().includes(query)
        )
      }
      
      // 状态过滤
      if (statusFilter.value) {
        result = result.filter(report => report.status === statusFilter.value)
      }
      
      return result
    })
    
    onMounted(() => {
      fetchReports()
    })
    
    return {
      reports,
      loading,
      submitting,
      total,
      currentPage,
      pageSize,
      showReportDialog,
      showCreateDialog,
      currentReport,
      searchQuery,
      statusFilter,
      createFormRef,
      createForm,
      formRules,
      fetchReports,
      formatDate,
      formatContent,
      viewReport,
      approveReport,
      rejectReport,
      submitCreate,
      resetCreateForm,
      handleSearch,
      handleFilterChange,
      handleCurrentChange,
      handleSizeChange,
      filteredReports
    }
  }
})
</script>

<style scoped>
.suggest-reports-container {
  padding: 20px;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0 5px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.main-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
}

.table-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.search-input {
  width: 300px;
  max-width: 100%;
}

.status-filter {
  width: 150px;
}

.reports-table {
  margin-top: 20px;
}

.el-table {
  border-radius: 4px;
  overflow: hidden;
}

.el-table__header-wrapper th {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #2c3e50;
}

.report-name {
  font-weight: 500;
  color: #2c3e50;
}

.report-preview {
  color: #606266;
  line-height: 1.4;
}

.time-text {
  color: #909399;
  font-size: 13px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.table-info {
  color: #606266;
  font-size: 14px;
}

.report-detail {
  padding: 20px;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.report-header h3 {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.report-meta {
  display: flex;
  gap: 30px;
  margin-bottom: 25px;
  padding: 15px 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  font-size: 14px;
}

.meta-icon {
  color: #409eff;
  font-size: 16px;
}

.meta-label {
  font-weight: 500;
  color: #2c3e50;
}

.report-content {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.report-content h4 {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 15px;
}

.content-text {
  line-height: 1.8;
  color: #303133;
  white-space: pre-wrap;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .suggest-reports-container {
    padding: 10px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .page-header h1 {
    font-size: 24px;
  }
  
  .table-controls {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input,
  .status-filter {
    width: 100%;
  }
  
  .pagination-container {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .report-meta {
    flex-direction: column;
    gap: 10px;
  }
  
  .report-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>