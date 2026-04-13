<template>
  <div class="message-detail-container">
    <div class="detail-header">
      <button class="back-btn" @click="$router.back()">⬅️ 返回</button>
      <h2>消息详情</h2>
    </div>

    <div class="message-card">
      <div class="message-info">
        <div class="sender">
          <span class="sender-label">来自：</span>
          <span class="sender-name">{{ message.senderName }}</span>
        </div>
        <div class="time">
          <span class="time-label">时间：</span>
          <span>{{ message.time }}</span>
        </div>
      </div>
      
      <div class="message-body">
        <p>{{ message.content }}</p>
      </div>

      <div class="message-actions">
        <button class="action-btn reply">回复</button>
        <button class="action-btn delete">删除</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNotifications, markNotificationRead } from '@/api/user'

const route = useRoute()
const router = useRouter()
const message = ref({
  senderName: '',
  time: '',
  content: ''
})

const fetchMessage = async () => {
  const id = route.params.id
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!user.id) return
    const res = await getNotifications(user.id)
    const notifications = res.data || []
    const notification = notifications.find(n => n.id === Number(id))
    if (notification) {
      message.value = {
        senderName: notification.title || '系统通知',
        time: notification.createTime ? new Date(notification.createTime).toLocaleString() : '',
        content: notification.content || ''
      }
      if (!notification.isRead) {
        await markNotificationRead(notification.id)
      }
    }
  } catch (e) {
    console.error('获取消息详情失败', e)
  }
}

onMounted(() => {
  fetchMessage()
})
</script>

<style scoped>
.message-detail-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.back-btn {
  padding: 8px 15px;
  border: none;
  background: #f0f0f0;
  border-radius: 4px;
  cursor: pointer;
}

.message-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.message-info {
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  color: #666;
}

.sender-name {
  color: #333;
  font-weight: bold;
}

.message-body {
  font-size: 16px;
  line-height: 1.6;
  min-height: 200px;
  color: #333;
}

.message-actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.action-btn {
  padding: 10px 25px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.reply {
  background: #409eff;
  color: white;
}

.delete {
  background: #f56c6c;
  color: white;
}
</style>
