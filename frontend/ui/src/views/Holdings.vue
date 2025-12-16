<template>
  <div class="holdings-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>当前持仓</h2>
          <el-button type="primary" @click="showUpdateDialog = true">更新持仓</el-button>
        </div>
      </template>
      
      <div class="holdings-content">
        <div class="holdings-chart">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="chart-header">
                <h3>资产分布</h3>
              </div>
            </template>
            <div id="holdings-chart" ref="chartRef"></div>
          </el-card>
        </div>
        
        <div class="holdings-table">
          <el-table
            :data="holdings"
            style="width: 100%"
            stripe
            border
          >
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="coinName" label="货币名称" width="120"></el-table-column>
            <el-table-column prop="coinType" label="货币类型" width="100"></el-table-column>
            <el-table-column prop="quantity" label="数量" width="120">
              <template #default="scope">
                {{ scope.row.quantity.toFixed(4) }}
              </template>
            </el-table-column>
            <el-table-column prop="price" label="价格" width="150">
              <template #default="scope">
                ${{ scope.row.price.toLocaleString('en-US', { minimumFractionDigits: 2 }) }}
              </template>
            </el-table-column>
            <el-table-column prop="totalValue" label="总价值" width="180">
              <template #default="scope">
                ${{ scope.row.totalValue.toLocaleString('en-US', { minimumFractionDigits: 2 }) }}
              </template>
            </el-table-column>
            <el-table-column prop="proportion" label="占比" width="100">
              <template #default="scope">
                {{ scope.row.proportion }}%
              </template>
            </el-table-column>
            <el-table-column prop="updatedTime" label="更新时间" width="200">
              <template #default="scope">
                {{ formatDate(scope.row.updatedTime) }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-card>
    
    <!-- 更新持仓对话框 -->
    <el-dialog
      v-model="showUpdateDialog"
      title="更新持仓"
      width="70%"
      :before-close="handleClose"
    >
      <el-form :model="updateForm" label-width="80px">
        <el-table
          :data="updateForm.holdings"
          style="width: 100%"
          border
        >
          <el-table-column prop="coinName" label="货币名称" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.coinName" placeholder="请输入货币名称"></el-input>
            </template>
          </el-table-column>
          <el-table-column prop="coinType" label="货币类型" width="120">
            <template #default="scope">
              <el-input v-model="scope.row.coinType" placeholder="请输入货币类型"></el-input>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="120">
            <template #default="scope">
              <el-input-number
                v-model="scope.row.quantity"
                :min="0"
                :step="0.0001"
                :precision="4"
              ></el-input-number>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="价格" width="120">
            <template #default="scope">
              <el-input-number
                v-model="scope.row.price"
                :min="0"
                :step="0.01"
                :precision="2"
              ></el-input-number>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button type="danger" size="small" @click="removeHolding(scope.$index)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="dialog-footer">
          <el-button type="primary" @click="addHolding">添加资产</el-button>
        </div>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showUpdateDialog = false">取消</el-button>
          <el-button type="primary" @click="submitUpdate">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { holdingsService } from '../services/holdingsService'

export default defineComponent({
  name: 'Holdings',
  setup() {
    const holdings = ref([])
    const chartRef = ref(null)
    let chart = null
    const showUpdateDialog = ref(false)
    const updateForm = ref({ holdings: [] })
    
    // 获取当前持仓
    const fetchHoldings = async () => {
      try {
        const data = await holdingsService.getCurrentHoldings()
        holdings.value = data
        initChart(data)
      } catch (error) {
        console.error('获取持仓失败:', error)
      }
    }
    
    // 初始化图表
    const initChart = (data) => {
      if (chartRef.value) {
        chart = echarts.init(chartRef.value)
        
        const chartData = data.map(item => ({
          name: item.coinName,
          value: item.totalValue
        }))
        
        const option = {
          tooltip: {
            trigger: 'item',
            formatter: '{b}: ${c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 'left'
          },
          series: [
            {
              name: '资产分布',
              type: 'pie',
              radius: '50%',
              data: chartData,
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              }
            }
          ]
        }
        
        chart.setOption(option)
      }
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleString()
    }
    
    // 处理对话框关闭
    const handleClose = () => {
      updateForm.value.holdings = []
    }
    
    // 添加资产
    const addHolding = () => {
      updateForm.value.holdings.push({
        coinName: '',
        coinType: '',
        quantity: 0,
        price: 0
      })
    }
    
    // 删除资产
    const removeHolding = (index) => {
      updateForm.value.holdings.splice(index, 1)
    }
    
    // 提交更新
    const submitUpdate = async () => {
      try {
        await holdingsService.updateHoldings(updateForm.value.holdings)
        showUpdateDialog.value = false
        fetchHoldings()
        ElMessage.success('持仓更新成功')
      } catch (error) {
        console.error('更新持仓失败:', error)
        ElMessage.error('更新持仓失败')
      }
    }
    
    onMounted(() => {
      fetchHoldings()
      window.addEventListener('resize', () => chart?.resize())
    })
    
    onUnmounted(() => {
      window.removeEventListener('resize', () => chart?.resize())
      chart?.dispose()
    })
    
    return {
      holdings,
      chartRef,
      showUpdateDialog,
      updateForm,
      fetchHoldings,
      formatDate,
      handleClose,
      addHolding,
      removeHolding,
      submitUpdate
    }
  }
})
</script>

<style scoped>
.holdings-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.holdings-content {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

.holdings-chart {
  flex: 1;
  min-width: 300px;
}

.chart-card {
  height: 400px;
}

#holdings-chart {
  width: 100%;
  height: 300px;
}

.holdings-table {
  flex: 1;
  min-width: 500px;
}

.dialog-footer {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>