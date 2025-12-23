<template>
  <div class="history-container">
    <el-card shadow="hover" class="animate-fadeIn">
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
            <el-select
              v-model="selectedCoin"
              placeholder="选择币种"
              style="margin-left: 10px; width: 150px"
            >
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
      
      <!-- 错误状态显示 -->
      <el-alert
        v-if="error"
        title="数据加载失败"
        description="系统无法加载持仓历史数据，请稍后重试。"
        type="error"
        :closable="false"
        show-icon
        class="history-error animate-fadeIn"
      >
        <template #default>
          <el-button size="small" type="primary" @click="reloadData">重新加载</el-button>
        </template>
      </el-alert>
      
      <div class="history-content">
        <div class="history-chart">
          <el-card shadow="hover" class="chart-card animate-slideInLeft" style="animation-delay: 0.2s;">
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
            :data="paginatedData"
            style="width: 100%; animation-delay: 0.4s;"
            stripe
            border
            v-loading="loading"
            class="animate-fadeIn"
            height="500px"
            virtual-scroll-y
          >
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column
              prop="coinName"
              label="货币名称"
              width="120"
            ></el-table-column>
            <el-table-column
              prop="coinType"
              label="货币类型"
              width="100"
            ></el-table-column>
            <el-table-column prop="quantity" label="数量" width="120">
              <template #default="scope">
                {{ scope.row.quantity.toFixed(4) }}
              </template>
            </el-table-column>
            <el-table-column prop="price" label="价格" width="150">
              <template #default="scope">
                ${{
                  scope.row.price.toLocaleString('en-US', {
                    minimumFractionDigits: 2,
                  })
                }}
              </template>
            </el-table-column>
            <el-table-column prop="totalValue" label="总价值" width="180">
              <template #default="scope">
                ${{
                  scope.row.totalValue.toLocaleString('en-US', {
                    minimumFractionDigits: 2,
                  })
                }}
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
import { defineComponent, ref, computed, onMounted, onUnmounted } from 'vue'
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
    const error = ref(false)
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
      { label: 'Avalanche', value: 'AVAX' },
    ])
    
    // 分页数据计算属性
    const paginatedData = computed(() => {
      const startIndex = (currentPage.value - 1) * pageSize.value
      const endIndex = startIndex + pageSize.value
      return historyData.value.slice(startIndex, endIndex)
    })
    
    // 获取历史持仓
    const fetchHistory = async () => {
      loading.value = true
      error.value = false
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
          const isVisible =
            chartRef.value.getBoundingClientRect().top <
            window.innerHeight + 100
          if (isVisible) {
            await initChart(data)
          }
        }
      } catch (err) {
        console.error('获取历史持仓失败:', err)
        error.value = true
        historyData.value = []
        total.value = 0
      } finally {
        loading.value = false
      }
    }
    
    const reloadData = () => {
      fetchHistory()
    }
    
    // 防抖函数
    const debounce = (func, delay) => {
      let timer
      return function (...args) {
        clearTimeout(timer)
        timer = setTimeout(() => func.apply(this, args), delay)
      }
    }
    
    // 初始化图表（使用懒加载和异步导入）
    const initChart = async (data) => {
      if (chartRef.value && !chart) {
        // 检查图表是否在视口中
        const isVisible =
          chartRef.value.getBoundingClientRect().top < window.innerHeight + 100

        if (isVisible) {
          // 动态加载echarts
          if (!echarts) {
            echarts = await import('echarts')
          }
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
              data: sortedDates.map(date => timeMap.get(date)[coinType] || 0),
            }
          })

          const option = {
            tooltip: {
              trigger: 'axis',
              backgroundColor: 'rgba(255, 255, 255, 0.95)',
              borderColor: '#e5e7eb',
              borderWidth: 1,
              padding: 12,
              textStyle: {
                color: '#111827'
              },
              shadowBlur: 10,
              shadowColor: 'rgba(0, 0, 0, 0.1)',
              formatter: function(params) {
                let result = params[0].name + '<br/>'
                params.forEach(param => {
                  result += '<div style="display:flex;align-items:center;margin-top:5px;"><span style="display:inline-block;width:10px;height:10px;border-radius:50%;margin-right:8px;background-color:' + param.color + '"></span><span>' + param.seriesName + ': $' + param.value.toLocaleString() + '</span></div>'
                })
                return result
              }
            },
            legend: {
              data: ['BTC', 'ETH', 'SOL', 'AVAX'],
              textStyle: {
                color: '#6b7280'
              },
              itemWidth: 10,
              itemHeight: 10,
              itemGap: 15
            },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true,
            },
            xAxis: {
              type: 'category',
              boundaryGap: false,
              data: sortedDates,
              axisLine: {
                lineStyle: {
                  color: '#e5e7eb'
                }
              },
              axisTick: {
                show: false
              },
              axisLabel: {
                color: '#6b7280',
                fontSize: 12
              }
            },
            yAxis: {
              type: 'value',
              axisLine: {
                show: false
              },
              axisTick: {
                show: false
              },
              axisLabel: {
<<<<<<< HEAD
                formatter: '${value}',
              },
            },
            series: series,
=======
                color: '#6b7280',
                fontSize: 12,
                formatter: '${value.toLocaleString()}'
              },
              splitLine: {
                lineStyle: {
                  color: '#f3f4f6'
                }
              }
            },
            series: series.map(coin => ({
              ...coin,
              symbol: 'circle',
              symbolSize: 8,
              emphasis: {
                symbolSize: 12,
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.3)'
                }
              },
              itemStyle: {
                color: coin.name === 'BTC' ? '#f7931a' : 
                      coin.name === 'ETH' ? '#627eea' :
                      coin.name === 'SOL' ? '#000000' : '#e84142'
              },
              lineStyle: {
                width: 3
              },
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: coin.name === 'BTC' ? 'rgba(247, 147, 26, 0.3)' :
                               coin.name === 'ETH' ? 'rgba(98, 126, 234, 0.3)' :
                               coin.name === 'SOL' ? 'rgba(0, 0, 0, 0.3)' : 'rgba(232, 65, 66, 0.3)' },
                  { offset: 1, color: coin.name === 'BTC' ? 'rgba(247, 147, 26, 0.05)' :
                               coin.name === 'ETH' ? 'rgba(98, 126, 234, 0.05)' :
                               coin.name === 'SOL' ? 'rgba(0, 0, 0, 0.05)' : 'rgba(232, 65, 66, 0.05)' }
                ])
              }
            }))
>>>>>>> 12401935e38ed27721d8e47e87ba3733a09dc087
          }

          chart.setOption(option)
          
          // 添加点击事件处理
          chart.on('click', (params) => {
            console.log('点击了资产变化趋势:', params)
            if (window.$message) {
              window.$message({
                message: `${params.seriesName} - ${params.name}: $${params.value.toLocaleString()}`,
                type: 'success',
                duration: 2000
              })
            }
          })
        }
      }
    }

    // 格式化日期
    const formatDate = dateString => {
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
    const handleCurrentChange = val => {
      currentPage.value = val
      // 实现分页逻辑
    }

    // 处理每页条数变化
    const handleSizeChange = val => {
      pageSize.value = val
      currentPage.value = 1
      // 实现分页逻辑
    }

    // 窗口大小变化处理（防抖）
    const handleResize = debounce(() => {
      chart?.resize()
    }, 100)

    // 滚动事件处理（用于检测图表是否进入视口）
    const handleScroll = debounce(async () => {
      if (!chart && chartRef.value && historyData.value.length > 0) {
        await initChart(historyData.value)
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
      error,
      total,
      currentPage,
      pageSize,
      dateRange,
      selectedCoin,
      coinOptions,
      fetchHistory,
      reloadData,
      formatDate,
      handleDateChange,
      handleCurrentChange,
      handleSizeChange,
    }
  },
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
  flex-wrap: wrap;
  gap: 10px;
}

.history-filters {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.history-content {
  display: flex;
  gap: 20px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.history-chart {
  flex: 1;
  min-width: 300px;
  width: 100%;
}

.chart-card {
  height: 400px;
  transition: all 0.3s ease;
}

.chart-card:hover {
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

#history-chart {
  width: 100%;
  height: 300px;
}

.history-table {
  flex: 2;
  min-width: 500px;
  width: 100%;
  overflow-x: auto;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
<<<<<<< HEAD
</style>
=======

/* 响应式设计 */
@media (max-width: 768px) {
  .history-content {
    flex-direction: column;
  }
  
  .history-table {
    min-width: auto;
  }
  
  .chart-card {
    height: 300px;
  }
  
  #history-chart {
    height: 230px;
  }
  
  .history-filters {
    flex-direction: column;
    align-items: flex-start;
    width: 100%;
  }
  
  .history-filters .el-select {
    width: 100% !important;
    margin-left: 0 !important;
  }
  
  .el-table {
    font-size: 12px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 576px) {
  .chart-card {
    height: 250px;
  }
  
  #history-chart {
    height: 180px;
  }
  
  .pagination-container {
    padding: 0 10px;
  }
  
  .el-pagination {
    font-size: 12px;
  }
}
</style>
>>>>>>> 12401935e38ed27721d8e47e87ba3733a09dc087
