<template>
  <div class="app-container">
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
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import Sidebar from './components/Sidebar.vue'

export default defineComponent({
  name: 'App',
  components: {
    Sidebar
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
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background-color: #f5f7fa;
}

.app-container {
  height: 100vh;
  width: 100%;
}

.sidebar-container {
  background-color: #2c3e50;
  color: white;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.header-container {
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
  display: flex;
  align-items: center;
}

.header-title h1 {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
}

.main-container {
  padding: 20px;
  overflow-y: auto;
}
</style>