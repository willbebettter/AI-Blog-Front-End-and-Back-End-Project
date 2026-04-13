<template>
  <div class="chat-list-container">
    <div class="chat-list-content">
      <div class="page-header">
        <h2 class="page-title">📱 消息列表</h2>
        <button v-if="hasUnreadMessages" class="mark-all-read-btn" @click="markAllMessagesAsRead">
          一键已读
        </button>
      </div>

      <!-- 消息列表 -->
      <div class="chat-list">
        <div 
          v-for="chat in chats" 
          :key="chat.id" 
          class="chat-item"
          @click="openChat(chat)"
        >
          <div class="chat-avatar">
            <img :src="chat.avatar || '/images/default-avatar.svg'" :alt="chat.name" />
            <span v-if="chat.unread > 0" class="unread-badge">{{ chat.unread }}</span>
          </div>
          <div class="chat-info">
            <div class="chat-header">
              <h3 class="chat-name">{{ chat.name }}</h3>
              <span class="chat-time">{{ formatTime(chat.lastMessage.time) }}</span>
            </div>
            <p class="chat-last-message">{{ chat.lastMessage.content }}</p>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="chats.length === 0" class="empty-state">
        <h3>暂无消息</h3>
        <p>开始与其他用户聊天吧</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getFollowing } from '@/api/user'

const router = useRouter()
const chats = ref([])

const user = computed(() => {
  const userData = JSON.parse(localStorage.getItem('user') || '{}')
  return Object.keys(userData).length > 0 ? userData : null
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleTimeString()
}

const openChat = (chat) => {
  if (chat.unread > 0) {
    chat.unread = 0
  }
  router.push(`/chat/${chat.id}`)
}

const hasUnreadMessages = computed(() => {
  return chats.value.some(chat => chat.unread > 0)
})

const markAllMessagesAsRead = () => {
  chats.value.forEach(chat => {
    chat.unread = 0
  })
}

const loadChats = async () => {
  try {
    const userData = JSON.parse(localStorage.getItem('user') || '{}')
    if (!userData.id) return
    const res = await getFollowing(userData.id)
    chats.value = (res.data || []).map(author => ({
      id: author.id,
      name: author.nickname || author.username,
      avatar: author.avatar || '',
      lastMessage: {
        content: '点击开始聊天',
        time: new Date().toISOString()
      },
      unread: 0
    }))
  } catch (e) {
    console.error('获取聊天列表失败', e)
  }
}

onMounted(() => {
  if (!user.value) {
    router.push('/login')
    return
  }
  loadChats()
})
</script>

<style scoped>
.chat-list-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 25%, #2a2a4a 50%, #1a1a3a 75%, #0a0a1a 100%);
  padding: 100px 20px 20px;
  position: relative;
  overflow: auto;
  box-sizing: border-box;
}

.chat-list-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 30%, rgba(123, 31, 162, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(25, 118, 210, 0.15) 0%, transparent 50%);
  z-index: 0;
  pointer-events: none;
}

.breadcrumb {
  padding: 15px 0;
  font-size: 0.9rem;
  color: #a0a0a0;
  position: relative;
  z-index: 1;
}

.breadcrumb-item {
  color: #4dd0e1;
  text-decoration: none;
}

.breadcrumb-item:hover {
  text-decoration: underline;
}

.current {
  color: #e0e0e0;
}

.chat-list-content {
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.page-title {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 30px;
  background: linear-gradient(90deg, #9c27b0, #03a9f4, #9c27b0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 30px rgba(123, 31, 162, 0.6);
  letter-spacing: 2px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.mark-all-read-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #9c27b0, #03a9f4);
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.mark-all-read-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(123, 31, 162, 0.4);
}

.mark-all-read-btn:active {
  transform: translateY(0);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.chat-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.chat-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: rgba(10, 10, 26, 0.85);
  border-radius: 15px;
  border: 1px solid rgba(123, 31, 162, 0.4);
  backdrop-filter: blur(15px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3), 0 0 15px rgba(123, 31, 162, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.chat-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, #9c27b0, #03a9f4, transparent);
  z-index: 1;
}

.chat-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.4), 0 0 20px rgba(123, 31, 162, 0.2);
  border-color: rgba(123, 31, 162, 0.6);
}

.chat-avatar {
  position: relative;
  flex-shrink: 0;
}

.chat-avatar img {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(123, 31, 162, 0.5);
}

.unread-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: linear-gradient(135deg, #9c27b0, #03a9f4);
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 0.8rem;
  font-weight: bold;
  box-shadow: 0 2px 10px rgba(123, 31, 162, 0.5);
}

.chat-info {
  flex: 1;
  min-width: 0;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.chat-name {
  font-size: 1.1rem;
  font-weight: bold;
  color: #e0e0e0;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

.chat-time {
  font-size: 0.8rem;
  color: #888;
}

.chat-last-message {
  font-size: 0.9rem;
  color: #a0a0a0;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #a0a0a0;
}

.empty-state h3 {
  font-size: 1.5rem;
  margin-bottom: 10px;
  color: #e0e0e0;
}

.empty-state p {
  font-size: 1rem;
  color: #888;
}
</style>