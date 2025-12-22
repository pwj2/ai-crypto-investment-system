<template>
  <div class="audits-container">
    <div class="page-header">
      <h1>审计记录</h1>
    </div>
    
    <el-card shadow="hover" class="main-card">
      <div class="table-controls">
        <el-input
          v-model="searchQuery"
          placeholder="搜索操作内容或IP地址"
          clearable
          prefix-icon="Search"
          class="search-input"
          @keyup.enter="handleSearch"
        ></el-input>
        
        <div class="filter-group">
          <el-select
            v-model="operationTypeFilter"
            placeholder="操作类型"
            clearable
            class="filter-select"
            @change="handleFilterChange"
          >
            <el-option label="全部" value=""></el-option>
            <el-option label="创建" value="CREATE"></el-option>
            <el-option label="更新" value="UPDATE"></el-option>
            <el-option label="删除" value="DELETE"></el-option>
            <el-option label="查询" value="QUERY"></el-option>
            <el-option label="审批" value="APPROVE"></el-option>
          </el-select>
          
          <el-select
            v-model="operationResultFilter"
            placeholder="操作结果"
            clearable
            class="filter-select"
            @change="handleFilterChange"
          >
            <el-option label="全部" value=""></el-option>
            <el-option label="成功" value="success"></el-option>
            <el-option label="失败" value="failed"></el-option>
          </el-select>
          
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            clearable
            class="date-filter"
            @change="handleFilterChange"
          ></el-date-picker>
        </div>
      </div>
      
      <div class="audits-table">
        <el-table
          :data="filteredAuditRecords"
          style="width: 100%"
          stripe
          border
          v-loading="loading"
          empty-text="暂无审计记录数据"
          highlight-current-row
        >
          <el-table-column prop="id" label="记录ID" width="100" align="center"></el-table-column>
          <el-table-column prop="operationType" label="操作类型" width="150" align="center">
            <template #default="scope">
              <el-tag
                :type="getOperationTypeColor(scope.row.operationType)"
                effect="dark"
              >
                {{ getOperationTypeLabel(scope.row.operationType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="operationUser" label="操作用户" width="120" align="center">
            <template #default="scope">
              <div class="user-name">{{ scope.row.operationUser }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="operationTime" label="操作时间" width="200" align="center">
            <template #default="scope">
              <div class="time-text">{{ formatDate(scope.row.operationTime) }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="operationContent" label="操作内容" show-overflow-tooltip min-width="300">
            <template #default="scope">
              <div class="operation-content" v-html="formatContent(scope.row.operationContent)"></div>
            </template>
          </el-table-column>
          <el-table-column prop="operationResult" label="操作结果" width="120" align="center">
            <template #default="scope">
              <el-tag
                :type="scope.row.operationResult === 'success' ? 'success' : 'danger'"
                effect="dark"
              >
                {{ scope.row.operationResult === 'success' ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="ipAddress" label="IP地址" width="150" align="center">
            <template #default="scope">
              <div class="ip-address">{{ scope.row.ipAddress }}</div>
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
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, computed } from 'vue'
import { auditService } from '../services/auditService'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

export default defineComponent({
  name: 'Audits',
  components: {
    Search
  },
  setup() {
    const auditRecords = ref([])
    const loading = ref(false)
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const searchQuery = ref('')
    const operationTypeFilter = ref('')
    const operationResultFilter = ref('')
    const dateRange = ref([])
    
    // 获取审计记录
    const fetchAuditRecords = async () => {
      loading.value = true
      try {
        const response = await auditService.getAuditRecords(currentPage.value, pageSize.value)
        // 将下划线命名转换为驼峰命名
        auditRecords.value = response.data.map(record => ({
          id: record.id,
          operationType: record.operation_type,
          operationUser: record.operation_user,
          operationTime: record.operation_time,
          operationContent: record.operation_content,
          operationResult: record.operation_result,
          ipAddress: record.ip_address
        }))
        total.value = response.pagination.total
      } catch (error) {
        console.error('获取审计记录失败:', error)
        ElMessage.error('获取审计记录失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleString()
    }
    
    // 格式化内容（处理换行）
    const formatContent = (content) => {
      if (!content) return ''
      return content.replace(/\n/g, '<br>')
    }
    
    // 获取操作类型的颜色
    const getOperationTypeColor = (type) => {
      const colorMap = {
        'CREATE': 'success',
        'UPDATE': 'primary',
        'DELETE': 'danger',
        'QUERY': 'info',
        'APPROVE': 'warning'
      }
      return colorMap[type] || 'info'
    }
    
    // 获取操作类型的标签
    const getOperationTypeLabel = (type) => {
      const labelMap = {
        'CREATE': '创建',
        'UPDATE': '更新',
        'DELETE': '删除',
        'QUERY': '查询',
        'APPROVE': '审批'
      }
      return labelMap[type] || type
    }
    
    // 处理搜索
    const handleSearch = () => {
      currentPage.value = 1
      fetchAuditRecords()
    }
    
    // 处理筛选变化
    const handleFilterChange = () => {
      currentPage.value = 1
      fetchAuditRecords()
    }
    
    // 处理当前页变化
    const handleCurrentChange = (val) => {
      currentPage.value = val
      fetchAuditRecords()
    }
    
    // 处理每页条数变化
    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
      fetchAuditRecords()
    }
    
    // 筛选后的审计记录
    const filteredAuditRecords = computed(() => {
      let result = [...auditRecords.value]
      
      // 搜索过滤
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        result = result.filter(record => 
          record.operationContent.toLowerCase().includes(query) || 
          record.ipAddress.toLowerCase().includes(query)
        )
      }
      
      // 操作类型过滤
      if (operationTypeFilter.value) {
        result = result.filter(record => record.operationType === operationTypeFilter.value)
      }
      
      // 操作结果过滤
      if (operationResultFilter.value) {
        result = result.filter(record => record.operationResult === operationResultFilter.value)
      }
      
      // 日期范围过滤
      if (dateRange.value && dateRange.value.length === 2) {
        const startDate = new Date(dateRange.value[0])
        const endDate = new Date(dateRange.value[1])
        endDate.setHours(23, 59, 59, 999)
        
        result = result.filter(record => {
          const recordDate = new Date(record.operationTime)
          return recordDate >= startDate && recordDate <= endDate
        })
      }
      
      return result
    })
    
    onMounted(() => {
      fetchAuditRecords()
    })
    
    return {
      auditRecords,
      loading,
      total,
      currentPage,
      pageSize,
      searchQuery,
      operationTypeFilter,
      operationResultFilter,
      dateRange,
      fetchAuditRecords,
      formatDate,
      formatContent,
      getOperationTypeColor,
      getOperationTypeLabel,
      handleSearch,
      handleFilterChange,
      handleCurrentChange,
      handleSizeChange,
      filteredAuditRecords
    }
  }
})
</script>

<style scoped>
.audits-container {
  padding: var(--spacing-lg);
  animation: fadeIn 0.6s ease-out forwards;
}

.page-header {
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-md);
  border-bottom: 2px solid var(--border-light);
}

.page-header h1 {
  font-size: 28px;
  color: var(--text-primary);
  font-weight: 600;
  margin-bottom: 0;
}

.main-card {
  background-color: var(--card-background);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  transition: var(--transition);
  border: 1px solid var(--border-light);
  overflow: hidden;
}

.main-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), var(--accent-color));
}

.main-card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

.table-controls {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--border-light);
}

.search-input {
  min-width: 280px;
  flex: 1;
}

.filter-group {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
  align-items: center;
}

.filter-select {
  min-width: 150px;
  width: 150px;
}

.date-filter {
  min-width: 280px;
  width: 280px;
}

.audits-table {
  margin-top: var(--spacing-md);
}

.el-table {
  border-radius: var(--border-radius-md);
  overflow: hidden;
}

.el-table th {
  background-color: var(--bg-tertiary);
  font-weight: 600;
  color: var(--text-primary);
  border-bottom: 2px solid var(--border-medium);
}

.el-table tr:hover {
  background-color: var(--bg-secondary) !important;
}

.el-table td {
  color: var(--text-secondary);
  border-bottom: 1px solid var(--border-light);
  transition: var(--transition);
}

.operation-content {
  line-height: 1.6;
  color: var(--text-secondary);
}

.ip-address {
  font-family: 'Courier New', monospace;
  color: var(--text-tertiary);
}

.time-text {
  color: var(--text-tertiary);
}

.user-name {
  font-weight: 500;
  color: var(--text-primary);
}

.pagination-container {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--border-light);
}

.table-info {
  color: var(--text-tertiary);
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .table-controls {
    flex-direction: column;
  }
  
  .search-input,
  .date-filter {
    width: 100%;
  }
  
  .filter-group {
    width: 100%;
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .audits-container {
    padding: var(--spacing-md);
  }
  
  .page-header h1 {
    font-size: 24px;
  }
  
  .filter-select {
    width: 100%;
    min-width: 100%;
  }
  
  .pagination-container {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: stretch;
  }
  
  .el-table {
    font-size: 13px;
  }
  
  .el-table th,
  .el-table td {
    padding: 8px 6px;
  }
}

@media (max-width: 576px) {
  .audits-container {
    padding: var(--spacing-sm);
  }
  
  .filter-group {
    flex-direction: column;
  }
  
  .date-filter {
    width: 100%;
  }
}
</style>