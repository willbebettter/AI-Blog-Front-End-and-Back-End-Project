<template>
    <header class="header" :class="{ 'header-scrolled': isScrolled }">
        <div class="container header-content">
            <div class="logo">
                <router-link to="/" class="logo-link">
                    <span class="logo-icon">🌌</span>
                    <span class="logo-text">StellarBlog</span>
                </router-link>
            </div>

            <!-- 简洁搜索框 -->
            <div class="search-section">
                <div class="search-wrapper" :class="{ 'search-focused': isSearchFocused }">
                    <input 
                        type="text" 
                        placeholder="搜索文章、标签或作者..." 
                        v-model="searchQuery" 
                        @keyup.enter="performSearch"
                        @focus="isSearchFocused = true"
                        @blur="isSearchFocused = false"
                        class="search-input"
                    >
                    <button @click="performSearch" class="search-btn">
                        <span class="search-icon">🔍</span>
                    </button>
                </div>
            </div>

            <nav class="nav-links">
                <!-- 消息组件 -->
                <div class="message-container" v-if="userIsLoggedIn">
                    <button @click="toggleMessageDropdown" class="nav-item message-btn">
                        <span class="nav-icon">💬</span>
                        <span class="nav-text">消息</span>
                        <span v-if="unreadCount > 0" class="unread-badge" :class="{ 'pulse': unreadCount > 0 }">{{ unreadCount }}</span>
                    </button>
                    <div v-if="showMessageDropdown" class="message-dropdown">
                        <div class="dropdown-header">
                            <h3>消息中心</h3>
                            <button @click="markAllAsRead" class="clear-btn">一键清空</button>
                        </div>
                        <div class="message-list">
                            <div v-for="message in messages" :key="message.id" class="message-item" :class="{ 'unread': !message.read }">
                                <div class="message-content">
                                    <h4 class="message-title">{{ message.title }}</h4>
                                    <p class="message-body">{{ message.body }}</p>
                                    <span class="message-time">{{ message.time }}</span>
                                </div>
                                <button @click="markAsRead(message.id)" class="read-btn" v-if="!message.read">标为已读</button>
                            </div>
                            <div v-if="messages.length === 0" class="empty-messages">
                                <p>暂无消息</p>
                            </div>
                        </div>
                    </div>
                </div>
                <router-link to="/user-center" class="nav-item" v-if="userIsLoggedIn">
                    <span class="nav-icon">👤</span>
                    <span class="nav-text">用户中心</span>
                </router-link>
                <router-link to="/login" class="nav-item" v-else>
                    <span class="nav-icon">🔑</span>
                    <span class="nav-text">登录</span>
                </router-link>
                <button @click="logout" v-if="userIsLoggedIn" class="btn-logout">
                    <span class="logout-icon">🚪</span>
                    <span class="logout-text">退出</span>
                </button>
            </nav>
        </div>
    </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router'
import { getNotifications, getUnreadCount, markNotificationRead, markAllNotificationsRead } from '@/api/user'

const router = useRouter()

const userIsLoggedIn = computed(() => {
  return !!JSON.parse(localStorage.getItem('user') || '{}').id
})
const searchQuery = ref('');
const isSearchFocused = ref(false);
const isScrolled = ref(false);

const showMessageDropdown = ref(false);
const messages = ref([]);

const unreadCount = computed(() => {
    return messages.value.filter(message => !message.isRead).length;
});

const fetchNotifications = async () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (!user.id) return
  try {
    const res = await getNotifications(user.id)
    messages.value = (res.data || []).map(n => ({
      id: n.id,
      title: n.title || '通知',
      body: n.content || '',
      time: formatTime(n.createTime),
      read: n.isRead,
      isRead: n.isRead
    }))
  } catch (e) {
    console.error('获取通知失败', e)
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / (1000 * 60))
  if (minutes < 60) return `${minutes}分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  return `${days}天前`
}

const toggleMessageDropdown = () => {
    showMessageDropdown.value = !showMessageDropdown.value;
};

const markAsRead = async (id) => {
    const message = messages.value.find(msg => msg.id === id);
    if (message) {
        message.read = true;
        message.isRead = true;
        try {
          await markNotificationRead(id)
        } catch (e) {
          console.error('标记已读失败', e)
        }
    }
};

const markAllAsRead = async () => {
    messages.value.forEach(message => {
        message.read = true;
        message.isRead = true;
    });
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (user.id) {
      try {
        await markAllNotificationsRead(user.id)
      } catch (e) {
        console.error('标记全部已读失败', e)
      }
    }
};

const performSearch = () => {
    if (searchQuery.value.trim()) {
      router.push(`/blog?keyword=${encodeURIComponent(searchQuery.value.trim())}`)
    }
};

const logout = () => {
    localStorage.removeItem('user')
    router.push('/')
    window.location.reload()
};

const handleClickOutside = (event) => {
    const messageContainer = document.querySelector('.message-container');
    if (messageContainer && !messageContainer.contains(event.target)) {
        showMessageDropdown.value = false;
    }
};

const handleScroll = () => {
    isScrolled.value = window.scrollY > 50;
};

onMounted(() => {
    window.addEventListener('scroll', handleScroll);
    document.addEventListener('click', handleClickOutside);
    fetchNotifications()
});

onUnmounted(() => {
    window.removeEventListener('scroll', handleScroll);
    document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
/* 使用全局定义的 CSS 变量 */
.header {
    width: 100%;
    padding: 0 20px;
    box-sizing: border-box;
    background: rgba(10, 10, 26, 0.9);
    backdrop-filter: blur(30px);
    border-bottom: 1px solid rgba(156, 39, 176, 0.5);
    position: sticky;
    top: 0;
    z-index: 1000;
    box-shadow: 0 4px 30px rgba(0, 0, 0, 0.5), 0 0 20px rgba(156, 39, 176, 0.2);
    transition: all 0.3s ease;
    height: 80px;
    border-radius: 0 0 20px 20px;
    margin: 10px;
}

/* 滚动时的头部样式 */
.header-scrolled {
    height: 70px;
    background: rgba(10, 10, 26, 0.95);
    box-shadow: 0 4px 30px rgba(0, 0, 0, 0.6), 0 0 20px rgba(156, 39, 176, 0.2);
}

.header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 30px;
    max-width: 1400px;
    margin: 0 auto;
    height: 100%;
    transition: all 0.3s ease;
}

/* Logo 样式 */
.logo-link {
    display: flex;
    align-items: center;
    gap: 12px;
    text-decoration: none;
    font-size: 1.8rem;
    font-weight: 800;
    transition: all 0.3s ease;
    position: relative;
    z-index: 2;
}

.logo-icon {
    font-size: 2.2rem;
    filter: drop-shadow(0 0 20px rgba(156, 39, 176, 0.8));
    animation: pulse 2s infinite;
    position: relative;
    z-index: 2;
}

.logo-text {
    background: linear-gradient(90deg, var(--color-accent-primary), var(--color-accent-secondary));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    text-shadow: 0 0 30px rgba(156, 39, 176, 0.6);
    position: relative;
    z-index: 2;
    letter-spacing: 1px;
    transition: all 0.3s ease;
}

.logo-link:hover .logo-text {
    text-shadow: 0 0 40px rgba(156, 39, 176, 0.8);
}

.logo-link:hover .logo-icon {
    transform: scale(1.1) rotate(5deg);
    filter: drop-shadow(0 0 30px rgba(156, 39, 176, 1));
}

/* 简洁搜索区域 */
.search-section {
    display: flex;
    align-items: center;
    justify-content: center;
    flex: 1;
    max-width: 400px;
}

.search-wrapper {
    display: flex;
    align-items: center;
    gap: 0;
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 30px;
    overflow: hidden;
    transition: all 0.3s ease;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.search-wrapper.search-focused {
    border-color: var(--color-accent-primary);
    box-shadow: 0 6px 20px rgba(0, 188, 212, 0.3);
    transform: translateY(-2px);
}

/* 优化的搜索框 */
.search-input {
    padding: 12px 20px;
    border: none;
    background: transparent;
    color: var(--color-text-primary);
    outline: none;
    transition: all 0.3s ease;
    width: 280px;
    font-size: 0.95rem;
    flex: 1;
}

.search-input::placeholder {
    color: var(--color-text-tertiary);
    transition: color 0.3s ease;
}

.search-wrapper.search-focused .search-input::placeholder {
    color: var(--color-text-secondary);
}

.search-btn {
    padding: 12px 20px;
    background: linear-gradient(90deg, var(--color-accent-primary), var(--color-accent-secondary));
    border: none;
    border-radius: 0 30px 30px 0;
    color: white;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
}

.search-btn:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 15px rgba(0, 188, 212, 0.4);
}

.search-icon {
    font-size: 1.1rem;
}

.nav-links {
    display: flex;
    align-items: center;
    gap: 10px;
}

.nav-item {
    display: flex;
    align-items: center;
    gap: 8px;
    color: var(--color-text-primary);
    text-decoration: none;
    padding: 12px 20px;
    border-radius: 25px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    font-size: 0.95rem;
    position: relative;
    overflow: hidden;
    border: 1px solid transparent;
    background: rgba(255, 255, 255, 0.02);
    backdrop-filter: blur(10px);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    z-index: 2;
}

/* 移除默认的 ::before 规则，只在 hover 时显示 */

.nav-item:hover::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, rgba(156, 39, 176, 0.1), rgba(0, 188, 212, 0.3), rgba(156, 39, 176, 0.1));
    transform: translateX(0);
    transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    z-index: 0;
}

.nav-item.router-link-active:hover::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, rgba(156, 39, 176, 0.2), rgba(0, 188, 212, 0.4), rgba(156, 39, 176, 0.2));
    transform: translateX(0);
    transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    z-index: 0;
}

.nav-item:hover {
    background: rgba(156, 39, 176, 0.1);
    color: var(--color-accent-primary);
    transform: translateY(-2px);
    box-shadow: 0 4px 20px rgba(156, 39, 176, 0.3);
    border-color: rgba(156, 39, 176, 0.4);
}

.nav-item.router-link-active:hover {
    background: rgba(156, 39, 176, 0.2);
    color: var(--color-accent-primary);
    box-shadow: 0 4px 20px rgba(156, 39, 176, 0.4);
    border-color: rgba(156, 39, 176, 0.5);
}

.nav-item.router-link-active {
    background: rgba(156, 39, 176, 0.2);
    color: var(--color-accent-primary);
    box-shadow: 0 4px 20px rgba(156, 39, 176, 0.4);
    border-color: rgba(156, 39, 176, 0.5);
}

.nav-item.router-link-active::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 80%;
    height: 3px;
    background: linear-gradient(90deg, transparent, var(--color-accent-primary), var(--color-accent-secondary), transparent);
    transition: width 0.3s ease, left 0.3s ease;
    z-index: 1;
}

.nav-item.router-link-active:hover::after {
    width: 80%;
}

/* 为所有导航按钮添加hover动画效果 - 排除active状态 */
.nav-item:not(.router-link-active):hover::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 80%;
    height: 3px;
    background: linear-gradient(90deg, transparent, var(--color-accent-primary), var(--color-accent-secondary), transparent);
    transition: width 0.3s ease, left 0.3s ease;
    z-index: 1;
}

.nav-icon {
    font-size: 1.1rem;
    transition: transform 0.3s ease;
}

.nav-item:hover .nav-icon {
    transform: scale(1.2) rotate(5deg);
}

.btn-logout {
    display: flex;
    align-items: center;
    gap: 8px;
    background: linear-gradient(90deg, rgba(231, 76, 60, 0.15), rgba(231, 76, 60, 0.25));
    color: #ff6b6b;
    padding: 12px 20px;
    border-radius: 12px;
    border: 1px solid rgba(231, 76, 60, 0.3);
    cursor: pointer;
    font-size: 0.95rem;
    transition: all 0.3s ease;
    box-shadow: 0 2px 10px rgba(231, 76, 60, 0.2);
}

.btn-logout:hover {
    background: linear-gradient(90deg, rgba(231, 76, 60, 0.3), rgba(231, 76, 60, 0.4));
    color: #ff7675;
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(231, 76, 60, 0.3);
    border-color: rgba(231, 76, 60, 0.5);
}

.logout-icon {
    font-size: 1.1rem;
    transition: transform 0.3s ease;
}

.btn-logout:hover .logout-icon {
    transform: scale(1.2) rotate(5deg);
}

/* 消息组件 */
.message-container {
    position: relative;
}

.message-btn {
    position: relative;
}

.unread-badge {
    position: absolute;
    top: 5px;
    right: 5px;
    background: linear-gradient(90deg, #ff4757, #ff6b7a);
    color: white;
    border-radius: 50%;
    width: 20px;
    height: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.8rem;
    font-weight: bold;
    box-shadow: 0 2px 10px rgba(255, 71, 87, 0.5);
}

.unread-badge.pulse {
    animation: pulse 2s infinite;
}

.message-dropdown {
    position: absolute;
    top: 100%;
    right: 0;
    width: 350px;
    background: rgba(10, 10, 26, 0.95);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(123, 31, 162, 0.3);
    border-radius: 16px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
    margin-top: 10px;
    z-index: 1001;
    max-height: 400px;
    overflow-y: auto;
}

.dropdown-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    border-bottom: 1px solid rgba(123, 31, 162, 0.3);
}

.dropdown-header h3 {
    margin: 0;
    color: var(--color-text-primary);
    font-size: 1.1rem;
}

.clear-btn {
    background: linear-gradient(90deg, #667eea, #764ba2);
    color: white;
    border: none;
    border-radius: 20px;
    padding: 5px 15px;
    font-size: 0.8rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
}

.clear-btn:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.message-list {
    padding: 10px 0;
}

.message-item {
    padding: 15px 20px;
    border-bottom: 1px solid rgba(123, 31, 162, 0.1);
    transition: all 0.3s ease;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
}

.message-item:hover {
    background: rgba(123, 31, 162, 0.1);
}

.message-item.unread {
    background: rgba(123, 31, 162, 0.05);
    border-left: 4px solid var(--color-accent-primary);
}

.message-content {
    flex: 1;
}

.message-title {
    margin: 0 0 5px 0;
    color: var(--color-text-primary);
    font-size: 1rem;
    font-weight: 600;
}

.message-body {
    margin: 0 0 10px 0;
    color: var(--color-text-secondary);
    font-size: 0.9rem;
    line-height: 1.4;
}

.message-time {
    color: var(--color-text-tertiary);
    font-size: 0.8rem;
}

.read-btn {
    background: linear-gradient(90deg, #4dd0e1, #00bcd4);
    color: white;
    border: none;
    border-radius: 15px;
    padding: 5px 10px;
    font-size: 0.8rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    margin-left: 10px;
    flex-shrink: 0;
}

.read-btn:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 15px rgba(77, 208, 225, 0.4);
}

.empty-messages {
    text-align: center;
    padding: 30px 20px;
    color: var(--color-text-tertiary);
}

/* 响应式设计 */
@media (max-width: 768px) {
    .header {
        padding: 0 15px;
        height: 70px;
    }
    
    .header-scrolled {
        height: 60px;
    }
    
    .header-content {
        gap: 15px;
    }
    
    .logo-link {
        font-size: 1.4rem;
    }
    
    .logo-icon {
        font-size: 1.8rem;
    }
    
    .search-section {
        max-width: 250px;
    }
    
    .search-input {
        width: 180px;
        padding: 10px 15px;
        font-size: 0.85rem;
    }
    
    .search-btn {
        padding: 10px 15px;
    }
    
    .nav-item {
        padding: 10px 15px;
        font-size: 0.85rem;
    }
    
    .btn-logout {
        padding: 10px 15px;
        font-size: 0.85rem;
    }
    
    .nav-text, .logout-text {
        display: none;
    }
    
    .nav-item, .btn-logout {
        gap: 0;
        padding: 10px;
        border-radius: 50%;
        width: 40px;
        height: 40px;
        justify-content: center;
    }
    
    .message-dropdown {
        width: 300px;
        right: -50px;
    }
    
    .unread-badge {
        top: 0;
        right: 0;
        width: 16px;
        height: 16px;
        font-size: 0.7rem;
    }
}

/* 动画 */
@keyframes pulse {
    0%, 100% {
        transform: scale(1) rotate(0deg);
    }
    50% {
        transform: scale(1.1) rotate(5deg);
        filter: drop-shadow(0 0 30px rgba(156, 39, 176, 1));
    }
}
</style>