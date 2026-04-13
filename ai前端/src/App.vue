<template>
  <div id="app" :class="{ 'login-register-page': $route.path === '/login' || $route.path === '/register' }">
    <!-- 导航栏 - 登录/注册/聊天页面不显示 -->
    <nav v-if="!isHideNav" class="navbar">
      <div class="nav-container">
        <router-link to="/" class="nav-logo">
          <span class="logo-icon">🚀</span>
          <span class="nav-logo-text">酷炫博客</span>
        </router-link>
        
        <div class="nav-links">
          <!-- 首页模块 -->
          <router-link to="/" class="nav-link" :class="{ active: $route.path === '/' }">🏠 首页</router-link>
          
          <!-- 博客模块 -->
          <router-link to="/blog" class="nav-link" :class="{ active: $route.path === '/blog' }">📖 博客列表</router-link>
          <router-link v-if="user" to="/blog/create" class="nav-link" :class="{ active: $route.path === '/blog/create' }">✏️ 写博客</router-link>
          <span v-else @click="goToLogin" class="nav-link nav-link-guest nav-link-blog">✏️ 写博客</span>
          
          <!-- 关注模块 -->
          <router-link v-if="user" to="/following" class="nav-link" :class="{ active: $route.path === '/following' }">👥 关注</router-link>
          <span v-else @click="goToLogin" class="nav-link nav-link-guest nav-link-follow">👥 关注</span>
          
          <!-- 聊天模块 -->
          <router-link v-if="user" to="/chat" class="nav-link chat-link" :class="{ active: $route.path === '/chat' }">
            📱 消息
            <span v-if="unreadMessageCount > 0" class="notification-badge">{{ formattedMessageCount }}</span>
          </router-link>
          <span v-else @click="goToLogin" class="nav-link nav-link-guest nav-link-message">📱 消息</span>
          
          <!-- 个人中心模块 -->
          <router-link v-if="user" to="/user-center" class="nav-link" :class="{ active: $route.path === '/user-center' }">👤 个人中心</router-link>
          <span v-else @click="goToLogin" class="nav-link nav-link-guest">👤 个人中心</span>
        </div>
        
        <div class="nav-user">
          <!-- 用户模块 -->
          <template v-if="user">
            <span @click="handleLogout" class="nav-link logout">🚪 退出</span>
          </template>
          
          <template v-else>
            <span @click="goToLogin" class="nav-link login-btn" :class="{ active: $route.path === '/login' }">🔑 登录/注册</span>
          </template>
        </div>
      </div>
    </nav>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <div class="page-wrapper">
          <component :is="Component" :key="$route.fullPath" />
        </div>
      </router-view>
    </main>

    <!-- 页脚 -->
    <footer v-if="showFooter" class="footer">
      <div class="footer-content">
        <p>&copy; 2024 酷炫博客。All rights reserved.</p>
        <p>探索知识的海域，分享每一个精彩瞬间</p>
      </div>
    </footer>

    <!-- 反馈按钮 -->
    <div v-if="showFeedbackBtn" class="feedback-btn" @click="toggleFeedback">
      <span class="feedback-icon">💬</span>
      <span class="feedback-text">反馈</span>
    </div>

    <!-- 回到顶部按钮 -->
    <button 
      v-if="showBackToTop" 
      class="back-to-top" 
      @click="scrollToTop"
      title="回到顶部"
    >
      ↑
    </button>

    <!-- 反馈表单 -->
    <div class="feedback-form" v-if="showFeedback">
      <div class="feedback-header">
        <h3>意见反馈</h3>
        <button class="close-btn" @click="showFeedback = false">&times;</button>
      </div>
      <div class="feedback-content">
        <textarea 
          v-model="feedbackContent"
          placeholder="请输入您的反馈..."
          class="feedback-textarea"
        ></textarea>
        <button class="submit-btn" @click="submitFeedback">提交反馈</button>
      </div>
    </div>

    <!-- 顶部提示框 -->
    <transition name="toast">
      <div v-if="toast.show" class="toast" :class="toast.type">
        <span class="toast-icon">{{ toast.type === 'success' ? '✓' : toast.type === 'error' ? '✕' : toast.type === 'warning' ? '⚠' : 'ℹ' }}</span>
        <span class="toast-message">{{ toast.message }}</span>
      </div>
    </transition>

    <!-- 消息弹窗 -->
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
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Modal from '@/components/Modal.vue'

const router = useRouter()
const route = useRoute()

const user = computed(() => {
  try {
    const userData = JSON.parse(localStorage.getItem('user') || 'null')
    return userData && userData.id ? userData : null
  } catch {
    return null
  }
})

const isLoggedIn = computed(() => !!user.value)

const isHideNav = computed(() => {
  const hideNavPaths = ['/login', '/register', '/liked-blogs', '/collected-blogs', '/all-following', '/blog-management', '/user-edit', '/followers']
  const hideNavPrefixes = ['/chat/', '/author/']
  const blogDetailPrefix = '/blog/'
  return hideNavPaths.some(path => path === route.path) ||
    hideNavPrefixes.some(prefix => route.path.startsWith(prefix)) ||
    (route.path.startsWith(blogDetailPrefix) && route.path !== '/blog/create')
})

const showFeedbackBtn = computed(() => {
  return route.path === '/' || route.path === '/user-center'
})

// 回到顶部按钮
const showBackToTop = ref(false)

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

// 监听滚动事件
onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const handleScroll = () => {
  showBackToTop.value = window.scrollY > 100
}

const showFooter = computed(() => {
  const hideFooterPaths = ['/blog', '/chat', '/author', '/liked-blogs', '/collected-blogs', '/following', '/all-following', '/followers']
  return !hideFooterPaths.some(path => route.path === path || route.path.startsWith(path))
})

// 未读消息数量
const unreadMessageCount = ref(0) // 初始值为0，实际应该从API获取

// 格式化消息数量显示
const formattedMessageCount = computed(() => {
  return unreadMessageCount.value > 99 ? '99+' : unreadMessageCount.value
})

watch(() => route.path, (newPath) => {
  showFeedback.value = false
  // 当用户进入聊天页面时，不再自动清空所有未读消息
  // 而是在具体消息被查看时单独减少数量
})

const searchKeyword = ref('')
const isFocused = ref(false)
const showFeedback = ref(false)
const feedbackContent = ref('')
const showModal = ref(false)
const modalTitle = ref('提示')
const modalMessage = ref('')

const toast = ref({
  show: false,
  message: '',
  type: 'info'
})

let toastTimer = null

const showToast = (message, type = 'info', duration = 3000) => {
  if (toastTimer) clearTimeout(toastTimer)
  toast.value = { show: true, message, type }
  toastTimer = setTimeout(() => {
    toast.value.show = false
  }, duration)
}

// 监听自定义事件，更新未读消息数量
onMounted(() => {
  if (localStorage.getItem('loginSuccess') === 'true') {
    localStorage.removeItem('loginSuccess')
    showToast('登录成功，欢迎回来！', 'success')
  }
  if (localStorage.getItem('registerSuccess') === 'true') {
    localStorage.removeItem('registerSuccess')
    showToast('注册成功，请登录！', 'success')
  }
  if (localStorage.getItem('logoutSuccess') === 'true') {
    localStorage.removeItem('logoutSuccess')
    showToast('已退出登录', 'info')
  }
  fetchUnreadMessageCount()
  
  // 监听更新未读消息数量的事件
  window.addEventListener('updateUnreadCount', (event) => {
    unreadMessageCount.value = event.detail
  })
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  // 移除事件监听器
  window.removeEventListener('updateUnreadCount', (event) => {
    unreadMessageCount.value = event.detail
  })
})

// 处理搜索
const handleSearch = () => {
  if (searchKeyword.value) {
    // TODO: 跳转到搜索结果页
    console.log('搜索关键词:', searchKeyword.value)
  }
}

const handleLogout = () => {
  localStorage.removeItem('user')
  localStorage.setItem('logoutSuccess', 'true')
  window.location.href = '/'
}

const goToLogin = () => {
  window.location.href = '/login'
}

// 处理反馈
const toggleFeedback = () => {
  showFeedback.value = !showFeedback.value
}

const submitFeedback = async () => {
  if (!user.value) {
    showToast('请先登录后再提交反馈', 'warning')
    return
  }

  if (!feedbackContent.value.trim()) {
    showToast('请输入反馈内容', 'warning')
    return
  }

  try {
    const api = await import('@/utils/api')
    await api.default.post('/feedback', {
      userId: user.value.id,
      content: feedbackContent.value.trim()
    })
    showToast('反馈提交成功！感谢您的意见。', 'success')
    feedbackContent.value = ''
    showFeedback.value = false
  } catch (e) {
    console.error('提交反馈失败', e)
    showToast('反馈提交失败，请稍后重试', 'error')
  }
}

// 模拟接收新消息
const receiveNewMessage = () => {
  unreadMessageCount.value++
  // 确保消息数量不超过99
  if (unreadMessageCount.value > 99) {
    unreadMessageCount.value = 100 // 内部存储实际数量，但显示为99+
  }
}

// 标记单个消息为已读
const markMessageAsRead = () => {
  if (unreadMessageCount.value > 0) {
    unreadMessageCount.value--
  }
}

// 初始化获取未读消息数量
const fetchUnreadMessageCount = async () => {
  try {
    const userData = JSON.parse(localStorage.getItem('user') || '{}')
    if (userData.id) {
      const { getUnreadCount } = await import('@/api/user')
      const res = await getUnreadCount(userData.id)
      unreadMessageCount.value = res.data || 0
    }
  } catch (e) {
    const storedCount = localStorage.getItem('unreadMessageCount')
    if (storedCount) {
      unreadMessageCount.value = parseInt(storedCount)
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  min-height: auto;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 25%, #2a2a4a 50%, #1a1a3a 75%, #0a0a1a 100%);
  color: #e0e0e0;
  position: relative;
}

#app::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #0f0f1a;
  z-index: 0;
  pointer-events: none;
}

#app::after {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(ellipse at 20% 30%, rgba(123, 31, 162, 0.1) 0%, transparent 40%),
    radial-gradient(ellipse at 80% 70%, rgba(25, 118, 212, 0.1) 0%, transparent 40%),
    radial-gradient(ellipse at 50% 50%, rgba(0, 188, 212, 0.08) 0%, transparent 40%);
  z-index: 0;
  pointer-events: none;
  animation: backgroundShift 20s ease-in-out infinite;
}

@keyframes backgroundShift {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(10, 10, 26, 0.9);
  backdrop-filter: blur(10px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  transition: opacity 0.3s ease;
}

.loading-spinner {
  width: 60px;
  height: 60px;
  border: 4px solid rgba(123, 31, 162, 0.3);
  border-top: 4px solid #9c27b0;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  box-shadow: 0 0 20px rgba(123, 31, 162, 0.5);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.navbar {
  background: rgba(10, 10, 26, 0.9);
  backdrop-filter: blur(30px);
  border-bottom: 1px solid rgba(123, 31, 162, 0.5);
  position: fixed;
  top: 10px;
  left: 10px;
  right: 10px;
  z-index: 1000;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.5), 0 0 20px rgba(123, 31, 162, 0.2);
  transition: all 0.3s ease;
  border-radius: 0 0 20px 20px;
  min-height: 80px;
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 12px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 15px;
}

.nav-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  font-size: 1.6rem;
  font-weight: 800;
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
}

.nav-logo-text {
  background: linear-gradient(90deg, #9c27b0, #03a9f4, #9c27b0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 30px rgba(123, 31, 162, 0.6);
  position: relative;
  z-index: 2;
  letter-spacing: 1px;
}

.logo-icon {
  font-size: 2.2rem;
  filter: drop-shadow(0 0 20px rgba(123, 31, 162, 0.8));
  animation: pulse 2s infinite;
  position: relative;
  z-index: 2;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1) rotate(0deg);
  }
  50% {
    transform: scale(1.1) rotate(5deg);
    filter: drop-shadow(0 0 30px rgba(123, 31, 162, 1));
  }
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  justify-content: center;
  overflow-x: auto;
  padding: 5px 0;
}

.nav-user {
  display: flex;
  align-items: center;
  gap: 8px;
  overflow-x: auto;
  padding: 5px 0;
}

.nav-link {
  padding: 12px 20px;
  color: #b0b0b0;
  text-decoration: none;
  border-radius: 25px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 0.95rem;
  position: relative;
  white-space: nowrap;
  border: 1px solid transparent;
  background: rgba(255, 255, 255, 0.02);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  height: 40px;
  z-index: 2;
}

/* 移除默认的 ::before 规则，只在 hover 时显示 */

.nav-link:hover::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, rgba(123, 31, 162, 0.1), rgba(25, 118, 210, 0.3), rgba(123, 31, 162, 0.1));
  transform: translateX(0);
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 25px;
  z-index: 0;
}

.nav-link.router-link-active:hover::before,
.nav-link.active:hover::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, rgba(123, 31, 162, 0.2), rgba(25, 118, 210, 0.4), rgba(123, 31, 162, 0.2));
  transform: translateX(0);
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 25px;
  z-index: 0;
}

.nav-link:hover {
  background: rgba(123, 31, 162, 0.1);
  color: #9c27b0;
  box-shadow: 0 4px 20px rgba(123, 31, 162, 0.3);
  border-color: rgba(123, 31, 162, 0.4);
}

.nav-link.router-link-active:hover,
.nav-link.active:hover {
  background: rgba(123, 31, 162, 0.2);
  color: #9c27b0;
  box-shadow: 0 4px 20px rgba(123, 31, 162, 0.4);
  border-color: rgba(123, 31, 162, 0.5);
}

.nav-link.router-link-active,
.nav-link.active {
  background: rgba(123, 31, 162, 0.2);
  color: #9c27b0;
  box-shadow: 0 4px 20px rgba(123, 31, 162, 0.4);
  border-color: rgba(123, 31, 162, 0.5);
}

.nav-link.router-link-active::after,
.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  height: 3px;
  background: linear-gradient(90deg, transparent, #9c27b0, #03a9f4, transparent);
  transition: width 0.3s ease;
  z-index: 1;
}

.nav-link.router-link-active:hover::after,
.nav-link.active:hover::after {
  width: 80%;
}

/* 为所有导航按钮添加hover动画效果 - 排除active状态 */
.nav-link:not(.login-btn):not(.nav-link-guest):not(.logout):not(.router-link-active):not(.active):hover::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  height: 3px;
  background: linear-gradient(90deg, transparent, #9c27b0, #03a9f4, transparent);
  transition: width 0.3s ease;
  z-index: 1;
}

/* 未登录状态下的导航按钮样式 */
.nav-link-guest {
  position: relative;
  cursor: pointer;
}

.nav-link-guest:hover {
  color: #4dd0e1 !important;
  border-color: rgba(77, 208, 225, 0.5) !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 15px rgba(77, 208, 225, 0.3) !important;
}

.nav-link-guest:hover::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  height: 3px;
  background: linear-gradient(90deg, transparent, #4dd0e1, transparent);
  transition: width 0.3s ease;
  z-index: 1;
}

/* 为写博客、关注、消息按钮添加选中状态样式 */
.nav-link-blog.active,
.nav-link-follow.active,
.nav-link-message.active {
  color: #4dd0e1 !important;
  border-color: rgba(77, 208, 225, 0.5) !important;
  box-shadow: 0 4px 15px rgba(77, 208, 225, 0.3) !important;
}

.nav-link-blog.active::after,
.nav-link-follow.active::after,
.nav-link-message.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  height: 3px;
  background: linear-gradient(90deg, transparent, #4dd0e1, transparent);
  transition: width 0.3s ease;
  z-index: 1;
}

/* 通知角标 */
.notification-badge {
  position: absolute;
  top: 0;
  right: 0;
  background: linear-gradient(135deg, #9c27b0, #03a9f4);
  color: white;
  border-radius: 50%;
  width: 16px;
  height: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 0.6rem;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(123, 31, 162, 0.5);
  animation: badgePulse 2s ease-in-out infinite;
  z-index: 10;
  border: 2px solid rgba(15, 15, 35, 0.9);
}

@keyframes badgePulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

.logout {
  background: linear-gradient(90deg, rgba(231, 76, 60, 0.15), rgba(231, 76, 60, 0.25));
  color: #ff6b6b;
  border-color: rgba(231, 76, 60, 0.3);
  cursor: pointer;
  border-radius: 12px;
  padding: 12px 20px;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  border: none;
}

.logout:hover {
  background: linear-gradient(90deg, rgba(231, 76, 60, 0.5), rgba(231, 76, 60, 0.6));
  color: #ff7675;
}

/* 登录/注册按钮样式 */
.login-btn {
  background: linear-gradient(135deg, rgba(123, 31, 162, 0.9), rgba(25, 118, 210, 0.9));
  color: white;
  border-color: rgba(123, 31, 162, 0.5);
  box-shadow: 0 6px 20px rgba(123, 31, 162, 0.4);
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  white-space: nowrap;
  text-overflow: ellipsis;
  cursor: pointer;
  border-radius: 25px;
  padding: 12px 25px;
  font-weight: 600;
  letter-spacing: 1px;
}

.login-btn::before {
  content: none;
}

.login-btn:hover {
  background: linear-gradient(135deg, rgba(123, 31, 162, 1), rgba(25, 118, 210, 1));
  color: #ffffff;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(123, 31, 162, 0.5);
  border-color: rgba(123, 31, 162, 0.8);
}

.login-btn.active {
  background: linear-gradient(135deg, rgba(123, 31, 162, 1), rgba(25, 118, 210, 1));
  color: white;
  box-shadow: 0 4px 20px rgba(123, 31, 162, 0.5);
  border-color: rgba(123, 31, 162, 0.8);
}

.login-btn.active::after {
  content: none;
}

.login-btn.router-link-active {
  color: white !important;
}

/* 回到顶部按钮样式 */
.back-to-top {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #9c27b0 0%, #03a9f4 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 6px 25px rgba(123, 31, 162, 0.4);
  transition: all 0.3s ease;
  z-index: 999;
  font-size: 1.5rem;
  border: none;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.back-to-top:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(123, 31, 162, 0.4);
}

.back-to-top.hidden {
  opacity: 0;
  visibility: hidden;
  transform: translateY(20px);
}

/* 简洁搜索框样式 */
.search-container {
  flex-shrink: 0;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  background: rgba(255, 255, 255, 0.95);
  padding: 0.6rem 1rem;
  border-radius: 50px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 2px solid transparent;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  min-width: 200px;
}

.search-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.search-box.focused {
  border-color: #667eea;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.2);
  transform: translateY(-2px);
}

.search-box.focused::before {
  transform: scaleX(1);
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 0.95rem;
  color: #333;
  padding: 0;
  transition: all 0.3s ease;
}

.search-input::placeholder {
  color: #999;
  transition: color 0.3s ease;
}

.search-box.focused .search-input::placeholder {
  color: #ccc;
}

.search-btn {
  padding: 0.6rem 0.8rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 30px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.search-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.search-btn:active {
  transform: scale(0.95);
}

.main-content {
  min-height: auto;
  position: relative;
  z-index: 1;
  padding-top: 0;
  display: flex;
  flex-direction: column;
  overflow: visible;
}

.page-wrapper {
  flex: 1;
  padding-top: 0;
  min-height: auto;
  box-sizing: border-box;
  overflow: visible;
}

/* 登录/注册页面时主内容区样式 */
.login-register-page .main-content {
  min-height: 100vh;
  overflow: hidden;
}

.footer {
  background: rgba(15, 15, 35, 0.9);
  padding: 40px 20px;
  text-align: center;
  border-top: 1px solid rgba(77, 208, 225, 0.3);
  position: relative;
  z-index: 1;
  border-radius: 20px 20px 0 0;
  margin: 0 10px;
  box-shadow: 0 -4px 30px rgba(0, 0, 0, 0.5), 0 0 20px rgba(123, 31, 162, 0.2);
  backdrop-filter: blur(20px);
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
}

.footer-content p {
  color: #888;
  margin: 10px 0;
  font-size: 0.95rem;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

/* 反馈按钮 */
.feedback-btn {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 90px;
  height: 90px;
  background: linear-gradient(135deg, #9c27b0, #03a9f4);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  box-shadow: 0 6px 25px rgba(123, 31, 162, 0.5);
  transition: all 0.3s ease;
  z-index: 1000;
  animation: float 3s ease-in-out infinite;
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.feedback-btn:hover {
  transform: scale(1.15);
  box-shadow: 0 8px 30px rgba(123, 31, 162, 0.6);
}

.feedback-icon {
  font-size: 1.5rem;
  margin-bottom: 5px;
}

.feedback-text {
  font-size: 0.8rem;
  font-weight: bold;
  color: white;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

/* 反馈表单 */
.feedback-form {
  position: fixed;
  bottom: 140px;
  right: 30px;
  width: 350px;
  background: rgba(10, 10, 26, 0.95);
  border-radius: 15px;
  border: 1px solid rgba(123, 31, 162, 0.4);
  backdrop-filter: blur(15px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3), 0 0 15px rgba(123, 31, 162, 0.1);
  z-index: 1001;
  transition: all 0.3s ease;
  overflow: hidden;
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid rgba(123, 31, 162, 0.3);
  background: rgba(10, 10, 26, 0.95);
}

.feedback-header h3 {
  color: #e0e0e0;
  margin: 0;
  font-size: 1.1rem;
}

.close-btn {
  background: none;
  border: none;
  color: #a0a0a0;
  font-size: 1.5rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.close-btn:hover {
  color: #e0e0e0;
  transform: rotate(90deg);
}

.feedback-content {
  padding: 20px;
}

.feedback-textarea {
  width: 100%;
  min-height: 150px;
  padding: 15px;
  background: rgba(40, 40, 40, 0.9);
  border: 1px solid rgba(123, 31, 162, 0.4);
  border-radius: 10px;
  color: #e0e0e0;
  font-size: 0.9rem;
  resize: vertical;
  outline: none;
  transition: all 0.3s ease;
  margin-bottom: 15px;
}

.feedback-textarea:focus {
  border-color: #9c27b0;
  box-shadow: 0 0 15px rgba(123, 31, 162, 0.3);
  background: rgba(40, 40, 40, 1);
}

.submit-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #9c27b0, #03a9f4);
  border: none;
  border-radius: 10px;
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.submit-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(123, 31, 162, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .feedback-form {
    width: 90%;
    right: 5%;
    left: 5%;
  }
  
  .feedback-btn {
    bottom: 20px;
    right: 20px;
    width: 70px;
    height: 70px;
  }
  
  .feedback-icon {
    font-size: 1.2rem;
  }
  
  .feedback-text {
    font-size: 0.7rem;
  }
}

/* 页面切换蒙层动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
  position: relative;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Toast 提示框样式 */
.toast {
  position: fixed;
  top: 90px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 24px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
  z-index: 10000;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  font-size: 0.95rem;
}

.toast.info {
  background: linear-gradient(135deg, rgba(25, 118, 210, 0.95), rgba(25, 118, 210, 0.85));
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.toast.success {
  background: linear-gradient(135deg, rgba(46, 125, 50, 0.95), rgba(46, 125, 50, 0.85));
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.toast.error {
  background: linear-gradient(135deg, rgba(198, 40, 40, 0.95), rgba(198, 40, 40, 0.85));
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.toast.warning {
  background: linear-gradient(135deg, rgba(123, 31, 162, 0.95), rgba(156, 39, 176, 0.85));
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.toast-icon {
  font-size: 1.2rem;
  font-weight: bold;
}

.toast-message {
  white-space: nowrap;
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(-20px);
}

/* 全局 toast 样式（用于工具函数） */
#global-toast {
  position: fixed;
  top: 90px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 24px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
  z-index: 10000;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  font-size: 0.95rem;
  transition: all 0.3s ease;
}

#global-toast.toast {
  display: flex;
}

#global-toast.toast.hidden {
  display: none;
  opacity: 0;
  transform: translateX(-50%) translateY(-20px);
}

/* 滚动条样式优化 */
.nav-links::-webkit-scrollbar {
  height: 4px;
}

.nav-links::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 2px;
}

.nav-links::-webkit-scrollbar-thumb {
  background: rgba(77, 208, 225, 0.3);
  border-radius: 2px;
}

.nav-links::-webkit-scrollbar-thumb:hover {
  background: rgba(77, 208, 225, 0.5);
}
</style>