<template>
  <div class="register-container">
    <button class="back-btn" @click="$router.back()">← 返回</button>
    <!-- 背景效果 -->
    <div class="background-effects">
      <div class="bg-glow bg-glow-1"></div>
      <div class="bg-glow bg-glow-2"></div>
      <div class="bg-glow bg-glow-3"></div>
    </div>

    <!-- 注册卡片 -->
    <div class="register-box fade-in">
      <!-- 头部 -->
      <div class="register-header">
        <div class="logo-container">
          <div class="logo-icon">🚀</div>
          <h1 class="logo-text">酷炫博客</h1>
        </div>
        <p class="register-subtitle">创建新账号，开启你的创作之旅</p>
      </div>

      <!-- 注册表单 -->
      <form @submit.prevent="submitRegister" class="register-form">
        <div class="form-group">
          <label for="username" class="form-label">
            <span class="label-icon">👤</span>
            用户名
          </label>
          <div class="input-container">
            <input 
              type="text" 
              id="username" 
              v-model="username" 
              placeholder="请输入用户名"
              class="form-input"
              @focus="onInputFocus($event)"
              @blur="onInputBlur($event)"
            >
            <span class="input-icon">👤</span>
          </div>
        </div>

        <div class="form-group">
          <label for="email" class="form-label">
            <span class="label-icon">📧</span>
            邮箱
          </label>
          <div class="input-container">
            <input 
              type="email" 
              id="email" 
              v-model="email" 
              placeholder="请输入邮箱"
              class="form-input"
              @focus="onInputFocus($event)"
              @blur="onInputBlur($event)"
            >
            <span class="input-icon">📧</span>
          </div>
        </div>

        <div class="form-group">
          <label for="password" class="form-label">
            <span class="label-icon">🔒</span>
            密码
          </label>
          <div class="input-container">
            <input 
              type="password" 
              id="password" 
              v-model="password" 
              placeholder="请输入密码（至少6位）"
              class="form-input"
              @focus="onInputFocus($event)"
              @blur="onInputBlur($event)"
            >
            <span class="input-icon">🔒</span>
          </div>
        </div>

        <div class="form-group">
          <label for="confirmPassword" class="form-label">
            <span class="label-icon">🔒</span>
            确认密码
          </label>
          <div class="input-container">
            <input 
              type="password" 
              id="confirmPassword" 
              v-model="confirmPassword" 
              placeholder="请再次输入密码"
              class="form-input"
              @focus="onInputFocus($event)"
              @blur="onInputBlur($event)"
            >
            <span class="input-icon">🔒</span>
          </div>
        </div>

        <label class="checkbox-container">
          <input type="checkbox" v-model="agreeTerms">
          <span class="checkbox-custom"></span>
          <span class="checkbox-label">我已阅读并同意<a href="#" class="terms-link">用户协议</a>和<a href="#" class="terms-link">隐私政策</a></span>
        </label>

        <button type="submit" class="btn-primary" :disabled="isLoading || !agreeTerms">
          <span v-if="!isLoading" class="btn-icon">🚀</span>
          <span v-else class="btn-loading">⏳</span>
          {{ isLoading ? '注册中...' : '立即注册' }}
        </button>

        <div class="divider">
          <span class="divider-text">或</span>
        </div>

        <div class="social-login">
          <button type="button" class="social-btn social-wechat">
            <span class="social-icon">💬</span>
            微信注册
          </button>
          <button type="button" class="social-btn social-github">
            <span class="social-icon">🐱</span>
            GitHub注册
          </button>
        </div>

        <div class="register-footer">
          <p>已有账号？<router-link to="/login" class="link">立即登录</router-link></p>
          <p><router-link to="/" class="link">返回首页</router-link></p>
        </div>
      </form>
    </div>

    <Modal 
      v-if="showModal"
      :visible="showModal"
      :title="modalTitle"
      :message="modalMessage"
      @close="showModal = false"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { register } from '@/api/user'
import { useRouter } from 'vue-router'
import Modal from '@/components/Modal.vue'

const username = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const agreeTerms = ref(false)
const isLoading = ref(false)
const router = useRouter()
const showModal = ref(false)
const modalTitle = ref('提示')
const modalMessage = ref('')

const onInputFocus = (event) => {
  const inputContainer = event.target.parentElement
  inputContainer.classList.add('focused')
}

const onInputBlur = (event) => {
  const inputContainer = event.target.parentElement
  inputContainer.classList.remove('focused')
}

const submitRegister = async () => {
  if (!username.value || !email.value || !password.value) {
    modalTitle.value = '提示'
    modalMessage.value = '请填写所有必填项'
    showModal.value = true
    return
  }

  if (password.value !== confirmPassword.value) {
    modalTitle.value = '提示'
    modalMessage.value = '两次输入的密码不一致'
    showModal.value = true
    return
  }

  if (password.value.length < 6) {
    modalTitle.value = '提示'
    modalMessage.value = '密码长度不能少于 6 位'
    showModal.value = true
    return
  }

  if (!agreeTerms.value) {
    modalTitle.value = '提示'
    modalMessage.value = '请阅读并同意用户协议和隐私政策'
    showModal.value = true
    return
  }

  isLoading.value = true
  
  try {
    const response = await register({
      username: username.value,
      email: email.value,
      username: username.value,
      password: password.value
    })
    if (response.code === 200) {
      localStorage.setItem('registerSuccess', 'true')
      window.location.href = '/login'
    } else {
      modalTitle.value = '错误'
      modalMessage.value = response.message || '注册失败'
      showModal.value = true
    }
  } catch (error) {
    console.error('注册失败:', error)
    modalTitle.value = '错误'
    modalMessage.value = error.response?.data?.message || '注册失败，请稍后重试'
    showModal.value = true
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
/* 容器样式 */
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #0f0f23 0%, #1a1a3e 100%);
  padding: 20px;
  overflow: hidden;
  position: relative;
}

.register-container .back-btn {
  position: fixed;
  top: 20px;
  left: 20px;
  padding: 8px 16px;
  background: rgba(123, 31, 162, 0.3);
  border: 1px solid rgba(123, 31, 162, 0.5);
  border-radius: 25px;
  color: #e0e0e0;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 1000;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.register-container .back-btn:hover {
  background: rgba(123, 31, 162, 0.5);
  border-color: #9c27b0;
}

/* 背景效果 */
.background-effects {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  overflow: hidden;
}

.bg-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  animation: glowMove 20s ease-in-out infinite;
}

.bg-glow-1 {
  width: 600px;
  height: 600px;
  background: rgba(77, 208, 225, 0.1);
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.bg-glow-2 {
  width: 400px;
  height: 400px;
  background: rgba(255, 107, 107, 0.1);
  bottom: -100px;
  right: -100px;
  animation-delay: 5s;
}

.bg-glow-3 {
  width: 300px;
  height: 300px;
  background: rgba(255, 165, 2, 0.1);
  top: 50%;
  right: 10%;
  animation-delay: 10s;
}

@keyframes glowMove {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  25% {
    transform: translate(20px, 20px) scale(1.1);
  }
  50% {
    transform: translate(0, 40px) scale(0.9);
  }
  75% {
    transform: translate(-20px, 20px) scale(1.05);
  }
}

/* 注册卡片 */
.register-box {
  width: 100%;
  max-width: 500px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 24px;
  padding: 50px;
  border: 1px solid rgba(77, 208, 225, 0.2);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(15px);
  z-index: 1;
  transition: all 0.3s ease;
}

.register-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 25px 70px rgba(0, 0, 0, 0.5);
  border-color: rgba(77, 208, 225, 0.4);
}

/* 头部 */
.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  margin-bottom: 15px;
}

.logo-icon {
  font-size: 2.5rem;
  animation: pulse 2s ease-in-out infinite;
}

.logo-text {
  font-size: 2.5rem;
  color: #4dd0e1;
  background: linear-gradient(90deg, #4dd0e1, #00bcd4);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 800;
  text-shadow: 0 0 30px rgba(77, 208, 225, 0.4);
  animation: glow 2s ease-in-out infinite alternate;
}

.register-subtitle {
  color: #a0a0a0;
  font-size: 1.1rem;
  margin: 0;
  opacity: 0.8;
}

/* 表单 */
.register-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #c0c0c0;
  font-size: 0.95rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.label-icon {
  font-size: 1.1rem;
}

.input-container {
  position: relative;
  transition: all 0.3s ease;
}

.input-container.focused {
  transform: translateY(-2px);
}

.form-input {
  width: 100%;
  padding: 16px 50px 16px 20px;
  background: rgba(40, 40, 40, 0.8);
  border: 2px solid #333;
  border-radius: 16px;
  color: #e0e0e0;
  outline: none;
  font-size: 1rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.form-input:focus {
  border-color: #4dd0e1;
  box-shadow: 0 0 20px rgba(77, 208, 225, 0.4);
  background: rgba(40, 40, 40, 0.9);
}

.form-input::placeholder {
  color: #666;
  transition: color 0.3s ease;
}

.form-input:focus::placeholder {
  color: #888;
}

.input-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
  transition: all 0.3s ease;
  font-size: 1.1rem;
}

.input-container.focused .input-icon {
  color: #4dd0e1;
  transform: translateY(-50%) scale(1.1);
}

/* 协议 checkbox */
.checkbox-container {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  cursor: pointer;
  user-select: none;
  margin: 10px 0;
}

.checkbox-container input[type="checkbox"] {
  display: none;
  margin-top: 2px;
}

.checkbox-custom {
  width: 18px;
  height: 18px;
  border: 2px solid #333;
  border-radius: 4px;
  position: relative;
  transition: all 0.3s ease;
  margin-top: 2px;
}

.checkbox-container input[type="checkbox"]:checked + .checkbox-custom {
  background: #4dd0e1;
  border-color: #4dd0e1;
  box-shadow: 0 0 10px rgba(77, 208, 225, 0.5);
}

.checkbox-container input[type="checkbox"]:checked + .checkbox-custom::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #0f0f23;
  font-weight: bold;
  font-size: 12px;
}

.checkbox-label {
  color: #a0a0a0;
  font-size: 0.9rem;
  line-height: 1.4;
  flex: 1;
}

.terms-link {
  color: #4dd0e1;
  text-decoration: none;
  transition: color 0.3s ease;
}

.terms-link:hover {
  color: #64ffda;
  text-decoration: underline;
}

/* 注册按钮 */
.btn-primary {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 18px;
  background: linear-gradient(90deg, #4dd0e1, #00bcd4);
  border: none;
  border-radius: 30px;
  color: #0f0f23;
  font-weight: 700;
  font-size: 1.15rem;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 15px;
  box-shadow: 0 6px 20px rgba(77, 208, 225, 0.4);
  position: relative;
  overflow: hidden;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(77, 208, 225, 0.6);
  background: linear-gradient(90deg, #64ffda, #4dd0e1);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-icon {
  font-size: 1.2rem;
  animation: pulse 2s ease-in-out infinite;
}

.btn-loading {
  font-size: 1.2rem;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 分隔线 */
.divider {
  position: relative;
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  margin: 30px 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.divider-text {
  background: rgba(255, 255, 255, 0.05);
  padding: 0 20px;
  color: #888;
  font-size: 0.9rem;
  backdrop-filter: blur(10px);
}

/* 社交登录 */
.social-login {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
}

.social-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.03);
  color: #a0a0a0;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.social-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.2);
}

.social-wechat:hover {
  border-color: #07C160;
  color: #07C160;
  box-shadow: 0 0 20px rgba(7, 193, 96, 0.3);
}

.social-github:hover {
  border-color: #333;
  color: #fff;
  box-shadow: 0 0 20px rgba(51, 51, 51, 0.5);
}

.social-icon {
  font-size: 1.2rem;
}

/* 底部 */
.register-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 0.95rem;
  color: #888;
  line-height: 1.5;
}

.link {
  color: #4dd0e1;
  text-decoration: none;
  transition: all 0.3s ease;
  padding: 3px 8px;
  border-radius: 4px;
  position: relative;
  overflow: hidden;
}

.link:hover {
  color: #64ffda;
  background: rgba(77, 208, 225, 0.1);
  transform: translateY(-1px);
}

.link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: #4dd0e1;
  transition: width 0.3s ease;
}

.link:hover::after {
  width: 100%;
}

/* 动画 */
.fade-in {
  animation: fadeIn 0.8s ease-out forwards;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

@keyframes glow {
  from {
    text-shadow: 0 0 20px rgba(77, 208, 225, 0.3);
  }
  to {
    text-shadow: 0 0 40px rgba(77, 208, 225, 0.6);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-box {
    padding: 35px;
    margin: 20px;
  }
  
  .logo-text {
    font-size: 2rem;
  }
  
  .register-subtitle {
    font-size: 1rem;
  }
  
  .form-input {
    padding: 14px 45px 14px 18px;
  }
  
  .btn-primary {
    padding: 16px;
    font-size: 1.05rem;
  }
  
  .social-login {
    flex-direction: column;
  }
  
  .social-btn {
    padding: 12px;
  }
  
  .checkbox-label {
    font-size: 0.85rem;
  }
}

@media (max-width: 480px) {
  .register-box {
    padding: 25px;
  }
  
  .logo-container {
    gap: 10px;
  }
  
  .logo-icon {
    font-size: 2rem;
  }
  
  .logo-text {
    font-size: 1.8rem;
  }
  
  .form-input {
    padding: 12px 40px 12px 16px;
  }
  
  .btn-primary {
    padding: 14px;
    font-size: 1rem;
  }
  
  .checkbox-container {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .checkbox-custom {
    margin-top: 0;
  }
}
</style>