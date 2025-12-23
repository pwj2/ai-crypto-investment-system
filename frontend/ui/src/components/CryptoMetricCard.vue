<template>
  <div class="crypto-metric-card" :class="{'card-positive': isPositive, 'card-negative': isNegative}">
    <div class="card-header">
      <div class="metric-icon">
        <el-icon :size="24"><component :is="icon" /></el-icon>
      </div>
      <div class="metric-title">
        <h3>{{ title }}</h3>
        <span class="metric-subtitle">{{ subtitle }}</span>
      </div>
      <div class="metric-trend" v-if="trend !== null">
        <el-icon :size="16" class="trend-icon">
          <ArrowUp v-if="isPositive" />
          <ArrowDown v-else />
        </el-icon>
        <span class="trend-value">{{ formatTrend(trend) }}</span>
      </div>
    </div>
    <div class="card-body">
      <div class="metric-value">
        {{ formatValue(value, isCurrency) }}
      </div>
      <div class="metric-change" v-if="change !== null">
        <span :class="{'change-positive': change > 0, 'change-negative': change < 0}">
          {{ change > 0 ? '+' : '' }}{{ change }}%
        </span>
      </div>
    </div>
    <div class="card-footer">
      <el-progress
        :percentage="progress"
        :stroke-width="3"
        :color="progressColor"
        :show-text="false"
      />
      <span class="progress-label">{{ progressLabel }}</span>
    </div>
  </div>
</template>

<script>
import { defineComponent, computed } from 'vue'
import { ArrowUp, ArrowDown, TrendingUp, TrendingDown, Dollar, BarChart, Wallet, Activity } from '@element-plus/icons-vue'

export default defineComponent({
  name: 'CryptoMetricCard',
  components: {
    ArrowUp,
    ArrowDown,
    TrendingUp,
    TrendingDown,
    Dollar,
    BarChart,
    Wallet,
    Activity
  },
  props: {
    title: {
      type: String,
      required: true
    },
    subtitle: {
      type: String,
      default: ''
    },
    value: {
      type: [Number, String],
      required: true
    },
    change: {
      type: Number,
      default: null
    },
    trend: {
      type: Number,
      default: null
    },
    progress: {
      type: Number,
      default: 0
    },
    progressLabel: {
      type: String,
      default: ''
    },
    icon: {
      type: String,
      default: 'Activity'
    },
    isCurrency: {
      type: Boolean,
      default: false
    },
    color: {
      type: String,
      default: null
    }
  },
  setup(props) {
    const isPositive = computed(() => {
      return props.change !== null ? props.change > 0 : props.trend !== null ? props.trend > 0 : false
    })

    const isNegative = computed(() => {
      return props.change !== null ? props.change < 0 : props.trend !== null ? props.trend < 0 : false
    })

    const progressColor = computed(() => {
      if (props.color) return props.color
      if (isPositive.value) return 'var(--success-color)'
      if (isNegative.value) return 'var(--danger-color)'
      return 'var(--primary-color)'
    })

    const formatValue = (value, isCurrency) => {
      if (typeof value === 'number') {
        if (isCurrency) {
          return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
          }).format(value)
        } else {
          return new Intl.NumberFormat('en-US', {
            minimumFractionDigits: 0,
            maximumFractionDigits: 2
          }).format(value)
        }
      }
      return value
    }

    const formatTrend = (trend) => {
      if (trend !== null) {
        return `${trend > 0 ? '+' : ''}${trend}%`
      }
      return ''
    }

    return {
      isPositive,
      isNegative,
      progressColor,
      formatValue,
      formatTrend
    }
  }
})
</script>

<style scoped>
.crypto-metric-card {
  background: var(--card-background);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
  transition: var(--transition);
  border: 1px solid var(--border-light);
  position: relative;
  overflow: hidden;
  animation: fadeIn 0.6s ease-out forwards;
}

.crypto-metric-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), var(--accent-color));
}

.crypto-metric-card:hover {
  box-shadow: var(--shadow-xl);
  transform: translateY(-4px);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-md);
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.metric-icon {
  background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
  border-radius: var(--border-radius-full);
  padding: var(--spacing-sm);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.metric-title h3 {
  margin: 0;
  font-size: 16px;
  color: var(--text-primary);
  font-weight: 600;
}

.metric-subtitle {
  font-size: 12px;
  color: var(--text-tertiary);
  font-weight: 400;
}

.metric-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
}

.trend-icon {
  margin-right: 2px;
}

.trend-value {
  color: var(--text-secondary);
}

.card-body {
  margin-bottom: var(--spacing-md);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.metric-value {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.2;
  letter-spacing: -0.5px;
}

.metric-change {
  font-size: 14px;
  font-weight: 500;
}

.change-positive {
  color: var(--success-color);
}

.change-negative {
  color: var(--danger-color);
}

.card-footer {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.progress-label {
  font-size: 12px;
  color: var(--text-tertiary);
  text-align: right;
  margin-top: var(--spacing-xs);
}

/* 状态颜色 */
.card-positive::before {
  background: linear-gradient(90deg, var(--success-color), var(--primary-color));
}

.card-negative::before {
  background: linear-gradient(90deg, var(--danger-color), var(--accent-color));
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .crypto-metric-card {
    padding: var(--spacing-md);
  }
  
  .metric-value {
    font-size: 24px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .metric-trend {
    align-self: flex-end;
  }
}
</style>