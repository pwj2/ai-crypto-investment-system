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
        <el-icon><ViewIcon /></el-icon>
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
import {
  House,
  Goods,
  View as ViewIcon,
  Document,
  ChatDotRound,
  DataAnalysis,
  Calendar,
} from '@element-plus/icons-vue'

export default defineComponent({
  name: 'Sidebar',
  components: {
    House,
    Goods,
    ViewIcon,
    Document,
    ChatDotRound,
    DataAnalysis,
    Calendar,
  },
  setup() {
    const route = useRoute()

    const activePath = computed(() => {
      return route.path
    })

    return {
      activePath,
    }
  },
})
</script>

<style scoped>
.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.sidebar-logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #34495e;
}

.sidebar-logo h2 {
  color: white;
  font-size: 18px;
  margin: 0;
}

.el-menu-vertical-demo {
  border-right: none;
  height: calc(100% - 70px);
}

.el-menu-item {
  margin: 5px 10px;
  border-radius: 4px;
}

.el-menu-item:hover {
  background-color: #34495e !important;
}

.el-menu-item.is-active {
  background-color: #409eff !important;
}
</style>
