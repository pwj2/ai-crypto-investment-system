<template>
  <div class="dashboard-container">
    <h2 class="animate-fadeIn">系统概览</h2>
    
    <!-- 错误状态显示 -->
    <el-alert
      v-if="error"
      title="数据加载失败"
      description="系统无法加载数据，请稍后重试。"
      type="error"
      :closable="false"
      show-icon
      class="dashboard-error animate-fadeIn"
    >
      <template #default>
        <el-button size="small" type="primary" @click="reloadData">重新加载</el-button>
      </template>
    </el-alert>
    
    <!-- 加载状态显示 -->
    <div v-if="loading" class="dashboard-loading">
      <el-skeleton :rows="6" animated>
        <template #template>
          <el-skeleton-item variant="text" style="width: 30%; margin-bottom: 20px;"></el-skeleton-item>
          <div class="skeleton-stats">
            <el-skeleton-item variant="card" style="width: 23%; height: 120px; margin-right: 2%;"></el-skeleton-item>
            <el-skeleton-item variant="card" style="width: 23%; height: 120px; margin-right: 2%;"></el-skeleton-item>
            <el-skeleton-item variant="card" style="width: 23%; height: 120px; margin-right: 2%;"></el-skeleton-item>
            <el-skeleton-item variant="card" style="width: 23%; height: 120px;"></el-skeleton-item>
          </div>
          <div class="skeleton-charts">
            <el-skeleton-item variant="card" style="width: 48%; height: 350px; margin-right: 2%;"></el-skeleton-item>
            <el-skeleton-item variant="card" style="width: 48%; height: 350px;"></el-skeleton-item>
          </div>
        </template>
      </el-skeleton>
    </div>
    
    <!-- 数据内容显示 -->
    <div v-else class="dashboard-content">
      <div class="dashboard-stats">
        <el-card class="stat-card animate-fadeIn" style="animation-delay: 0.1s;">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Goods /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ totalHoldings }}</div>
              <div class="stat-label">当前持仓总资产</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card animate-fadeIn" style="animation-delay: 0.2s;">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ totalCoins }}</div>
              <div class="stat-label">持有币种数</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card animate-fadeIn" style="animation-delay: 0.3s;">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ pendingReports }}</div>
              <div class="stat-label">待审核报告</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card animate-fadeIn" style="animation-delay: 0.4s;">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ totalMessages }}</div>
              <div class="stat-label">分析消息数</div>
            </div>
          </div>
        </el-card>
      </div>
      
      <div class="dashboard-charts">
        <el-card class="chart-card animate-slideInLeft" style="animation-delay: 0.5s;">
          <template #header>
            <div class="card-header">
              <span>持仓分布</span>
            </div>
          </template>
          <div id="holdings-chart" class="chart-container"></div>
        </el-card>
        
        <el-card class="chart-card animate-slideInRight" style="animation-delay: 0.6s;">
          <template #header>
            <div class="card-header">
              <span>资产变化趋势</span>
            </div>
          </template>
          <div id="trend-chart" class="chart-container"></div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Goods, TrendCharts, Document, ChatDotRound } from '@element-plus/icons-vue'

// 统计数据
const totalHoldings = ref('$0.00')
const totalCoins = ref(0)
const pendingReports = ref(0)
const totalMessages = ref(0)
const loading = ref(true)
const error = ref(false)

let holdingsChart = null
let trendChart = null
let resizeObserver = null
let echarts = null

// 防抖函数
const debounce = (func, delay) => {
  let timer
  return function(...args) {
    clearTimeout(timer)
    timer = setTimeout(() => func.apply(this, args), delay)
  }
}

// 持仓分布饼图
const initHoldingsChart = async () => {
  const chartDom = document.getElementById('holdings-chart')
  if (chartDom && !holdingsChart) {
    // 动态加载echarts
    if (!echarts) {
      echarts = await import('echarts')
    }
    holdingsChart = echarts.init(chartDom)
    const holdingsOption = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: ${c.toLocaleString()} ({d}%)',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#e5e7eb',
        borderWidth: 1,
        padding: 12,
        textStyle: {
          color: '#111827'
        },
        shadowBlur: 10,
        shadowColor: 'rgba(0, 0, 0, 0.1)'
      },
      legend: {
        orient: 'vertical',
        right: 10,
        top: 'center',
        textStyle: {
          color: '#6b7280',
          fontSize: 12
        },
        itemWidth: 10,
        itemHeight: 10,
        itemGap: 15
      },
      series: [
        {
          name: '持仓分布',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['35%', '50%'],
          avoidLabelOverlap: false,
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            scale: true,
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.3)'
            },
            label: {
              show: true,
              fontSize: '20',
              fontWeight: 'bold',
              color: '#111827'
            }
          },
          labelLine: {
            show: false
          },
          data: [
            { value: 67500, name: 'Bitcoin', itemStyle: { color: '#3b82f6' } },
            { value: 45000, name: 'Ethereum', itemStyle: { color: '#8b5cf6' } },
            { value: 15000, name: 'Solana', itemStyle: { color: '#10b981' } },
            { value: 12500, name: 'Avalanche', itemStyle: { color: '#f59e0b' } }
          ],
          animationType: 'scale',
          animationEasing: 'elasticOut',
          animationDelay: function(idx) {
            return Math.random() * 200;
          }
        }
      ]
    }
    holdingsChart.setOption(holdingsOption)
    
    // 添加点击事件处理
    holdingsChart.on('click', (params) => {
      console.log('点击了持仓分布:', params)
    })
  }
}

// 资产变化趋势图
const initTrendChart = async () => {
  const chartDom = document.getElementById('trend-chart')
  if (chartDom && !trendChart) {
    // 动态加载echarts
    if (!echarts) {
      echarts = await import('echarts')
    }
    trendChart = echarts.init(chartDom)
    const trendOption = {
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
          result += '<div style="display:flex;align-items:center;margin-top:5px;"><span style="display:inline-block;width:10px;height:10px;background-color:#3b82f6;border-radius:50%;margin-right:8px;"></span><span>总资产: $' + params[0].value.toLocaleString() + '</span></div>'
          return result
        }
      },
      xAxis: {
        type: 'category',
        data: ['1月', '2月', '3月', '4月', '5月', '6月'],
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
        },
        boundaryGap: false
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
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      series: [
        {
          name: '总资产',
          data: [120000, 140000, 135000, 160000, 155000, 180000],
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          emphasis: {
            symbolSize: 12,
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(59, 130, 246, 0.5)'
            }
          },
          itemStyle: {
            color: '#3b82f6'
          },
          lineStyle: {
            width: 3,
            color: '#3b82f6'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
              { offset: 1, color: 'rgba(59, 130, 246, 0.05)' }
            ])
          }
        }
      ]
    }
    trendChart.setOption(trendOption)
    
    // 添加点击事件处理
    trendChart.on('click', (params) => {
      console.log('点击了资产变化趋势:', params)
    })
  }
}

// 图表懒加载
const initChartsLazy = () => {
  // 检查图表是否在视口中
  const checkChartsVisibility = async () => {
    const holdingsChartDom = document.getElementById('holdings-chart')
    const trendChartDom = document.getElementById('trend-chart')
    
    if (holdingsChartDom && holdingsChartDom.getBoundingClientRect().top < window.innerHeight + 100) {
      await initHoldingsChart()
    }
    
    if (trendChartDom && trendChartDom.getBoundingClientRect().top < window.innerHeight + 100) {
      await initTrendChart()
    }
    
    // 如果两个图表都已初始化，停止监听
    if (holdingsChart && trendChart) {
      window.removeEventListener('scroll', debouncedCheckVisibility)
    }
  }
  
  const debouncedCheckVisibility = debounce(checkChartsVisibility, 100)
  
  // 初始化时检查一次
  checkChartsVisibility()
  
  // 监听滚动事件
  window.addEventListener('scroll', debouncedCheckVisibility)
  
  return debouncedCheckVisibility
}

// 窗口大小变化处理（防抖）
const handleResize = debounce(() => {
  holdingsChart?.resize()
  trendChart?.resize()
}, 100)

const updateStats = async () => {
  loading.value = true
  error.value = false
  
  try {
    // 模拟网络请求延迟
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟数据获取成功，实际项目中从API获取
    totalHoldings.value = '$140,000.00'
    totalCoins.value = 4
    pendingReports.value = 2
    totalMessages.value = 150
    
    // 清理已有的图表实例（如果有）
    if (holdingsChart) {
      holdingsChart.dispose()
      holdingsChart = null
    }
    if (trendChart) {
      trendChart.dispose()
      trendChart = null
    }
    
    // 重新初始化图表
    await initChartsLazy()
    
  } catch (err) {
    console.error('数据加载失败:', err)
    error.value = true
  } finally {
    loading.value = false
  }
}

const reloadData = () => {
  updateStats()
}

onMounted(() => {
  updateStats()
  
  // 懒加载图表
  const debouncedCheckVisibility = initChartsLazy()
  
  // 响应窗口大小变化
  window.addEventListener('resize', handleResize)
  
  // 保存事件处理函数以便清理
  window.__dashboardResizeHandler = handleResize
  window.__dashboardScrollHandler = debouncedCheckVisibility
})

onUnmounted(() => {
  // 清理资源
  window.removeEventListener('resize', window.__dashboardResizeHandler)
  window.removeEventListener('scroll', window.__dashboardScrollHandler)
  
  // 销毁图表实例
  holdingsChart?.dispose()
  trendChart?.dispose()
  
  holdingsChart = null
  trendChart = null
})
</script>

<style scoped>
.dashboard-container {
  padding: var(--spacing-lg) 0;
}

h2 {
  margin-bottom: var(--spacing-lg);
  color: var(--text-primary);
}

.dashboard-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.stat-card {
  height: 100%;
  transition: var(--transition);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-light);
}

.stat-card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: var(--spacing-md);
}

.stat-icon {
  font-size: 36px;
  color: var(--primary-color);
  margin-right: var(--spacing-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  border-radius: var(--border-radius-md);
  background-color: rgba(59, 130, 246, 0.1);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: var(--text-primary);
  margin-bottom: var(--spacing-xs);
}

.stat-label {
  font-size: 14px;
  color: var(--text-tertiary);
}

.dashboard-charts {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: var(--spacing-lg);
}

.chart-card {
  height: 100%;
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-light);
  transition: var(--transition);
}

.chart-card:hover {
  box-shadow: var(--shadow-md);
}

.chart-container {
  height: 320px;
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: var(--text-primary);
}

/* 错误提示样式 */
.dashboard-error {
  margin-bottom: var(--spacing-lg);
}

/* 加载状态样式 */
.dashboard-loading {
  padding: var(--spacing-lg) 0;
}

.skeleton-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--spacing-lg);
}
</style>