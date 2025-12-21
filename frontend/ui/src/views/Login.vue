<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">AI 加密货币投资系统</h2>
      <p class="login-subtitle">请登录您的账户</p>
      
      <el-form :model="loginForm" label-position="left" label-width="0">
        <el-form-item>
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            prefix-icon="User"
            class="login-input"
          />
        </el-form-item>
        
        <el-form-item>
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            class="login-input"
          />
        </el-form-item>
        
        <div class="login-options">
          <el-checkbox v-model="loginForm.rememberMe" class="remember-me">记住我</el-checkbox>
          <el-link type="primary" class="forgot-password">忘记密码</el-link>
        </div>
        
        <el-form-item>
          <el-button
            type="primary"
            block
            size="large"
            class="login-button"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
        
        <el-form-item>
          <el-button
            block
            size="large"
            class="register-button"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue'

export default defineComponent({
  name: 'Login',
  setup() {
    const loginForm = ref({
      username: 'admin',
      password: '',
      rememberMe: true
    })
    
    const handleLogin = () => {
      // 这里可以添加登录逻辑
      console.log('登录按钮点击', loginForm.value)
      // 模拟登录成功，跳转到首页
      window.location.href = '/dashboard'
    }
    
    const handleRegister = () => {
      // 这里可以添加注册逻辑
      console.log('注册按钮点击')
    }
    
    return {
      loginForm,
      handleLogin,
      handleRegister
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
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: 16px;
  padding: 40px 30px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.login-title {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
  text-align: center;
}

.login-subtitle {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 30px;
  text-align: center;
}

.login-input {
  border-radius: 8px;
  height: 48px;
  font-size: 16px;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.remember-me {
  font-size: 14px;
  color: #34495e;
}

.forgot-password {
  font-size: 14px;
}

.login-button {
  border-radius: 8px;
  height: 48px;
  font-size: 16px;
  background: #409eff;
  border: none;
}

.register-button {
  border-radius: 8px;
  height: 48px;
  font-size: 16px;
  margin-top: 12px;
}
</style>