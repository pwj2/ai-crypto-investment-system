<template>
  <div class="login-container">
    <!-- 背景装饰元素 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    
    <div class="login-card" :class="{ 'animate-in': isVisible }">
      <div class="logo-container">
        <div class="logo">
          <el-icon class="logo-icon"><TrendCharts /></el-icon>
        </div>
      </div>
      
      <h2 class="login-title">AI 加密货币投资系统</h2>
      <p class="login-subtitle">请登录您的账户</p>
      
      <el-form 
        :model="loginForm" 
        label-position="left" 
        label-width="0"
        :rules="loginRules"
        ref="loginFormRef"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            prefix-icon="User"
            class="login-input"
            autofocus
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            class="login-input"
            show-password
          />
        </el-form-item>
        
        <div class="login-options">
          <el-checkbox v-model="loginForm.rememberMe" class="remember-me">记住我</el-checkbox>
          <el-link type="primary" class="forgot-password" @click="handleForgotPassword">忘记密码</el-link>
        </div>
        
        <el-form-item>
          <el-button
            type="primary"
            block
            size="large"
            class="login-button"
            @click="handleLogin"
            :loading="isLoading"
          >
            <template v-if="isLoading">
              <el-icon class="is-loading"><Loading /></el-icon>
              登录中...
            </template>
            <template v-else>
              登录
            </template>
          </el-button>
        </el-form-item>
        
        <el-form-item>
          <el-button
            block
            size="large"
            class="register-button"
            @click="handleRegister"
            type="success"
          >
            注册新账户
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default defineComponent({
  name: 'Login',
  setup() {
    const loginForm = ref({
      username: 'admin',
      password: '',
      rememberMe: true
    })
    
    const loginFormRef = ref(null)
    const isLoading = ref(false)
    const isVisible = ref(false)
    const router = useRouter()
    
    const loginRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 30, message: '密码长度在 6 到 30 个字符', trigger: 'blur' }
      ]
    }
    
    onMounted(() => {
      // 添加延迟动画效果
      setTimeout(() => {
        isVisible.value = true
      }, 100)
      
      // 如果记住了密码，从localStorage获取
      const savedUsername = localStorage.getItem('savedUsername')
      if (savedUsername) {
        loginForm.value.username = savedUsername
      }
    })
    
    const handleLogin = async () => {
      if (!loginFormRef.value) return
      
      try {
        await loginFormRef.value.validate()
        isLoading.value = true
        
        // 模拟登录请求
        setTimeout(() => {
          console.log('登录按钮点击', loginForm.value)
          
          // 保存记住的用户名
          if (loginForm.value.rememberMe) {
            localStorage.setItem('savedUsername', loginForm.value.username)
          } else {
            localStorage.removeItem('savedUsername')
          }
          
          // 登录成功，跳转到首页
          router.push('/dashboard')
        }, 1500)
      } catch (error) {
        console.log('表单验证失败', error)
      } finally {
        setTimeout(() => {
          isLoading.value = false
        }, 1500)
      }
    }
    
    const handleRegister = () => {
      console.log('注册按钮点击')
      // 这里可以添加注册页面跳转逻辑
    }
    
    const handleForgotPassword = () => {
      console.log('忘记密码点击')
      // 这里可以添加忘记密码逻辑
    }
    
    return {
      loginForm,
      loginFormRef,
      loginRules,
      isLoading,
      isVisible,
      handleLogin,
      handleRegister,
      handleForgotPassword
    }
  }
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  overflow: hidden;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 150px;
  height: 150px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: 15%;
  right: 15%;
  animation-delay: -10s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  top: 50%;
  right: 25%;
  animation-delay: -5s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-30px) rotate(180deg);
  }
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: white;
  border-radius: 20px;
  padding: 50px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  z-index: 10;
  opacity: 0;
  transform: translateY(30px) scale(0.95);
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.login-card.animate-in {
  opacity: 1;
  transform: translateY(0) scale(1);
}

.logo-container {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.logo {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 16px;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
  animation: pulse 2s infinite;
}

.logo-icon {
  font-size: 40px;
  color: white;
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
    transform: scale(1);
  }
  50% {
    box-shadow: 0 12px 35px rgba(102, 126, 234, 0.5);
    transform: scale(1.05);
  }
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 8px;
  text-align: center;
  letter-spacing: -0.5px;
}

.login-subtitle {
  font-size: 14px;
  color: #718096;
  margin-bottom: 36px;
  text-align: center;
  letter-spacing: 0.3px;
}

.login-input {
  border-radius: 12px;
  height: 52px;
  font-size: 16px;
  border: 2px solid #e2e8f0;
  transition: all 0.3s ease;
}

.login-input:hover {
  border-color: #cbd5e0;
}

.login-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* 表单验证错误样式 */
.el-form-item.is-error .login-input {
  border-color: #f56c6c;
  box-shadow: 0 0 0 3px rgba(245, 108, 108, 0.1);
  animation: shake 0.5s ease;
}

/* 错误提示文字样式 */
.el-form-item__error {
  font-size: 12px;
  color: #f56c6c;
  margin-top: 4px;
  animation: fadeIn 0.3s ease;
}

/* 记住我复选框样式优化 */
.remember-me .el-checkbox__inner {
  border-radius: 6px;
  border: 2px solid #e2e8f0;
  transition: all 0.3s ease;
}

.remember-me .el-checkbox__inner:hover {
  border-color: #667eea;
  transform: scale(1.1);
}

.remember-me .el-checkbox__input.is-checked .el-checkbox__inner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
}

/* 按钮加载状态优化 */
.login-button:disabled,
.register-button:disabled {
  opacity: 0.6;
  transform: none !important;
  box-shadow: none !important;
  cursor: not-allowed;
}

/* 动画效果增强 */
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

/* 为输入框添加聚焦动画 */
.login-input:focus {
  animation: inputFocus 0.3s ease;
}

@keyframes inputFocus {
  0% { transform: scale(1); }
  50% { transform: scale(1.02); }
  100% { transform: scale(1); }
}

/* 注册按钮进入动画 */
.register-button {
  animation: slideUp 0.6s ease 0.2s both;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 按钮点击反馈 */
.login-button:active,
.register-button:active {
  transform: translateY(0) scale(0.98) !important;
  transition: transform 0.1s ease;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}

.remember-me {
  font-size: 14px;
  color: #4a5568;
  transition: color 0.2s ease;
}

.remember-me:hover {
  color: #2d3748;
}

.forgot-password {
  font-size: 14px;
  transition: all 0.2s ease;
}

.forgot-password:hover {
  color: #5a67d8;
  text-decoration: underline;
}

.login-button {
  border-radius: 12px;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
  letter-spacing: 0.5px;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(102, 126, 234, 0.4);
  background: linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%);
}

.login-button:active {
  transform: translateY(0);
}

.register-button {
  border-radius: 12px;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 16px;
  background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
  border: none;
  transition: all 0.3s ease;
  letter-spacing: 0.5px;
  color: white;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(72, 187, 120, 0.4);
  background: linear-gradient(135deg, #38a169 0%, #2f855a 100%);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    padding: 40px 30px;
    max-width: 380px;
  }
  
  .login-title {
    font-size: 24px;
  }
  
  .logo {
    width: 60px;
    height: 60px;
  }
  
  .logo-icon {
    font-size: 32px;
  }
}

@media (max-width: 480px) {
  .login-card {
    padding: 35px 25px;
    max-width: 100%;
    margin: 0 20px;
  }
  
  .login-title {
    font-size: 22px;
  }
  
  .login-subtitle {
    font-size: 13px;
  }
  
  .login-input,
  .login-button,
  .register-button {
    height: 48px;
    font-size: 15px;
  }
  
  .circle {
    opacity: 0.5;
  }
}
</style>