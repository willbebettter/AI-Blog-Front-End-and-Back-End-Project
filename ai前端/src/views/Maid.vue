<template>
  <div class="maid-container">
    <div class="maid-header">
      <h2>🤖 看板娘</h2>
      <p class="maid-desc">智能助手，为您提供贴心服务</p>
    </div>
    
    <div class="maid-display">
      <div class="maid-avatar">
        <span class="maid-icon">👩‍💻</span>
      </div>
      <div class="maid-message">
        <p class="message-text">{{ message }}</p>
        <p class="message-time">{{ currentTime }}</p>
      </div>
    </div>
    
    <div class="maid-controls">
      <h3>消息提示</h3>
      <div class="message-tips">
        <p>📅 每日问候：{{ dailyGreeting }}</p>
        <p>🎉 节日祝福：{{ festivalGreeting }}</p>
        <p>💡 技术提示：{{ techTip }}</p>
      </div>
    </div>
    
    <div class="maid-models">
      <h3>看板娘模型</h3>
      <div class="model-cards">
        <div v-for="model in models" :key="model.id" 
             @click="changeModel(model.id)"
             :class="{ active: currentModel === model.id }"
             class="model-card">
          <span class="model-icon">{{ model.icon }}</span>
          <span class="model-name">{{ model.name }}</span>
        </div>
      </div>
    </div>
    
    <div class="maid-voice">
      <h3>语音互动</h3>
      <p class="voice-desc">支持语音互动（需要浏览器支持 Web Speech API）</p>
      <button @click="speakMessage" class="voice-btn">
        <span class="voice-icon">🔊</span>
        <span class="voice-text">播放语音</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { showToast } from '@/utils/toast'

// 看板娘数据
const models = ref([
  { id: 1, name: '默认', icon: '👩‍💻' },
  { id: 2, name: '程序员', icon: '👨‍💻' },
  { id: 3, name: '设计师', icon: '👩‍🎨' },
  { id: 4, name: '运维', icon: '👨‍🔧' }
])

const currentModel = ref(1)
const message = ref('你好！我是看板娘，有什么可以帮你的吗？')
const currentTime = ref('')

// 每日问候
const dailyGreeting = ref('')
const festivalGreeting = ref('')
const techTip = ref('')

// 当前时间
const updateCurrentTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

// 更新每日问候
const updateDailyGreeting = () => {
  const now = new Date()
  const day = now.getDay()
  const dayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  dailyGreeting.value = `今天是${dayNames[day]}，祝你${day === 0 ? '周末愉快！' : '工作顺利！'}`
}

// 更新节日祝福
const updateFestivalGreeting = () => {
  const now = new Date()
  const month = now.getMonth() + 1
  const day = now.getDate()
  const festivals = [
    { month: 1, day: 1, greeting: '新年快乐！' },
    { month: 3, day: 8, greeting: '妇女节快乐！' },
    { month: 5, day: 1, greeting: '劳动节快乐！' },
    { month: 10, day: 1, greeting: '国庆节快乐！' }
  ]
  const festival = festivals.find(f => f.month === month && f.day === day)
  if (festival) {
    festivalGreeting.value = festival.greeting
  }
}

// 更新技术提示
const updateTechTip = () => {
  const tips = [
    '记得多喝水，保护嗓子哦！',
    '长时间工作记得休息眼睛',
    '今天也要保持好心情！',
    '代码写得不错，继续加油！'
  ]
  techTip.value = tips[Math.floor(Math.random() * tips.length)]
}

// 切换模型
const changeModel = (id) => {
  currentModel.value = id
  const modelsData = models.value.find(m => m.id === id)
  message.value = `切换到${modelsData.name}模式！有什么可以帮你的？`
}

// 播放语音
const speakMessage = () => {
  if ('speechSynthesis' in window) {
    const utterance = new SpeechSynthesisUtterance(message.value)
    utterance.lang = 'zh-CN'
    window.speechSynthesis.speak(utterance)
  } else {
    showToast('您的浏览器不支持语音功能', 'warning')
  }
}

// 初始化
onMounted(() => {
  updateCurrentTime()
  updateDailyGreeting()
  updateFestivalGreeting()
  updateTechTip()
  
  // 每 5 分钟更新技术提示
  setInterval(updateTechTip, 300000)
})
</script>

<style scoped>
.maid-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.maid-header {
  text-align: center;
  margin-bottom: 30px;
}

.maid-header h2 {
  font-size: 2rem;
  color: #fff;
  margin-bottom: 10px;
}

.maid-desc {
  color: #999;
  font-size: 1rem;
}

.maid-display {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  border-radius: 20px;
  padding: 40px;
  text-align: center;
  margin-bottom: 30px;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.maid-avatar {
  width: 150px;
  height: 150px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 5rem;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
}

.maid-message {
  color: #fff;
}

.message-text {
  font-size: 1.2rem;
  margin-bottom: 10px;
  line-height: 1.6;
}

.message-time {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
}

.maid-controls,
.maid-models,
.maid-voice {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 15px;
  padding: 25px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 20px;
}

.maid-controls h3,
.maid-models h3,
.maid-voice h3 {
  color: #4dd0e1;
  margin-bottom: 15px;
}

.message-tips {
  color: #999;
  line-height: 2;
}

.model-cards {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.model-card {
  padding: 20px 30px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  text-align: center;
}

.model-card:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-5px);
}

.model-card.active {
  background: linear-gradient(135deg, rgba(77, 208, 225, 0.2), rgba(77, 208, 225, 0.3));
  border-color: rgba(77, 208, 225, 0.4);
}

.model-icon {
  display: block;
  font-size: 2.5rem;
  margin-bottom: 10px;
}

.model-name {
  color: #e0e0e0;
  font-size: 1rem;
}

.voice-desc {
  color: #999;
  margin-bottom: 15px;
}

.voice-btn {
  width: 100%;
  padding: 15px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 1.1rem;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.voice-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(79, 172, 254, 0.4);
}

.voice-icon {
  font-size: 1.5rem;
}
</style>