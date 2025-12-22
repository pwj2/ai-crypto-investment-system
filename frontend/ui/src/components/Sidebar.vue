<template>
  <div class="sidebar">
    <div class="sidebar-logo">
      <h2>AI Crypto</h2>
    </div>
    <el-menu
      :default-active="activePath"
      class="el-menu-vertical-demo"
      background-color="#2c3e50"
      text-color="white"
      active-text-color="#409EFF"
      router
    >
      <el-menu-item index="/dashboard">
        <el-icon><House /></el-icon>
        <span>仪表盘</span>
      </el-menu-item>
      <el-menu-item index="/holdings">
        <el-icon><Goods /></el-icon>
        <span>当前持仓</span>
      </el-menu-item>
      <el-menu-item index="/history">
        <el-icon><View /></el-icon>
        <span>持仓历史</span>
      </el-menu-item>
      <el-menu-item index="/suggest-reports">
        <el-icon><Document /></el-icon>
        <span>建议报告</span>
      </el-menu-item>
      <el-menu-item index="/message-analytics">
        <el-icon><ChatDotRound /></el-icon>
        <span>消息分析</span>
      </el-menu-item>
      <el-menu-item index="/audits">
        <el-icon><DataAnalysis /></el-icon>
        <span>审计记录</span>
      </el-menu-item>
      <el-menu-item index="/tasks">
        <el-icon><Calendar /></el-icon>
        <span>系统任务</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script>
import { defineComponent, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { House, Goods, View, Document, ChatDotRound, DataAnalysis, Calendar } from '@element-plus/icons-vue'

export default defineComponent({
  name: 'Sidebar',
  components: {
    House,
    Goods,
    View,
    Document,
    ChatDotRound,
    DataAnalysis,
    Calendar
  },
  setup() {
    const route = useRoute()
    
    const activePath = computed(() => {
      return route.path
    })
    
    return {
      activePath
    }
  }
})
</script>

<style scoped>
.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #2c3e50;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  transition: var(--transition);
}

.sidebar-logo {
  padding: var(--spacing-lg);
  text-align: center;
  border-bottom: 1px solid #34495e;
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
}

.sidebar-logo h2 {
  color: white;
  font-size: 20px;
  margin: 0;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.el-menu-vertical-demo {
  border-right: none;
  height: calc(100% - 70px);
  background-color: transparent;
}

.el-menu-item {
  margin: 5px 10px;
  border-radius: var(--border-radius-md);
  transition: var(--transition);
  border: 1px solid transparent;
}

.el-menu-item:hover {
  background-color: #34495e !important;
  transform: translateX(3px);
}

.el-menu-item.is-active {
  background-color: var(--primary-color) !important;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
  border-color: var(--primary-light);
}

.el-menu-item .el-icon {
  margin-right: 10px;
  font-size: 16px;
}
</style>