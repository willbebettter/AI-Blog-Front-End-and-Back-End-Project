<template>
  <div class="chat-container">
    <div class="chat-header">
      <h1>💬 聊天室</h1>
      <div class="chat-status">
        <span class="status-indicator"></span>
        <span>在线用户</span>
      </div>
    </div>

    <div class="chat-content">
      <!-- 用户列表 -->
      <div class="user-list">
        <div class="user-list-header">
          <h3>👥 在线用户</h3>
          <div class="user-filters">
            <button class="filter-btn active" @click="filterUsers('all')">全部</button>
            <button class="filter-btn" @click="filterUsers('followed')">关注</button>
            <button class="filter-btn" @click="filterUsers('online')">在线</button>
          </div>
        </div>
        
        <div class="users-grid">
          <div 
            v-for="user in filteredUsers" 
            :key="user.id"
            class="user-card"
            :class="{ online: user.status === 'online' }"
            @click="selectUser(user)"
          >
            <div class="user-avatar" :style="`background-image: url('${user.avatar || '/default-avatar.svg'}')`">
              <span class="user-initial">{{ user.nickname?.charAt(0) || 'U' }}</span>
            </div>
            <div class="user-info">
              <h4 class="user-name">{{ user.nickname || '用户' }}</h4>
              <p class="user-status">{{ user.status === 'online' ? '在线' : '离线' }}</p>
            </div>
            <div v-if="user.status === 'online'" class="online-indicator"></div>
          </div>
        </div>
      </div>

      <!-- 聊天区域 -->
      <div class="chat-area" v-if="selectedUser">
        <div class="chat-info">
          <div class="chat-header-info">
            <h3>{{ selectedUser.nickname || '用户' }}</h3>
            <span class="chat-status-text">{{ selectedUser.status === 'online' ? '在线' : '离线' }}</span>
          </div>
          <div class="chat-messages">
            <div 
              v-for="message in messages" 
              :key="message.id"
              class="message"
              :class="{ 
                own: message.senderId === currentUser.id,
                system: message.type === 'system'
              }"
            >
              <div v-if="message.type !== 'system'" class="message-content">
                <div class="message-avatar" :style="`background-image: url('${message.senderAvatar || '/default-avatar.png'}')`">
                  <span class="message-initial">{{ message.senderNickname?.charAt(0) || 'U' }}</span>
                </div>
                <div class="message-body">
                  <div class="message-header">
                    <span class="message-name">{{ message.senderNickname || '用户' }}</span>
                    <span class="message-time">{{ formatTime(message.createTime) }}</span>
                  </div>
                  <div class="message-text">{{ message.content }}</div>
                </div>
              </div>
              <div v-else class="system-message">
                {{ message.content }}
              </div>
            </div>
          </div>
        </div>

        <div class="chat-input">
          <input 
            type="text" 
            v-model="newMessage"
            @keyup.enter="sendMessage"
            placeholder="输入消息..."
            class="message-input"
          />
          <button class="send-btn" @click="sendMessage">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="22" y1="2" x2="11" y2="13"></line>
              <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
            </svg>
          </button>
        </div>
      </div>

      <div v-else class="no-chat-selected">
        <div class="no-chat-content">
          <div class="no-chat-icon">💬</div>
          <h3>选择一位用户开始聊天</h3>
          <p>点击左侧用户列表中的在线用户即可开始聊天</p>
          <div class="chat-tips">
            <h4>💡 聊天提示</h4>
            <ul>
              <li>只能与关注的好友聊天</li>
              <li>消息实时推送</li>
              <li>支持表情和图片</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import api from '@/utils/api'

const currentUser = ref({})
const users = ref([])
const selectedUser = ref(null)
const messages = ref([])
const newMessage = ref('')
const filter = ref('all')

const filteredUsers = computed(() => {
  if (filter.value === 'all') return users.value
  if (filter.value === 'followed') return users.value.filter(u => isFollowing(u.id))
  if (filter.value === 'online') return users.value.filter(u => u.status === 'online')
  return users.value
})

const isFollowing = (userId) => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  // TODO: 调用 API 获取关注列表
  return false
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const selectUser = (user) => {
  selectedUser.value = user
  messages.value = []
  
  // 获取聊天历史
  fetchChatHistory(user.id)
  
  // 连接 WebSocket
  connectWebSocket(user.id)
}

const fetchChatHistory = async (userId) => {
  try {
    const response = await api.get(`/chat/history?userId=${currentUser.value.id}&targetId=${userId}`)
    if (response.code === 200) {
      messages.value = response.data || []
    }
  } catch (error) {
    console.error('获取聊天历史失败:', error)
  }
}

const sendMessage = async () => {
  if (!newMessage.value.trim() || !selectedUser.value) return
  
  try {
    const response = await api.post('/chat/send', {
      targetId: selectedUser.value.id,
      content: newMessage.value
    })
    if (response.code === 200) {
      newMessage.value = ''
      // 添加本地消息
      messages.value.push({
        id: Date.now(),
        senderId: currentUser.value.id,
        senderNickname: currentUser.value.nickname,
        senderAvatar: currentUser.value.avatar,
        content: newMessage.value,
        type: 'own'
      })
    }
  } catch (error) {
    console.error('发送消息失败:', error)
  }
}

const connectWebSocket = (userId) => {
  // TODO: 实现 WebSocket 连接
  console.log('连接 WebSocket 到用户:', userId)
  // 这里需要后端 WebSocket 支持
  // const socket = new WebSocket(`ws://localhost:8080/chat/${userId}`)
  // socket.onmessage = (event) => {
  //   const data = JSON.parse(event.data)
  //   messages.value.push(data)
  // }
}

const filterUsers = (newFilter) => {
  filter.value = newFilter
}

const getUser = async () => {
  try {
    const response = await api.get('/user/me')
    if (response.code === 200) {
      currentUser.value = response.data
    }
  } catch (error) {
    console.error('获取当前用户失败:', error)
  }
}

const fetchUsers = async () => {
  try {
    const response = await api.get('/chat/users')
    if (response.code === 200) {
      users.value = response.data || []
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
}

onMounted(() => {
  getUser()
  fetchUsers()
})

onUnmounted(() => {
  // 断开 WebSocket 连接
})
</script>

<style scoped>
.chat-container {
  height: 100vh;
  background: linear-gradient(135deg, #1a1a3e 0%, #2d2d5a 50%, #1a1a3e 100%);
  color: #fff;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.chat-header {
  max-width: 1400px;
  margin: 0 auto 2rem;
  padding: 0 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-header h1 {
  font-size: 2rem;
  color: #4dd0e1;
  margin: 0;
}

.chat-status {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #888;
  font-size: 0.9rem;
}

.status-indicator {
  width: 10px;
  height: 10px;
  background: #55efc4;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.chat-content {
  flex: 1;
  padding: 0 1rem;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 1rem;
  overflow: hidden;
  min-height: 0;
}

.user-list {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  padding: 1.5rem;
  border: 1px solid rgba(77, 208, 225, 0.1);
  display: flex;
  flex-direction: column;
}

.user-list-header {
  margin-bottom: 1.5rem;
}

.user-list-header h3 {
  color: #4dd0e1;
  margin-bottom: 0.5rem;
}

.user-filters {
  display: flex;
  gap: 0.5rem;
}

.filter-btn {
  flex: 1;
  padding: 0.5rem;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 8px;
  color: #888;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.85rem;
}

.filter-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.filter-btn.active {
  background: rgba(77, 208, 225, 0.3);
  color: #4dd0e1;
}

.users-grid {
  flex: 1;
  overflow-y: auto;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.user-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 1rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(77, 208, 225, 0.1);
  position: relative;
}

.user-card:hover {
  background: rgba(77, 208, 225, 0.15);
  transform: translateY(-3px);
}

.user-card.online {
  border-color: #55efc4;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-size: cover;
  background-position: center;
  margin: 0 auto 0.8rem;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-initial {
  font-size: 1.5rem;
  font-weight: bold;
  color: #fff;
}

.user-info {
  margin-bottom: 0.5rem;
}

.user-name {
  color: #4dd0e1;
  font-size: 0.95rem;
  margin: 0;
}

.user-status {
  color: #666;
  font-size: 0.8rem;
  margin: 0.2rem 0 0;
}

.online-indicator {
  position: absolute;
  bottom: 5px;
  right: 5px;
  width: 12px;
  height: 12px;
  background: #55efc4;
  border: 2px solid #1a1a3e;
  border-radius: 50%;
}

.chat-area {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  padding: 1.5rem;
  border: 1px solid rgba(77, 208, 225, 0.1);
  display: flex;
  flex-direction: column;
}

.chat-info {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 1rem;
}

.chat-header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.chat-header-info h3 {
  color: #4dd0e1;
  margin: 0;
}

.chat-status-text {
  color: #666;
  font-size: 0.85rem;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding-right: 5px;
}

.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(77, 208, 225, 0.3);
  border-radius: 3px;
}

.message {
  display: flex;
  gap: 1rem;
}

.message.own {
  flex-direction: row-reverse;
}

.message-content {
  display: flex;
  gap: 0.8rem;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-size: cover;
  background-position: center;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.message-body {
  background: rgba(255, 255, 255, 0.05);
  padding: 0.8rem 1rem;
  border-radius: 12px;
  border: 1px solid rgba(77, 208, 225, 0.1);
  max-width: 70%;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.message-name {
  color: #4dd0e1;
  font-size: 0.9rem;
  font-weight: 600;
}

.message-time {
  color: #666;
  font-size: 0.75rem;
}

.message-text {
  color: #ccc;
  line-height: 1.5;
  word-break: break-word;
}

.system-message {
  align-self: center;
  background: rgba(77, 208, 225, 0.1);
  padding: 0.5rem 1rem;
  border-radius: 20px;
  color: #4dd0e1;
  font-size: 0.85rem;
  border: 1px solid rgba(77, 208, 225, 0.2);
}

.chat-input {
  display: flex;
  gap: 1rem;
}

.message-input {
  flex: 1;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(77, 208, 225, 0.3);
  border-radius: 25px;
  padding: 0.8rem 1.2rem;
  color: #fff;
  outline: none;
  font-size: 0.95rem;
}

.message-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.message-input:focus {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(77, 208, 225, 0.5);
}

.send-btn {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #4dd0e1, #00bcd4);
  border: none;
  border-radius: 50%;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.send-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 5px 15px rgba(77, 208, 225, 0.4);
}

.no-chat-selected {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.no-chat-content {
  text-align: center;
  max-width: 400px;
}

.no-chat-icon {
  font-size: 4rem;
  margin-bottom: 1.5rem;
}

.no-chat-content h3 {
  color: #4dd0e1;
  margin-bottom: 0.5rem;
}

.no-chat-content p {
  color: #888;
  margin-bottom: 2rem;
}

.chat-tips {
  background: rgba(255, 255, 255, 0.05);
  padding: 1.5rem;
  border-radius: 12px;
  border: 1px solid rgba(77, 208, 225, 0.1);
}

.chat-tips h4 {
  color: #4dd0e1;
  margin-bottom: 1rem;
}

.chat-tips ul {
  color: #888;
  padding-left: 1.2rem;
  line-height: 2;
}

.chat-tips li {
  margin-bottom: 0.5rem;
}

@media (max-width: 1024px) {
  .chat-content {
    grid-template-columns: 1fr;
  }
  
  .user-list {
    max-height: 300px;
  }
}

@media (max-width: 768px) {
  .users-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .chat-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
}
</style>
