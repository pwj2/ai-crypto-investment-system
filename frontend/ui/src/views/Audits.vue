<template>
  <div class="audits-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>审计记录</h2>
        </div>
      </template>
      
      <div class="audits-table">
        <el-table
          :data="auditRecords"
          style="width: 100%"
          stripe
          border
          v-loading="loading"
        >
          <el-table-column prop="id" label="记录ID" width="100"></el-table-column>
          <el-table-column prop="operationType" label="操作类型" width="150">
            <template #default="scope">
              <el-tag
                :type="getOperationTypeColor(scope.row.operationType)"
              >
                {{ scope.row.operationType }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="operationUser" label="操作用户" width="120"></el-table-column>
          <el-table-column prop="operationTime" label="操作时间" width="200">
            <template #default="scope">
              {{ formatDate(scope.row.operationTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="operationContent" label="操作内容" show-overflow-tooltip>
            <template #default="scope">
              {{ scope.row.operationContent }}
            </template>
          </el-table-column>
          <el-table-column prop="operationResult" label="操作结果" width="120">
            <template #default="scope">
              <el-tag
                :type="scope.row.operationResult === 'success' ? 'success' : 'danger'"
              >
                {{ scope.row.operationResult === 'success' ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="ipAddress" label="IP地址" width="150"></el-table-column>
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
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue'
import { auditService } from '../services/auditService'

export default defineComponent({
  name: 'Audits',
  setup() {
    const auditRecords = ref([])
    const loading = ref(false)
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)
    
    // 获取审计记录
    const fetchAuditRecords = async () => {
      loading.value = true
      try {
        const data = await auditService.getAuditRecords()
        auditRecords.value = data
        total.value = data.length
      } catch (error) {
        console.error('获取审计记录失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleString()
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
      fetchAuditRecords()
    })
    
    return {
      auditRecords,
      loading,
      total,
      currentPage,
      pageSize,
      fetchAuditRecords,
      formatDate,
      getOperationTypeColor,
      handleCurrentChange,
      handleSizeChange
    }
  }
})
</script>

<style scoped>
.audits-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.audits-table {
  margin-top: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>