<template>
  <div class="vote-container">
    <div class="vote-header">
      <h2>📊 投票系统</h2>
      <p class="vote-desc">参与投票，发表您的意见</p>
    </div>
    
    <div class="vote-list">
      <div v-for="vote in votes" :key="vote.id" class="vote-card">
        <h3>{{ vote.title }}</h3>
        <p class="vote-desc">{{ vote.description }}</p>
        
        <div class="vote-options">
          <div v-for="option in vote.options" :key="option.id" class="option-card">
            <div class="option-header">
              <span class="option-title">{{ option.title }}</span>
              <span class="option-votes">{{ option.votes }} 票</span>
            </div>
            <div class="option-progress">
              <div class="progress-bar" :style="{ width: option.percentage + '%', background: option.color }"></div>
            </div>
            <button @click="voteOption(vote.id, option.id)" class="vote-btn">
              {{ isVoted ? '已投票' : '投票' }}
            </button>
          </div>
        </div>
        
        <div v-if="vote.isFinished" class="vote-result">
          <p>投票已结束</p>
          <button @click="viewResult(vote.id)" class="result-btn">查看结果</button>
        </div>
      </div>
    </div>
    
    <div class="vote-footer">
      <button @click="createVote" class="create-btn">发起新投票</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { showToast } from '@/utils/toast'

// 投票数据
const votes = ref([
  {
    id: 1,
    title: '前端框架选择',
    description: '您更喜欢哪个前端框架？',
    options: [
      { id: 1, title: 'Vue.js', votes: 156, percentage: 45, color: '#42b883' },
      { id: 2, title: 'React', votes: 120, percentage: 34, color: '#61dafb' },
      { id: 3, title: 'Angular', votes: 60, percentage: 18, color: '#ff6d00' },
      { id: 4, title: 'Svelte', votes: 44, percentage: 13, color: '#ff3e00' }
    ],
    isFinished: false
  },
  {
    id: 2,
    title: '博客主题颜色',
    description: '您更喜欢什么主题颜色？',
    options: [
      { id: 1, title: '深色', votes: 89, percentage: 38, color: '#2d3436' },
      { id: 2, title: '浅色', votes: 112, percentage: 48, color: '#f5f6fa' },
      { id: 3, title: '护眼', votes: 43, percentage: 14, color: '#67c4e9' }
    ],
    isFinished: false
  }
])

const isVoted = ref(false)

// 投票
const voteOption = (voteId, optionId) => {
  if (isVoted.value) {
    showToast('您已经投票过了', 'warning')
    return
  }
  
  const vote = votes.value.find(v => v.id === voteId)
  if (vote) {
    const option = vote.options.find(o => o.id === optionId)
    if (option) {
      option.votes++
      option.percentage = Math.round((option.votes / votes.value[0].options.reduce((sum, o) => sum + o.votes, 0)) * 100)
      isVoted.value = true
      showToast('投票成功', 'success')
    }
  }
}

// 创建投票
const createVote = () => {
  showToast('创建投票功能开发中...', 'info')
}

// 查看结果
const viewResult = (voteId) => {
  showToast('查看结果功能开发中...', 'info')
}

onMounted(() => {
  // 加载投票数据
})
</script>

<style scoped>
.vote-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.vote-header {
  text-align: center;
  margin-bottom: 40px;
}

.vote-header h2 {
  font-size: 2rem;
  color: #fff;
  margin-bottom: 10px;
}

.vote-desc {
  color: #999;
  font-size: 1rem;
}

.vote-list {
  display: grid;
  gap: 30px;
}

.vote-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  padding: 30px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.vote-card h3 {
  font-size: 1.5rem;
  color: #4dd0e1;
  margin-bottom: 15px;
}

.vote-card .vote-desc {
  color: #999;
  margin-bottom: 25px;
}

.vote-options {
  display: grid;
  gap: 20px;
}

.option-card {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 10px;
  padding: 20px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: all 0.3s ease;
}

.option-card:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-2px);
}

.option-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.option-title {
  font-size: 1.1rem;
  color: #e0e0e0;
}

.option-votes {
  background: rgba(77, 208, 225, 0.2);
  color: #4dd0e1;
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 0.9rem;
}

.option-progress {
  height: 8px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 15px;
}

.progress-bar {
  height: 100%;
  border-radius: 4px;
  transition: width 0.5s ease;
}

.vote-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.vote-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.vote-btn:disabled {
  background: rgba(255, 255, 255, 0.1);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.vote-result {
  text-align: center;
  padding: 20px;
  background: rgba(77, 208, 225, 0.1);
  border-radius: 10px;
  margin-top: 20px;
}

.vote-result p {
  color: #4dd0e1;
  margin-bottom: 15px;
}

.result-btn {
  padding: 10px 30px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.result-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(79, 172, 254, 0.4);
}

.create-btn {
  width: 100%;
  padding: 15px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 1.1rem;
  transition: all 0.3s ease;
  margin-top: 20px;
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 87, 108, 0.4);
}
</style>