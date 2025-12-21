import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path
      }
    },
    // 启用gzip压缩
    compress: true
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
    // 代码分割
    rollupOptions: {
      output: {
        manualChunks: {
          // 第三方库单独打包
          'vue-vendor': ['vue', 'vue-router', 'pinia'],
          'element-plus': ['element-plus'],
          // 将echarts等大型库单独打包，减少主包体积
          'chart-libs': ['echarts']
        }
      }
    },
    // 启用gzip压缩
    compress: true,
    // 关闭CSS代码分割，减少HTTP请求
    cssCodeSplit: false
  },
  // 优化依赖预构建
  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia', 'element-plus', 'echarts'],
    exclude: ['node_modules']
  }
})