<template>
  <div class="rss-container">
    <div class="rss-header">
      <h2>📰 RSS 订阅配置</h2>
      <p class="rss-desc">订阅您喜欢的博客和内容源</p>
    </div>
    
    <div class="rss-form">
      <div class="form-group">
        <label>订阅地址</label>
        <input type="text" v-model="rssUrl" placeholder="输入 RSS 订阅地址" />
      </div>
      
      <div class="form-group">
        <label>订阅名称</label>
        <input type="text" v-model="rssName" placeholder="输入订阅名称" />
      </div>
      
      <div class="form-group">
        <label>描述</label>
        <textarea v-model="rssDesc" placeholder="输入订阅描述"></textarea>
      </div>
      
      <div class="form-group">
        <label>自动更新</label>
        <div class="toggle-group">
          <label class="toggle">
            <input type="checkbox" v-model="settings.autoUpdate" />
            <span class="toggle-slider"></span>
            自动更新订阅内容
          </label>
        </div>
      </div>
      
      <div class="form-group">
        <label>通知新内容</label>
        <div class="toggle-group">
          <label class="toggle">
            <input type="checkbox" v-model="settings.notify" />
            <span class="toggle-slider"></span>
            有新内容时通知
          </label>
        </div>
      </div>
    </div>
    
    <div class="rss-subscriptions">
      <h3>已订阅的内容</h3>
      <div v-for="subscription in subscriptions" :key="subscription.id" 
           class="subscription-card">
        <div class="card-header">
          <h4>{{ subscription.name }}</h4>
          <button @click="removeSubscription(subscription.id)" class="remove-btn">
            ✕
          </button>
        </div>
        <div class="card-body">
          <p>{{ subscription.desc }}</p>
          <p class="update-time">上次更新：{{ subscription.lastUpdate }}</p>
          <p class="article-count">文章数量：{{ subscription.articleCount }}</p>
        </div>
        <div class="card-footer">
          <button @click="refreshSubscription(subscription.id)" class="refresh-btn">
            🔄 刷新
          </button>
          <button @click="viewSubscription(subscription.id)" class="view-btn">
            查看
          </button>
        </div>
      </div>
    </div>
    
    <button @click="addSubscription" class="add-btn">添加订阅</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast } from '@/utils/toast'

// RSS 订阅数据
const subscriptions = ref([
  {
    id: 1,
    url: 'https://example.com/rss',
    name: 'Vue 官方文档',
    desc: 'Vue.js 官方文档和教程',
    lastUpdate: '2024-01-15 10:30',
    articleCount: 156
  },
  {
    id: 2,
    url: 'https://react.dev/rss',
    name: 'React 官方博客',
    desc: 'React 官方博客和更新',
    lastUpdate: '2024-01-15 09:15',
    articleCount: 89
  }
])

const rssUrl = ref('')
const rssName = ref('')
const rssDesc = ref('')
const settings = ref({
  autoUpdate: true,
  notify: false
})

// 添加订阅
const addSubscription = () => {
  if (!rssUrl.value || !rssName.value) {
    showToast('请填写订阅地址和名称', 'warning')
    return
  }
  
  const newSubscription = {
    id: Date.now(),
    url: rssUrl.value,
    name: rssName.value,
    desc: rssDesc.value || '暂无描述',
    lastUpdate: new Date().toLocaleString('zh-CN'),
    articleCount: 0
  }
  
  subscriptions.value.unshift(newSubscription)
  
  // 清空表单
  rssUrl.value = ''
  rssName.value = ''
  rssDesc.value = ''
}

// 移除订阅
const removeSubscription = (id) => {
  subscriptions.value = subscriptions.value.filter(s => s.id !== id)
}

// 刷新订阅
const refreshSubscription = (id) => {
  console.log('刷新订阅:', id)
}

// 查看订阅
const viewSubscription = (id) => {
  console.log('查看订阅:', id)
}

onMounted(() => {
  // 加载 RSS 订阅配置
})
</script>

<style scoped>
.rss-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.rss-header {
  text-align: center;
  margin-bottom: 30px;
}

.rss-header h2 {
  font-size: 2rem;
  color: #fff;
  margin-bottom: 10px;
}

.rss-desc {
  color: #999;
  font-size: 1rem;
}

.rss-form {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 15px;
  padding: 30px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 25px;
}

.form-group label {
  display: block;
  color: #e0e0e0;
  margin-bottom: 10px;
  font-size: 1rem;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 15px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  color: #e0e0e0;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #4dd0e1;
  background: rgba(255, 255, 255, 0.08);
}

.form-group textarea {
  min-height: 100px;
  resize: vertical;
}

.toggle-group {
  display: flex;
  align-items: center;
  gap: 15px;
}

.toggle {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 32px;
}

.toggle input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.1);
  transition: .4s;
  border-radius: 32px;
}

.toggle-slider:before {
  position: absolute;
  content: "";
  height: 26px;
  width: 26px;
  left: 2px;
  bottom: 2px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .toggle-slider {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

input:checked + .toggle-slider:before {
  transform: translateX(28px);
}

.rss-subscriptions {
  margin-bottom: 30px;
}

.rss-subscriptions h3 {
  color: #4dd0e1;
  margin-bottom: 20px;
}

.subscription-card {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 15px;
  padding: 25px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.subscription-card:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-3px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.card-header h4 {
  color: #e0e0e0;
  font-size: 1.2rem;
}

.remove-btn {
  width: 35px;
  height: 35px;
  background: rgba(231, 76, 60, 0.8);
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  transition: all 0.3s ease;
}

.remove-btn:hover {
  background: rgba(231, 76, 60, 1);
  transform: scale(1.1);
}

.card-body {
  color: #999;
  margin-bottom: 15px;
  line-height: 1.6;
}

.update-time,
.article-count {
  margin: 5px 0;
  font-size: 0.9rem;
}

.card-footer {
  display: flex;
  gap: 15px;
}

.refresh-btn,
.view-btn {
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.1);
  color: #999;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.refresh-btn:hover,
.view-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.add-btn {
  width: 100%;
  padding: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 1.1rem;
  transition: all 0.3s ease;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}
</style>