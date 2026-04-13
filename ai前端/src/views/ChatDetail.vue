<template>
  <div class="chat-detail-container">
    <div class="chat-detail-content">
      <!-- 聊天头部 -->
      <div class="chat-header">
        <button class="back-btn" @click="goBack">← 返回</button>
        <div class="chat-user-info">
          <div class="chat-user-avatar">
            <img :src="chatUser.avatar || '/images/default-avatar.svg'" :alt="chatUser.name" />
          </div>
          <h2 class="chat-user-name">{{ chatUser.name }}</h2>
        </div>
      </div>

      <!-- 聊天消息区 -->
      <div class="chat-messages" ref="messagesContainer">
        <div 
          v-for="(message, index) in messages" 
          :key="index"
          :class="['message-item', message.isMine ? 'message-mine' : 'message-other']"
        >
          <div class="message-avatar">
            <img :src="message.isMine ? (user.avatar || '/images/default-avatar.svg') : (chatUser.avatar || '/images/default-avatar.svg')" :alt="message.isMine ? user.nickname : chatUser.name" />
          </div>
          <div class="message-content">
            <div class="message-bubble" :class="message.isMine ? 'message-bubble-mine' : 'message-bubble-other'">
              {{ message.content }}
            </div>
            <div class="message-time">{{ formatTime(message.time) }}</div>
          </div>
        </div>
      </div>

      <!-- 消息输入区 -->
      <div class="chat-input-area">
        <textarea 
          v-model="messageInput"
          placeholder="输入消息..."
          class="chat-input"
          @keyup.enter.exact="sendMessage"
          @keyup.enter.shift="() => {}"
        ></textarea>
        <button @click="sendMessage" class="send-btn">发送</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const chatId = computed(() => route.params.id)

const messagesContainer = ref(null)
const messageInput = ref('')
const messages = ref([])
const chatUser = ref({
  id: 1,
  name: '张三',
  avatar: ''
})

const user = computed(() => {
  const userData = JSON.parse(localStorage.getItem('user') || '{}')
  return Object.keys(userData).length > 0 ? userData : null
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleTimeString()
}

const goBack = () => {
  router.back()
}

const loadMessages = async () => {
  messages.value = []
  try {
    const { getUserInfo } = await import('@/api/user')
    const res = await getUserInfo(chatId.value)
    if (res.data) {
      chatUser.value = {
        id: res.data.id,
        name: res.data.nickname || res.data.username,
        avatar: res.data.avatar || ''
      }
    }
  } catch (e) {
    console.error('获取聊天用户信息失败', e)
  }
}

const sendMessage = () => {
  if (!messageInput.value.trim()) return

  const newMessage = {
    content: messageInput.value.trim(),
    time: new Date().toISOString(),
    isMine: true
  }

  messages.value.push(newMessage)
  messageInput.value = ''
  scrollToBottom()
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

watch(messages, () => {
  scrollToBottom()
}, { deep: true })

onMounted(() => {
  if (!user.value) {
    router.push('/login')
    return
  }
  loadMessages()
  scrollToBottom()
})
</script>

<style scoped>
.chat-detail-container {
  height: 100vh;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 25%, #2a2a4a 50%, #1a1a3a 75%, #0a0a1a 100%);
  padding: 0;
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
}

.chat-detail-container::before {
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

.chat-detail-content {
  width: 100%;
  height: 100%;
  background: rgba(10, 10, 26, 0.85);
  border: 1px solid rgba(123, 31, 162, 0.4);
  backdrop-filter: blur(15px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3), 0 0 15px rgba(123, 31, 162, 0.1);
  position: relative;
  z-index: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.chat-header {
  padding: 15px 20px;
  border-bottom: 1px solid rgba(123, 31, 162, 0.3);
  background: rgba(10, 10, 26, 0.95);
  display: flex;
  align-items: center;
  gap: 15px;
  flex-shrink: 0;
  min-height: 70px;
  box-sizing: border-box;
}

.back-btn {
  background: rgba(77, 208, 225, 0.2);
  color: #4dd0e1;
  border: 1px solid rgba(77, 208, 225, 0.3);
  padding: 8px 15px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: rgba(77, 208, 225, 0.3);
}

.chat-user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.chat-user-avatar img {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(123, 31, 162, 0.5);
}

.chat-user-name {
  font-size: 1.2rem;
  font-weight: bold;
  color: #e0e0e0;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
  margin: 0;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 15px;
  /* 隐藏滚动条但保留滚动功能 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}

/* 隐藏滚动条但保留滚动功能 */
.chat-messages::-webkit-scrollbar {
  display: none;
}

.message-item {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.message-mine {
  flex-direction: row-reverse;
}

.message-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(123, 31, 162, 0.5);
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 0.95rem;
  line-height: 1.4;
}

.message-bubble-mine {
  background: linear-gradient(135deg, #9c27b0, #03a9f4);
  color: white;
  border-bottom-right-radius: 4px;
  align-self: flex-end;
}

.message-bubble-other {
  background: rgba(40, 40, 40, 0.8);
  color: #e0e0e0;
  border-bottom-left-radius: 4px;
  align-self: flex-start;
}

.message-time {
  font-size: 0.8rem;
  color: #888;
  align-self: flex-end;
}

.chat-input-area {
  padding: 15px 20px;
  border-top: 1px solid rgba(123, 31, 162, 0.3);
  background: rgba(10, 10, 26, 0.95);
  display: flex;
  gap: 12px;
  align-items: stretch;
  flex-shrink: 0;
}

.chat-input {
  flex: 1;
  padding: 12px 18px;
  border: 2px solid rgba(123, 31, 162, 0.4);
  border-radius: 24px;
  background: rgba(30, 30, 50, 0.9);
  color: #e0e0e0;
  font-size: 0.95rem;
  resize: none;
  min-height: 48px;
  max-height: 100px;
  outline: none;
  transition: all 0.3s ease;
  line-height: 1.5;
}

.chat-input:focus {
  border-color: #9c27b0;
  box-shadow: 0 0 15px rgba(156, 39, 176, 0.3);
  background: rgba(30, 30, 50, 1);
}

.send-btn {
  padding: 0 28px;
  min-height: 48px;
  background: linear-gradient(135deg, #9c27b0 0%, #e040fb 50%, #03a9f4 100%);
  border: none;
  border-radius: 24px;
  color: white;
  font-weight: 700;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(156, 39, 176, 0.4);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  letter-spacing: 1px;
}

.send-btn:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 8px 30px rgba(156, 39, 176, 0.5);
}

.send-btn:active {
  transform: translateY(0) scale(0.98);
}
</style>