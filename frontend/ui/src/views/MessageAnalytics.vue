<template>
  <div class="message-analytics-container">
    <el-card shadow="hover" class="animate-fadeIn">
      <template #header>
        <div class="card-header">
          <h2>消息分析报告</h2>
        </div>
      </template>
      
      <div class="analytics-content">
        <!-- 错误状态显示 -->
        <el-alert
          v-if="error"
          title="数据加载失败"
          description="系统无法加载消息分析数据，请稍后重试。"
          type="error"
          :closable="false"
          show-icon
          class="analytics-error animate-fadeIn"
        >
          <template #default>
            <el-button size="small" type="primary" @click="reloadData">重新加载</el-button>
          </template>
        </el-alert>
        
        <div class="analytics-filters">
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
        
        <div class="analytics-stats">
          <!-- 加载骨架屏 -->
          <template v-if="loading">
            <el-card class="stat-card" v-for="i in 4" :key="i">
              <el-skeleton animated>
                <el-skeleton-item variant="text" style="width: 60%; height: 30px; margin-bottom: 10px;"></el-skeleton-item>
                <el-skeleton-item variant="text" style="width: 80%; height: 15px;"></el-skeleton-item>
              </el-skeleton>
            </el-card>
          </template>
          
          <template v-else>
            <el-card class="stat-card animate-fadeIn" style="animation-delay: 0.1s;">
              <div class="stat-content">
                <div class="stat-number">{{ totalMessages }}</div>
                <div class="stat-label">总消息数</div>
              </div>
            </el-card>
            <el-card class="stat-card animate-fadeIn" style="animation-delay: 0.2s;">
              <div class="stat-content">
                <div class="stat-number">{{ positiveMessages }}</div>
                <div class="stat-label">正面消息</div>
              </div>
            </el-card>
            <el-card class="stat-card animate-fadeIn" style="animation-delay: 0.3s;">
              <div class="stat-content">
                <div class="stat-number">{{ neutralMessages }}</div>
                <div class="stat-label">中性消息</div>
              </div>
            </el-card>
            <el-card class="stat-card animate-fadeIn" style="animation-delay: 0.4s;">
              <div class="stat-content">
                <div class="stat-number">{{ negativeMessages }}</div>
                <div class="stat-label">负面消息</div>
              </div>
            </el-card>
          </template>
        </div>
        
        <div class="analytics-charts">
          <el-card class="chart-card animate-slideInLeft" style="animation-delay: 0.5s;">
            <template #header>
              <div class="card-header">
                <span>消息情感分析</span>
              </div>
            </template>
            <!-- 图表加载骨架屏 -->
            <el-skeleton v-if="loading" animated>
              <el-skeleton-item variant="rect" style="width: 100%; height: 300px;"></el-skeleton-item>
            </el-skeleton>
            <div v-else id="sentiment-chart" ref="sentimentChartRef"></div>
          </el-card>
          
          <el-card class="chart-card animate-slideInRight" style="animation-delay: 0.6s;">
            <template #header>
              <div class="card-header">
                <span>消息趋势</span>
              </div>
            </template>
            <!-- 图表加载骨架屏 -->
            <el-skeleton v-if="loading" animated>
              <el-skeleton-item variant="rect" style="width: 100%; height: 300px;"></el-skeleton-item>
            </el-skeleton>
            <div v-else id="trend-chart" ref="trendChartRef"></div>
          </el-card>
        </div>
        
        <div class="analytics-reports">
          <el-card shadow="hover" class="animate-fadeIn" style="animation-delay: 0.7s;">
            <template #header>
              <div class="card-header">
                <span>分析报告列表</span>
              </div>
            </template>
            <div class="tasks-table">
              <!-- 表格加载骨架屏 -->
              <el-skeleton v-if="loading" animated style="margin-bottom: 20px;">
                <el-skeleton-item variant="table" :rows="10" :columns="6"></el-skeleton-item>
              </el-skeleton>
              
              <el-table
                v-else
                :data="reports"
                style="width: 100%"
                stripe
                border
              >
                <el-table-column prop="id" label="报告ID" width="100"></el-table-column>
                <el-table-column prop="reportName" label="报告名称" width="180"></el-table-column>
                <el-table-column prop="sentimentAnalysis" label="情感分析" width="200">
                  <template #default="scope">
                    <el-tag
                      :type="scope.row.sentimentAnalysis === 'positive' ? 'success' : scope.row.sentimentAnalysis === 'neutral' ? 'info' : 'danger'"
                    >
                      {{ scope.row.sentimentAnalysis === 'positive' ? '正面' : scope.row.sentimentAnalysis === 'neutral' ? '中性' : '负面' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="confidence" label="置信度" width="120">
                  <template #default="scope">
                    {{ (scope.row.confidence * 100).toFixed(2) }}%
                  </template>
                </el-table-column>
                <el-table-column prop="createdTime" label="创建时间" width="200">
                  <template #default="scope">
                    {{ formatDate(scope.row.createdTime) }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="150">
                  <template #default="scope">
                    <el-button type="primary" size="small" @click="viewAnalyticsReport(scope.row)">查看</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-card>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, onUnmounted } from 'vue'
import { reportService } from '../services/reportService'
// 使用动态导入echarts，只在需要时加载
let echarts = null

export default defineComponent({
  name: 'MessageAnalytics',
  setup() {
    const reports = ref([])
    const sentimentChartRef = ref(null)
    const trendChartRef = ref(null)
    let sentimentChart = null
    let trendChart = null
    const dateRange = ref([])
    const selectedCoin = ref('')
    const coinOptions = ref([
      { label: '全部币种', value: '' },
      { label: 'Bitcoin', value: 'BTC' },
      { label: 'Ethereum', value: 'ETH' },
      { label: 'Solana', value: 'SOL' },
      { label: 'Avalanche', value: 'AVAX' }
    ])
    const loading = ref(false)
    const error = ref(false)
    
    // 模拟统计数据
    const totalMessages = ref(150)
    const positiveMessages = ref(80)
    const neutralMessages = ref(50)
    const negativeMessages = ref(20)
    
    // 获取消息分析报告
    const fetchAnalyticsReports = async () => {
      loading.value = true
      error.value = false
      try {
        const data = await reportService.getMessageAnalysisReports()
        reports.value = data
        initCharts()
      } catch (err) {
        console.error('获取消息分析报告失败:', err)
        error.value = true
        reports.value = []
      } finally {
        loading.value = false
      }
    }
    
    // 重新加载数据
    const reloadData = () => {
      // 先销毁现有图表实例
      sentimentChart?.dispose()
      trendChart?.dispose()
      sentimentChart = null
      trendChart = null
      // 重新获取数据
      fetchAnalyticsReports()
    }
    
    // 防抖函数
    const debounce = (func, delay) => {
      let timer
      return function(...args) {
        clearTimeout(timer)
        timer = setTimeout(() => func.apply(this, args), delay)
      }
    }
    
    // 检查元素是否在视口中
    const isElementVisible = (element) => {
      if (!element) return false
      const rect = element.getBoundingClientRect()
      return rect.top < window.innerHeight + 100
    }
    
    // 初始化情感分析饼图（懒加载和异步导入）
    const initSentimentChart = async () => {
      if (sentimentChartRef.value && !sentimentChart && isElementVisible(sentimentChartRef.value)) {
        // 动态加载echarts
        if (!echarts) {
          echarts = await import('echarts')
        }
        sentimentChart = echarts.init(sentimentChartRef.value)
        const sentimentOption = {
          tooltip: {
            trigger: 'item',
            formatter: '{b}: {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            right: 10,
            top: 'center'
          },
          series: [
            {
              name: '情感分布',
              type: 'pie',
              radius: ['40%', '70%'],
              avoidLabelOverlap: false,
              itemStyle: {
                borderRadius: 10,
                borderColor: '#fff',
                borderWidth: 2
              },
              label: {
                show: false,
                position: 'center'
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: 20,
                  fontWeight: 'bold'
                }
              },
              labelLine: {
                show: false
              },
              data: [
                { value: positiveMessages.value, name: '正面', itemStyle: { color: '#67C23A' } },
                { value: neutralMessages.value, name: '中性', itemStyle: { color: '#909399' } },
                { value: negativeMessages.value, name: '负面', itemStyle: { color: '#F56C6C' } }
              ]
            }
          ]
        }
        sentimentChart.setOption(sentimentOption)
        
        // 添加点击事件处理
        sentimentChart.on('click', (params) => {
          // 点击事件处理逻辑
          console.log('点击了情感分布:', params)
          // 这里演示一个简单的消息提示
          if (window.$message) {
            window.$message({
              message: `查看${params.name}情感的消息详情`,
              type: 'info',
              duration: 2000
            })
          }
        })
      }
    }
    
    // 初始化消息趋势图（懒加载和异步导入）
    const initTrendChart = async () => {
      if (trendChartRef.value && !trendChart && isElementVisible(trendChartRef.value)) {
        // 动态加载echarts
        if (!echarts) {
          echarts = await import('echarts')
        }
        trendChart = echarts.init(trendChartRef.value)
        const trendOption = {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross',
              crossStyle: {
                color: '#999'
              }
            }
          },
          legend: {
            data: ['正面', '中性', '负面']
          },
          xAxis: [
            {
              type: 'category',
              data: ['1日', '2日', '3日', '4日', '5日', '6日', '7日'],
              axisPointer: {
                type: 'shadow'
              }
            }
          ],
          yAxis: [
            {
              type: 'value',
              name: '消息数量',
              min: 0,
              max: 30,
              interval: 5
            }
          ],
          series: [
            {
              name: '正面',
              type: 'bar',
              data: [12, 15, 18, 14, 16, 19, 20],
              itemStyle: { color: '#67C23A' }
            },
            {
              name: '中性',
              type: 'bar',
              data: [8, 10, 7, 9, 11, 8, 10],
              itemStyle: { color: '#909399' }
            },
            {
              name: '负面',
              type: 'bar',
              data: [5, 4, 6, 5, 4, 6, 5],
              itemStyle: { color: '#F56C6C' }
            }
          ]
        }
        trendChart.setOption(trendOption)
        
        // 添加点击事件处理
        trendChart.on('click', (params) => {
          // 点击事件处理逻辑
          console.log('点击了消息趋势:', params)
          // 这里演示一个简单的消息提示
          if (window.$message) {
            window.$message({
              message: `${params.name} - ${params.dataIndex + 1}日: ${params.value}条消息`,
              type: 'success',
              duration: 2000
            })
          }
        })
      }
    }
    
    // 初始化图表（懒加载）
    const initCharts = () => {
      initSentimentChart()
      initTrendChart()
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleString()
    }
    
    // 处理日期范围变化
    const handleDateChange = () => {
      console.log('日期范围变化:', dateRange.value)
    }
    
    // 查看分析报告
    const viewAnalyticsReport = (report) => {
      console.log('查看报告:', report)
      ElMessage.info('查看报告功能待实现')
    }
    
    // 窗口大小变化处理（防抖）
    const handleResize = debounce(() => {
      sentimentChart?.resize()
      trendChart?.resize()
    }, 100)
    
    // 滚动事件处理（用于检测图表是否进入视口）
    const handleScroll = debounce(() => {
      initSentimentChart()
      initTrendChart()
      
      // 如果两个图表都已初始化，停止监听
      if (sentimentChart && trendChart) {
        window.removeEventListener('scroll', handleScroll)
      }
    }, 100)
    
    onMounted(() => {
      fetchAnalyticsReports()
      
      // 初始检查图表是否可见
      initCharts()
      
      // 监听窗口大小变化
      window.addEventListener('resize', handleResize)
      
      // 监听滚动事件用于懒加载
      window.addEventListener('scroll', handleScroll)
      
      // 保存事件处理函数以便清理
      window.__analyticsResizeHandler = handleResize
      window.__analyticsScrollHandler = handleScroll
    })
    
    onUnmounted(() => {
      // 清理资源
      window.removeEventListener('resize', window.__analyticsResizeHandler)
      window.removeEventListener('scroll', window.__analyticsScrollHandler)
      
      // 销毁图表实例
      sentimentChart?.dispose()
      trendChart?.dispose()
      
      sentimentChart = null
      trendChart = null
    })
    
    return {
      reports,
      sentimentChartRef,
      trendChartRef,
      dateRange,
      selectedCoin,
      coinOptions,
      totalMessages,
      positiveMessages,
      neutralMessages,
      negativeMessages,
      loading,
      error,
      fetchAnalyticsReports,
      reloadData,
      formatDate,
      handleDateChange,
      viewAnalyticsReport
    }
  }
})
</script>

<style scoped>
.message-analytics-container {
  padding: 0;
  background-color: var(--bg-color);
  min-height: 100vh;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--primary-color);
  margin: 0;
}

.analytics-content {
  margin-top: 20px;
}

.analytics-filters {
  margin-bottom: 25px;
  display: flex;
  align-items: center;
  gap: 15px;
  background-color: white;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.analytics-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  text-align: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(224, 224, 224, 0.5);
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
}

.stat-card:hover {
  transform: translateY(-5px) scale(1.02);
  box-shadow: 0 12px 25px rgba(0, 0, 0, 0.15);
  border-color: var(--primary-color);
}

.stat-content {
  padding: 25px 20px;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.stat-card:hover .stat-number {
  color: var(--primary-color);
}

.stat-label {
  font-size: 14px;
  color: #666;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 500;
}

.analytics-charts {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
  gap: 25px;
  margin-bottom: 30px;
}

.chart-card {
  height: 380px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(224, 224, 224, 0.5);
}

.chart-card:hover {
  box-shadow: 0 12px 25px rgba(0, 0, 0, 0.15);
  transform: translateY(-3px);
}

#sentiment-chart, #trend-chart {
  width: 100%;
  height: 300px;
  transition: height 0.3s ease;
}

.analytics-reports {
  margin-top: 25px;
}

.tasks-table {
  background-color: white;
  border-radius: 12px;
}

.tasks-table .el-table {
  border-radius: 12px;
  overflow: hidden;
}

.tasks-table .el-table__header-wrapper th {
  background-color: var(--primary-color);
  color: white;
  font-weight: 600;
}

.tasks-table .el-table__body-wrapper tr:hover {
  background-color: rgba(64, 158, 255, 0.05);
}

.tasks-table .el-button {
  border-radius: 6px;
  transition: all 0.3s ease;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .analytics-stats {
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
    gap: 18px;
  }
  
  .analytics-charts {
    grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .analytics-filters {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
    padding: 12px;
  }
  
  .analytics-filters .el-date-picker,
  .analytics-filters .el-select {
    width: 100% !important;
    margin-left: 0 !important;
  }
  
  .analytics-stats {
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
    gap: 15px;
  }
  
  .stat-content {
    padding: 20px 15px;
  }
  
  .stat-number {
    font-size: 24px;
  }
  
  .stat-label {
    font-size: 13px;
  }
  
  .analytics-charts {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .chart-card {
    height: 330px;
  }
  
  #sentiment-chart, #trend-chart {
    height: 250px;
  }
  
  .tasks-table {
    overflow-x: auto;
    font-size: 12px;
  }
  
  .tasks-table .el-table {
    font-size: 12px;
  }
  
  .tasks-table .el-button {
    padding: 4px 10px;
    font-size: 12px;
  }
  
  .tasks-table .el-table__header-wrapper th,
  .tasks-table .el-table__body-wrapper td {
    padding: 8px 4px;
  }
}

@media (max-width: 576px) {
  .card-header h2 {
    font-size: 1.25rem;
  }
  
  .analytics-stats {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .stat-content {
    padding: 18px 12px;
  }
  
  .stat-number {
    font-size: 20px;
  }
  
  .stat-label {
    font-size: 12px;
  }
  
  .chart-card {
    height: 280px;
  }
  
  #sentiment-chart, #trend-chart {
    height: 200px;
  }
  
  .tasks-table {
    font-size: 11px;
  }
  
  .tasks-table .el-table {
    font-size: 11px;
  }
  
  .tasks-table .el-button {
    padding: 3px 8px;
    font-size: 11px;
  }
  
  .tasks-table .el-table__header-wrapper th,
  .tasks-table .el-table__body-wrapper td {
    padding: 6px 3px;
  }
}

@media (max-width: 375px) {
  .analytics-stats {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  
  .stat-content {
    padding: 15px 10px;
  }
  
  .stat-number {
    font-size: 18px;
  }
  
  .stat-label {
    font-size: 11px;
  }
  
  .chart-card {
    height: 250px;
  }
  
  #sentiment-chart, #trend-chart {
    height: 170px;
  }
}
</style>