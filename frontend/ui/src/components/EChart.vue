<template>
  <div ref="chartRef" class="echart-container" :style="chartStyle"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed, nextTick } from 'vue'

// 组件属性
const props = defineProps({
  // 图表配置项
  option: {
    type: Object,
    required: true,
  },
  // 图表高度
  height: {
    type: [String, Number],
    default: '400px',
  },
  // 是否启用懒加载（滚动到视图中才初始化）
  lazy: {
    type: Boolean,
    default: false,
  },
  // 懒加载的偏移量（px）
  lazyOffset: {
    type: Number,
    default: 100,
  },
  // 是否响应式
  responsive: {
    type: Boolean,
    default: true,
  },
})

// 组件事件
const emit = defineEmits(['init', 'click', 'dblclick', 'hover'])

// 图表引用
const chartRef = ref(null)
let chartInstance = null
let echarts = null
let resizeObserver = null
let intersectionObserver = null

// 计算图表样式
const chartStyle = computed(() => {
  return {
    height:
      typeof props.height === 'number' ? `${props.height}px` : props.height,
    width: '100%',
  }
})

// 初始化图表
const initChart = async () => {
  if (!chartRef.value || chartInstance) return

  // 动态加载echarts
  if (!echarts) {
    echarts = (await import('echarts')).default
  }

  // 创建图表实例
  chartInstance = echarts.init(chartRef.value)

  // 设置图表配置
  chartInstance.setOption(props.option)

  // 绑定事件
  chartInstance.on('click', params => {
    emit('click', params)
  })

  chartInstance.on('dblclick', params => {
    emit('dblclick', params)
  })

  chartInstance.on('mouseover', params => {
    emit('hover', params)
  })

  // 响应窗口大小变化
  if (props.responsive) {
    window.addEventListener('resize', handleResize)

    // 使用ResizeObserver监听容器大小变化
    if (window.ResizeObserver) {
      resizeObserver = new ResizeObserver(handleResize)
      resizeObserver.observe(chartRef.value)
    }
  }

  // 发送初始化事件
  emit('init', chartInstance)
}

// 处理窗口大小变化
const handleResize = () => {
  chartInstance?.resize()
}

// 检查元素是否在视口中
const isElementInViewport = element => {
  const rect = element.getBoundingClientRect()
  return (
    rect.top <
      (window.innerHeight || document.documentElement.clientHeight) +
        props.lazyOffset &&
    rect.left <
      (window.innerWidth || document.documentElement.clientWidth) +
        props.lazyOffset &&
    rect.bottom > -props.lazyOffset &&
    rect.right > -props.lazyOffset
  )
}

// 初始化懒加载
const initLazyLoad = () => {
  if (isElementInViewport(chartRef.value)) {
    initChart()
    return
  }

  // 创建IntersectionObserver
  if (window.IntersectionObserver) {
    intersectionObserver = new IntersectionObserver(
      entries => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            initChart()
            intersectionObserver?.disconnect()
          }
        })
      },
      { rootMargin: `${props.lazyOffset}px` }
    )
    intersectionObserver.observe(chartRef.value)
  } else {
    // 降级方案：使用滚动事件
    const checkVisibility = () => {
      if (isElementInViewport(chartRef.value)) {
        initChart()
        window.removeEventListener('scroll', checkVisibility)
      }
    }
    window.addEventListener('scroll', checkVisibility)
  }
}

// 更新图表配置
watch(
  () => props.option,
  newOption => {
    chartInstance?.setOption(newOption, true)
  },
  { deep: true }
)

// 生命周期钩子
onMounted(() => {
  nextTick(() => {
    if (props.lazy) {
      initLazyLoad()
    } else {
      initChart()
    }
  })
})

onUnmounted(() => {
  // 清理资源
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }

  if (resizeObserver) {
    resizeObserver.disconnect()
    resizeObserver = null
  }

  if (intersectionObserver) {
    intersectionObserver.disconnect()
    intersectionObserver = null
  }

  window.removeEventListener('resize', handleResize)
})

// 暴露方法给父组件
defineExpose({
  chartInstance,
  resize: () => chartInstance?.resize(),
  setOption: option => chartInstance?.setOption(option),
  getOption: () => chartInstance?.getOption(),
})
</script>

<style scoped>
.echart-container {
  width: 100%;
  min-height: 300px;
  transition: height 0.3s ease;
}
</style>
