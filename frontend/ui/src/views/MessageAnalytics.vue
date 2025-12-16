<template>
  <div class="message-analytics-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>消息分析报告</h2>
        </div>
      </template>
      
      <div class="analytics-content">
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
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ totalMessages }}</div>
              <div class="stat-label">总消息数</div>
            </div>
          </el-card>
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ positiveMessages }}</div>
              <div class="stat-label">正面消息</div>
            </div>
          </el-card>
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ neutralMessages }}</div>
              <div class="stat-label">中性消息</div>
            </div>
          </el-card>
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ negativeMessages }}</div>
              <div class="stat-label">负面消息</div>
            </div>
          </el-card>
        </div>
        
        <div class="analytics-charts">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>消息情感分析</span>
              </div>
            </template>
            <div id="sentiment-chart" ref="sentimentChartRef"></div>
          </el-card>
          
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>消息趋势</span>
              </div>
            </template>
            <div id="trend-chart" ref="trendChartRef"></div>
          </el-card>
        </div>
        
        <div class="analytics-reports">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>分析报告列表</span>
              </div>
            </template>
            <el-table
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
          </el-card>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { reportService } from '../services/reportService'

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
    
    // 模拟统计数据
    const totalMessages = ref(150)
    const positiveMessages = ref(80)
    const neutralMessages = ref(50)
    const negativeMessages = ref(20)
    
    // 获取消息分析报告
    const fetchAnalyticsReports = async () => {
      try {
        const data = await reportService.getMessageAnalysisReports()
        reports.value = data
        initCharts()
      } catch (error) {
        console.error('获取消息分析报告失败:', error)
      }
    }
    
    // 初始化图表
    const initCharts = () => {
      // 情感分析饼图
      if (sentimentChartRef.value) {
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
      }
      
      // 消息趋势图
      if (trendChartRef.value) {
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
      }
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
    
    onMounted(() => {
      fetchAnalyticsReports()
      window.addEventListener('resize', () => {
        sentimentChart?.resize()
        trendChart?.resize()
      })
    })
    
    onUnmounted(() => {
      window.removeEventListener('resize', () => {
        sentimentChart?.resize()
        trendChart?.resize()
      })
      sentimentChart?.dispose()
      trendChart?.dispose()
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
      fetchAnalyticsReports,
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
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.analytics-content {
  margin-top: 20px;
}

.analytics-filters {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.analytics-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  text-align: center;
}

.stat-content {
  padding: 20px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.analytics-charts {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.chart-card {
  height: 350px;
}

#sentiment-chart, #trend-chart {
  width: 100%;
  height: 280px;
}

.analytics-reports {
  margin-top: 20px;
}
</style>