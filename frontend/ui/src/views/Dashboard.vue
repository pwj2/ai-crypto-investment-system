<template>
  <div class="dashboard-container">
    <h2>系统概览</h2>
    <div class="dashboard-stats">
      <el-card class="stat-card">
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
      <el-card class="stat-card">
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
      <el-card class="stat-card">
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
      <el-card class="stat-card">
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
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>持仓分布</span>
          </div>
        </template>
        <EChart
          :option="holdingsChartOption"
          height="400px"
          lazy
          @init="onHoldingsChartInit"
          @click="onChartClick"
        />
      </el-card>

      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>资产变化趋势</span>
          </div>
        </template>
        <EChart
          :option="trendChartOption"
          height="400px"
          lazy
          @init="onTrendChartInit"
          @click="onChartClick"
        />
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  Goods,
  TrendCharts,
  Document,
  ChatDotRound,
} from '@element-plus/icons-vue'
import EChart from '../components/EChart.vue'

// 统计数据
const totalHoldings = ref('$0.00')
const totalCoins = ref(0)
const pendingReports = ref(0)
const totalMessages = ref(0)

// 图表实例引用
const holdingsChartRef = ref(null)
const trendChartRef = ref(null)

// 持仓分布图表配置
const holdingsChartOption = ref({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c} ({d}%)',
  },
  legend: {
    orient: 'vertical',
    right: 10,
    top: 'center',
  },
  series: [
    {
      name: '持仓分布',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      label: {
        show: false,
        position: 'center',
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '18',
          fontWeight: 'bold',
        },
      },
      labelLine: {
        show: false,
      },
      data: [
        { value: 67500, name: 'Bitcoin' },
        { value: 45000, name: 'Ethereum' },
        { value: 15000, name: 'Solana' },
        { value: 12500, name: 'Avalanche' },
      ],
    },
  ],
})

// 资产变化趋势图表配置
const trendChartOption = ref({
  tooltip: {
    trigger: 'axis',
  },
  xAxis: {
    type: 'category',
    data: ['1月', '2月', '3月', '4月', '5月', '6月'],
  },
  yAxis: {
    type: 'value',
    axisLabel: {
      formatter: '${value}',
    },
  },
  series: [
    {
      name: '总资产',
      data: [120000, 140000, 135000, 160000, 155000, 180000],
      type: 'line',
      smooth: true,
      itemStyle: {
        color: '#409EFF',
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' },
          ],
        },
      },
    },
  ],
})

// 图表初始化回调
const onHoldingsChartInit = chartInstance => {
  holdingsChartRef.value = chartInstance
}

const onTrendChartInit = chartInstance => {
  trendChartRef.value = chartInstance
}

// 图表点击事件
const onChartClick = params => {
  console.log('Chart clicked:', params)
  // 可以在这里添加图表点击后的交互逻辑
}

// 更新统计数据
const updateStats = () => {
  // 模拟数据，实际项目中从API获取
  totalHoldings.value = '$140,000.00'
  totalCoins.value = 4
  pendingReports.value = 2
  totalMessages.value = 150
}

onMounted(() => {
  updateStats()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px 0;
}

h2 {
  margin-bottom: 20px;
  color: #2c3e50;
}

.dashboard-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  height: 100%;
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  font-size: 36px;
  color: #409eff;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
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

.dashboard-charts {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.chart-card {
  height: 100%;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
