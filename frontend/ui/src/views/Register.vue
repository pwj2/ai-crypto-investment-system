<template>
  <div class="register-container">
    <!-- 背景装饰元素 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    
    <div class="register-card" :class="{ 'animate-in': isVisible }">
      <div class="logo-container">
        <div class="logo">
          <el-icon class="logo-icon"><TrendCharts /></el-icon>
        </div>
      </div>
      
      <h2 class="register-title">创建新账户</h2>
      <p class="register-subtitle">加入AI加密货币投资系统</p>
      
      <el-form 
        :model="registerForm" 
        label-position="left" 
        label-width="0"
        :rules="registerRules"
        ref="registerFormRef"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="用户名"
            prefix-icon="User"
            class="register-input"
            autofocus
          />
        </el-form-item>
        
        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="邮箱地址"
            prefix-icon="Message"
            class="register-input"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            class="register-input"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="确认密码"
            prefix-icon="Lock"
            class="register-input"
            show-password
          />
        </el-form-item>
        
        <div class="register-options">
          <el-checkbox v-model="registerForm.agreeTerms" class="agree-terms">
            我已阅读并同意<a href="#" class="terms-link">服务条款</a>和<a href="#" class="terms-link">隐私政策</a>
          </el-checkbox>
        </div>
        
        <el-form-item>
          <el-button
            type="primary"
            block
            size="large"
            class="register-button"
            @click="handleRegister"
            :loading="isLoading"
          >
            <template v-if="isLoading">
              <el-icon class="is-loading"><Loading /></el-icon>
              注册中...
            </template>
            <template v-else>
              注册
            </template>
          </el-button>
        </el-form-item>
        
        <el-form-item>
          <el-button
            block
            size="large"
            class="login-button"
            @click="handleLogin"
          >
            已有账户？登录
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
  name: 'Register',
  setup() {
    const registerForm = ref({
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
      agreeTerms: false
    })
    
    const registerFormRef = ref(null)
    const isLoading = ref(false)
    const isVisible = ref(false)
    const router = useRouter()
    
    const registerRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱地址', trigger: 'blur' },
        { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 8, max: 30, message: '密码长度在 8 到 30 个字符', trigger: 'blur' },
        { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{8,30}$/, message: '密码必须包含大小写字母和数字', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        { validator: (rule, value, callback) => {
            if (value !== registerForm.value.password) {
              callback(new Error('两次输入的密码不一致'))
            } else {
              callback()
            }
          }, trigger: 'blur' }
      ],
      agreeTerms: [
        { validator: (rule, value, callback) => {
            if (!value) {
              callback(new Error('请阅读并同意服务条款和隐私政策'))
            } else {
              callback()
            }
          }, trigger: 'change' }
      ]
    }
    
    onMounted(() => {
      // 添加延迟动画效果
      setTimeout(() => {
        isVisible.value = true
      }, 100)
    })
    
    const handleRegister = async () => {
      if (!registerFormRef.value) return
      
      try {
        await registerFormRef.value.validate()
        isLoading.value = true
        
        // 模拟注册请求
        setTimeout(() => {
          console.log('注册信息:', registerForm.value)
          
          // 注册成功，跳转到登录页
          router.push('/login')
        }, 1500)
      } catch (error) {
        console.log('表单验证失败', error)
      } finally {
        setTimeout(() => {
          isLoading.value = false
        }, 1500)
      }
    }
    
    const handleLogin = () => {
      router.push('/login')
    }
    
    return {
      registerForm,
      registerFormRef,
      registerRules,
      isLoading,
      isVisible,
      handleRegister,
      handleLogin
    }
  }
})
</script>

<style scoped>
.register-container {
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

.register-card {
  width: 100%;
  max-width: 480px;
  background: white;
  border-radius: 20px;
  padding: 50px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  z-index: 10;
  opacity: 0;
  transform: translateY(30px) scale(0.95);
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.register-card.animate-in {
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

.register-title {
  font-size: 28px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 8px;
  text-align: center;
  letter-spacing: -0.5px;
}

.register-subtitle {
  font-size: 14px;
  color: #718096;
  margin-bottom: 36px;
  text-align: center;
  letter-spacing: 0.3px;
}

.register-input {
  border-radius: 12px;
  height: 52px;
  font-size: 16px;
  border: 2px solid #e2e8f0;
  transition: all 0.3s ease;
}

.register-input:hover {
  border-color: #cbd5e0;
}

.register-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  animation: inputFocus 0.3s ease;
}

/* 表单验证错误样式 */
.el-form-item.is-error .register-input {
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

/* 同意条款复选框样式 */
.agree-terms {
  font-size: 13px;
  color: #4a5568;
  transition: color 0.2s ease;
  margin-bottom: 28px;
}

.agree-terms .el-checkbox__inner {
  border-radius: 6px;
  border: 2px solid #e2e8f0;
  transition: all 0.3s ease;
}

.agree-terms .el-checkbox__inner:hover {
  border-color: #667eea;
  transform: scale(1.1);
}

.agree-terms .el-checkbox__input.is-checked .el-checkbox__inner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
}

.terms-link {
  color: #667eea;
  text-decoration: none;
  transition: color 0.2s ease;
}

.terms-link:hover {
  color: #5a67d8;
  text-decoration: underline;
}

/* 按钮加载状态优化 */
.register-button:disabled,
.login-button:disabled {
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

@keyframes inputFocus {
  0% { transform: scale(1); }
  50% { transform: scale(1.02); }
  100% { transform: scale(1); }
}

/* 登录按钮进入动画 */
.login-button {
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
.register-button:active,
.login-button:active {
  transform: translateY(0) scale(0.98) !important;
  transition: transform 0.1s ease;
}

.register-button {
  border-radius: 12px;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
  border: none;
  transition: all 0.3s ease;
  letter-spacing: 0.5px;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(72, 187, 120, 0.4);
  background: linear-gradient(135deg, #38a169 0%, #2f855a 100%);
}

.register-button:active {
  transform: translateY(0);
}

.login-button {
  border-radius: 12px;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 16px;
  background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e0 100%);
  border: none;
  transition: all 0.3s ease;
  letter-spacing: 0.5px;
  color: #4a5568;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(226, 232, 240, 0.5);
  background: linear-gradient(135deg, #cbd5e0 0%, #a0aec0 100%);
  color: #2d3748;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-card {
    padding: 40px 30px;
    max-width: 420px;
  }
  
  .register-title {
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
  .register-card {
    padding: 35px 25px;
    max-width: 100%;
    margin: 0 20px;
  }
  
  .register-title {
    font-size: 22px;
  }
  
  .register-subtitle {
    font-size: 13px;
  }
  
  .register-input,
  .register-button,
  .login-button {
    height: 48px;
    font-size: 15px;
  }
  
  .agree-terms {
    font-size: 12px;
  }
  
  .circle {
    opacity: 0.5;
  }
}
</style>