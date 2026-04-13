<template>
  <div class="message-list-container">
    <div class="message-header">
      <h1>✉️ 我的消息</h1>
      <div class="message-tabs">
        <button class="tab-btn active">全部</button>
        <button class="tab-btn">未读</button>
        <button class="tab-btn">系统通知</button>
      </div>
    </div>

    <div class="message-content">
      <div v-if="messages.length === 0" class="no-messages">
        <div class="no-message-icon">📭</div>
        <p>暂无消息</p>
      </div>
      
      <div v-else class="message-items">
        <div 
          v-for="msg in messages" 
          :key="msg.id" 
          class="message-item"
          :class="{ unread: !msg.isRead }"
          @click="viewDetail(msg.id)"
        >
          <div class="message-avatar">
            <span class="avatar-icon">{{ msg.senderIcon || '👤' }}</span>
          </div>
          <div class="message-info">
            <div class="message-item-header">
              <h3 class="sender-name">{{ msg.senderName }}</h3>
              <span class="message-time">{{ msg.time }}</span>
            </div>
            <p class="message-preview">{{ msg.content }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNotifications } from '@/api/user'

const router = useRouter()
const messages = ref([])

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const fetchMessages = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!user.id) return
    const res = await getNotifications(user.id)
    messages.value = (res.data || []).map(n => ({
      id: n.id,
      senderName: n.title || '系统通知',
      senderIcon: n.type === 'FOLLOW' ? '👤' : n.type === 'LIKE' ? '❤️' : '📢',
      time: formatTime(n.createTime),
      content: n.content || '',
      isRead: n.isRead
    }))
  } catch (e) {
    console.error('获取消息列表失败', e)
  }
}

const viewDetail = (id) => {
  router.push(`/messages/${id}`)
}

onMounted(() => {
  fetchMessages()
})
</script>

<style scoped>
.message-list-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.message-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message-tabs {
  display: flex;
  gap: 10px;
}

.tab-btn {
  padding: 8px 16px;
  border: none;
  background: #f0f0f0;
  border-radius: 20px;
  cursor: pointer;
}

.tab-btn.active {
  background: #409eff;
  color: white;
}

.message-items {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.message-item {
  display: flex;
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
}

.message-item:hover {
  background: #f9f9f9;
}

.message-item.unread {
  background: #f0f7ff;
}

.message-avatar {
  width: 50px;
  height: 50px;
  background: #e1f5fe;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.avatar-icon {
  font-size: 24px;
}

.message-info {
  flex: 1;
}

.message-item-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.sender-name {
  margin: 0;
  font-size: 16px;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.message-preview {
  margin: 0;
  color: #666;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.no-messages {
  text-align: center;
  padding: 50px;
  color: #999;
}

.no-message-icon {
  font-size: 64px;
  margin-bottom: 10px;
}
</style>
