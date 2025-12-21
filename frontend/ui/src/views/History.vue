<template>
  <div class="history-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>持仓历史</h2>
          <div class="history-filters">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              @change="handleDateChange"
            ></el-date-picker>
            <el-select v-model="selectedCoin" placeholder="选择币种" style="margin-left: 10px; width: 150px;">
              <el-option
                v-for="coin in coinOptions"
                :key="coin.value"
                :label="coin.label"
                :value="coin.value"
              ></el-option>
            </el-select>
          </div>
        </div>
      </template>
      
      <div class="history-content">
        <div class="history-chart">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="chart-header">
                <h3>资产变化趋势</h3>
              </div>
            </template>
            <div id="history-chart" ref="chartRef"></div>
          </el-card>
        </div>
        
        <div class="history-table">
          <el-table
            :data="historyData"
            style="width: 100%"
            stripe
            border
            v-loading="loading"
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
            <el-table-column prop="updatedTime" label="更新时间" width="200">
              <template #default="scope">
                {{ formatDate(scope.row.updatedTime) }}
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
      </div>
    </el-card>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, onUnmounted } from 'vue'
import { holdingsService } from '../services/holdingsService'
// 使用动态导入echarts，只在需要时加载
let echarts = null

export default defineComponent({
  name: 'History',
  setup() {
    const historyData = ref([])
    const chartRef = ref(null)
    let chart = null
    const loading = ref(false)
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const dateRange = ref([])
    const selectedCoin = ref('')
    const coinOptions = ref([
      { label: '全部币种', value: '' },
      { label: 'Bitcoin', value: 'BTC' },
      { label: 'Ethereum', value: 'ETH' },
      { label: 'Solana', value: 'SOL' },
      { label: 'Avalanche', value: 'AVAX' }
    ])
    
    // 获取历史持仓
    const fetchHistory = async () => {
      loading.value = true
      try {
        // 准备查询参数
        const params = {}
        if (dateRange.value.length === 2) {
          params.startDate = dateRange.value[0]
          params.endDate = dateRange.value[1]
        }
        if (selectedCoin.value) {
          params.coinType = selectedCoin.value
        }
        
        const data = await holdingsService.getHoldingsHistory(params)
        historyData.value = data
        total.value = data.length
        
        // 只有当图表未初始化且数据不为空时，才尝试初始化图表
        if (!chart && historyData.value.length > 0 && chartRef.value) {
          const isVisible = chartRef.value.getBoundingClientRect().top < window.innerHeight + 100
          if (isVisible) {
            initChart(data)
          }
        }
      } catch (error) {
        console.error('获取历史持仓失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 防抖函数
    const debounce = (func, delay) => {
      let timer
      return function(...args) {
        clearTimeout(timer)
        timer = setTimeout(() => func.apply(this, args), delay)
      }
    }
    
    // 初始化图表（使用懒加载）
    const initChart = (data) => {
      if (chartRef.value && !chart) {
        // 检查图表是否在视口中
        const isVisible = chartRef.value.getBoundingClientRect().top < window.innerHeight + 100
        
        if (isVisible) {
          chart = echarts.init(chartRef.value)
          
          // 按时间分组数据（优化数据处理）
          const timeMap = new Map()
          data.forEach(item => {
            const date = new Date(item.updatedTime).toLocaleDateString()
            if (!timeMap.has(date)) {
              timeMap.set(date, { date })
            }
            timeMap.get(date)[item.coinType] = item.totalValue
          })
          
          const sortedDates = Array.from(timeMap.keys()).sort()
          const series = ['BTC', 'ETH', 'SOL', 'AVAX'].map(coinType => {
            return {
              name: coinType,
              type: 'line',
              smooth: true,
              data: sortedDates.map(date => timeMap.get(date)[coinType] || 0)
            }
          })
          
          const option = {
            tooltip: {
              trigger: 'axis'
            },
            legend: {
              data: ['BTC', 'ETH', 'SOL', 'AVAX']
            },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true
            },
            xAxis: {
              type: 'category',
              boundaryGap: false,
              data: sortedDates
            },
            yAxis: {
              type: 'value',
              axisLabel: {
                formatter: '${value}'
              }
            },
            series: series
          }
          
          chart.setOption(option)
        }
      }
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleString()
    }
    
    // 处理日期范围变化
    const handleDateChange = () => {
      // 日期范围变化时重新获取数据
      console.log('日期范围变化:', dateRange.value)
      fetchHistory()
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
    
    // 窗口大小变化处理（防抖）
    const handleResize = debounce(() => {
      chart?.resize()
    }, 100)
    
    // 滚动事件处理（用于检测图表是否进入视口）
    const handleScroll = debounce(() => {
      if (!chart && chartRef.value && historyData.value.length > 0) {
        initChart(historyData.value)
      }
    }, 100)
    
    onMounted(() => {
      fetchHistory()
      
      // 监听窗口大小变化
      window.addEventListener('resize', handleResize)
      
      // 监听滚动事件用于懒加载
      window.addEventListener('scroll', handleScroll)
      
      // 保存事件处理函数以便清理
      window.__historyResizeHandler = handleResize
      window.__historyScrollHandler = handleScroll
    })
    
    onUnmounted(() => {
      // 清理资源
      window.removeEventListener('resize', window.__historyResizeHandler)
      window.removeEventListener('scroll', window.__historyScrollHandler)
      
      // 销毁图表实例
      chart?.dispose()
      chart = null
    })
    
    return {
      historyData,
      chartRef,
      loading,
      total,
      currentPage,
      pageSize,
      dateRange,
      selectedCoin,
      coinOptions,
      fetchHistory,
      formatDate,
      handleDateChange,
      handleCurrentChange,
      handleSizeChange
    }
  }
})
</script>

<style scoped>
.history-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.history-filters {
  display: flex;
  align-items: center;
}

.history-content {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

.history-chart {
  flex: 1;
  min-width: 300px;
}

.chart-card {
  height: 400px;
}

#history-chart {
  width: 100%;
  height: 300px;
}

.history-table {
  flex: 2;
  min-width: 500px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>