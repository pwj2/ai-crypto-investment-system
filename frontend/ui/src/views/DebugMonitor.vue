<template>
  <div class="debug-monitor">
    <h2>调试监控</h2>
    
    <el-card class="monitor-card">
      <template #header>
        <div class="card-header">
          <span>网络请求监控</span>
        </div>
      </template>
      <div class="monitor-content">
        <el-table :data="networkRequests" style="width: 100%">
          <el-table-column prop="method" label="方法" width="100"></el-table-column>
          <el-table-column prop="url" label="URL" width="300"></el-table-column>
          <el-table-column prop="status" label="状态" width="100"></el-table-column>
          <el-table-column prop="size" label="大小" width="100"></el-table-column>
          <el-table-column prop="time" label="时间" width="150"></el-table-column>
          <el-table-column prop="response" label="响应" width="300">
            <template #default="scope">
              <el-popover
                trigger="click"
                placement="top"
                width="600"
              >
                <pre>{{ scope.row.response }}</pre>
                <template #reference>
                  <el-button type="text" size="small">查看响应</el-button>
                </template>
              </el-popover>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
    
    <el-card class="monitor-card">
      <template #header>
        <div class="card-header">
          <span>控制台日志监控</span>
        </div>
      </template>
      <div class="monitor-content">
        <el-table :data="consoleLogs" style="width: 100%">
          <el-table-column prop="type" label="类型" width="100"></el-table-column>
          <el-table-column prop="message" label="消息" width="500"></el-table-column>
          <el-table-column prop="time" label="时间" width="150"></el-table-column>
        </el-table>
      </div>
    </el-card>
    
    <el-card class="monitor-card">
      <template #header>
        <div class="card-header">
          <span>WebSocket消息监控</span>
        </div>
      </template>
      <div class="monitor-content">
        <el-table :data="websocketMessages" style="width: 100%">
          <el-table-column prop="type" label="类型" width="100"></el-table-column>
          <el-table-column prop="url" label="URL" width="250"></el-table-column>
          <el-table-column prop="message" label="消息" width="500"></el-table-column>
          <el-table-column prop="time" label="时间" width="150"></el-table-column>
        </el-table>
      </div>
    </el-card>
    
    <el-card class="monitor-card">
      <template #header>
        <div class="card-header">
          <span>内存地址日志监控</span>
        </div>
      </template>
      <div class="monitor-content">
        <el-table :data="memoryAddressLogs" style="width: 100%">
          <el-table-column prop="source" label="来源" width="100"></el-table-column>
          <el-table-column prop="url" label="URL" width="250">
            <template #default="scope">
              {{ scope.row.source === 'websocket' ? scope.row.url : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="message" label="日志内容" width="500"></el-table-column>
          <el-table-column prop="time" label="时间" width="150"></el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const networkRequests = ref([])
const consoleLogs = ref([])
const websocketMessages = ref([])
const memoryAddressLogs = ref([])

// 拦截网络请求
const originalXHR = XMLHttpRequest
const originalFetch = window.fetch

// 重写XMLHttpRequest
window.XMLHttpRequest = function() {
  const xhr = new originalXHR()
  const open = xhr.open
  const send = xhr.send
  
  xhr.open = function(method, url) {
    this._method = method
    this._url = url
    return open.apply(this, arguments)
  }
  
  xhr.send = function(data) {
    const startTime = Date.now()
    
    xhr.addEventListener('load', () => {
      const endTime = Date.now()
      networkRequests.value.push({
        method: this._method,
        url: this._url,
        status: this.status,
        size: this.responseText.length,
        time: new Date().toLocaleTimeString(),
        response: this.responseText
      })
    })
    
    return send.apply(this, arguments)
  }
  
  return xhr
}

// 重写fetch
window.fetch = async function(url, options) {
  const startTime = Date.now()
  const response = await originalFetch.apply(this, arguments)
  const endTime = Date.now()
  
  // 克隆响应以读取内容
  const clonedResponse = response.clone()
  let responseText = ''
  try {
    responseText = await clonedResponse.text()
  } catch (e) {
    responseText = 'Binary data'
  }
  
  networkRequests.value.push({
    method: options?.method || 'GET',
    url: url,
    status: response.status,
    size: responseText.length,
    time: new Date().toLocaleTimeString(),
    response: responseText
  })
  
  return response
}

// 拦截WebSocket通信
const originalWebSocket = window.WebSocket
window.WebSocket = class extends originalWebSocket {
  constructor(url, protocols) {
    super(url, protocols)
    this._url = url
    
    // 记录WebSocket创建
    websocketMessages.value.push({
      type: 'connect',
      url: url,
      time: new Date().toLocaleTimeString(),
      message: `WebSocket连接已创建: ${url}`
    })
    
    // 监听WebSocket消息
    this.addEventListener('message', (event) => {
      const message = event.data
      websocketMessages.value.push({
        type: 'message',
        url: url,
        time: new Date().toLocaleTimeString(),
        message: message
      })
      
      // 检查是否包含内存地址格式（匹配一个或多个内存地址）
      if (message.match(/\[0x[0-9a-fA-F]+(\s+0x[0-9a-fA-F]+)+\]/)) {
        memoryAddressLogs.value.push({
          source: 'websocket',
          url: url,
          time: new Date().toLocaleTimeString(),
          message: message
        })
      }
    })
    
    // 监听WebSocket关闭
    this.addEventListener('close', () => {
      websocketMessages.value.push({
        type: 'close',
        url: url,
        time: new Date().toLocaleTimeString(),
        message: `WebSocket连接已关闭: ${url}`
      })
    })
    
    // 监听WebSocket错误
    this.addEventListener('error', (error) => {
      websocketMessages.value.push({
        type: 'error',
        url: url,
        time: new Date().toLocaleTimeString(),
        message: `WebSocket错误: ${error.message}`
      })
    })
  }
  
  send(data) {
    websocketMessages.value.push({
      type: 'send',
      url: this._url,
      time: new Date().toLocaleTimeString(),
      message: data
    })
    
    return super.send(data)
  }
}

// 重写console方法
const originalConsole = {
  log: console.log,
  error: console.error,
  warn: console.warn,
  info: console.info,
  debug: console.debug
}

const logTypes = ['log', 'error', 'warn', 'info', 'debug']
logTypes.forEach(type => {
  console[type] = function() {
    const args = Array.from(arguments)
    const message = args.map(arg => {
      if (typeof arg === 'object') {
        try { return JSON.stringify(arg) } 
        catch (e) { return arg.toString() }
      }
      return arg
    }).join(' ')
    
    // 创建日志对象
    const logEntry = {
      type: type,
      message: message,
      time: new Date().toLocaleTimeString(),
      stack: new Error().stack
    }
    
    // 将所有日志添加到控制台日志列表
    consoleLogs.value.push(logEntry)
    
    // 检查是否包含内存地址格式（匹配一个或多个内存地址）
    const memoryAddressPattern = /\[0x[0-9a-fA-F]+(\s+0x[0-9a-fA-F]+)+\]/
    if (memoryAddressPattern.test(message)) {
      // 将内存地址日志添加到专门的内存地址日志列表
      memoryAddressLogs.value.push({
        ...logEntry,
        source: 'console'
      })
      
      // 显示详细信息
      originalConsole.error('发现内存地址日志:', message)
      originalConsole.error('调用栈:', new Error().stack)
    }
    
    return originalConsole[type].apply(this, args)
  }
})

onUnmounted(() => {
  // 恢复原始方法
  window.XMLHttpRequest = originalXHR
  window.fetch = originalFetch
  window.WebSocket = originalWebSocket
  logTypes.forEach(type => {
    console[type] = originalConsole[type]
  })
})
</script>

<style scoped>
.debug-monitor {
  padding: 20px;
}

.monitor-card {
  margin-bottom: 20px;
}

.monitor-content {
  max-height: 400px;
  overflow-y: auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>