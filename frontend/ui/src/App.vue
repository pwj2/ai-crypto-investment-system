<template>
  <div class="app-container">
    <template v-if="$route.path === '/login'">
      <!-- 登录页面布局 -->
      <router-view />
    </template>
    <template v-else>
      <!-- 主应用布局 -->
      <el-container>
        <el-aside width="200px" class="sidebar-container">
          <Sidebar />
        </el-aside>
        <el-container>
          <el-header class="header-container">
            <div class="header-title">
              <h1>AI 加密货币投资系统</h1>
            </div>
          </el-header>
          <el-main class="main-container">
        <transition name="fade" mode="out-in">
          <router-view />
        </transition>
      </el-main>
        </el-container>
      </el-container>
    </template>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import Sidebar from './components/Sidebar.vue'

export default defineComponent({
  name: 'App',
  components: {
    Sidebar,
  },
  mounted() {
    // 测试内存地址日志检测 - 两个地址
    console.log('测试内存地址日志（两个）: [0xc0048e4020 0xc0048e4050]')
    // 测试内存地址日志检测 - 三个地址
    console.log('测试内存地址日志（三个）: [0xc008aa5e70 0xc008aa5ea0 0xc008aa5ed0]')
    // 测试用户提供的具体内存地址日志
    console.log('用户提供的内存地址日志: [0xc007b522d0 0xc007b52300 0xc007b52330]')
  }
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family:
    'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background-color: #f5f7fa;
}

.app-container {
  height: 100vh;
  width: 100%;
  background-color: var(--bg-secondary);
}

.sidebar-container {
  background-color: #2c3e50;
  color: white;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  transition: var(--transition);
}

.header-container {
  background-color: var(--bg-primary);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 0 var(--spacing-xl);
  display: flex;
  align-items: center;
  height: 64px;
  transition: var(--transition);
}

.header-title h1 {
  font-size: 22px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  letter-spacing: 0.5px;
}

.main-container {
  padding: var(--spacing-xl);
  overflow-y: auto;
  background-color: var(--bg-secondary);
  min-height: calc(100vh - 64px);
}

/* 页面切换过渡效果 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>