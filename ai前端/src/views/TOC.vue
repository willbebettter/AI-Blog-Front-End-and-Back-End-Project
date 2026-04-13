<template>
  <div class="toc-container">
    <div class="toc-header">
      <h2>📑 文章目录</h2>
      <p class="toc-desc">快速导航至文章任意位置</p>
    </div>
    
    <div class="toc-content">
      <div class="toc-list">
        <div v-for="section in sections" :key="section.id" 
             @click="scrollTo(section.id)"
             class="toc-item">
          <span class="toc-number">{{ section.number }}</span>
          <span class="toc-title">{{ section.title }}</span>
          <span class="toc-time">{{ section.time }}</span>
        </div>
      </div>
    </div>
    
    <div class="toc-status">
      <p>当前阅读位置：<span class="current-position">{{ currentPosition }}</span></p>
      <p>阅读进度：<span class="progress">{{ progress }}%</span></p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

// 目录数据
const sections = ref([
  { id: 'intro', number: '01', title: '引言', time: '2 分钟' },
  { id: 'basic', number: '02', title: '基础知识', time: '5 分钟' },
  { id: 'advanced', number: '03', title: '进阶技巧', time: '8 分钟' },
  { id: 'bestpractice', number: '04', title: '最佳实践', time: '6 分钟' },
  { id: 'summary', number: '05', title: '总结', time: '3 分钟' }
])

const currentPosition = ref('引言')
const progress = ref(15)

// 滚动到指定位置
const scrollTo = (id) => {
  console.log('滚动到:', id)
}

onMounted(() => {
  // 加载目录数据
})
</script>

<style scoped>
.toc-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.toc-header {
  text-align: center;
  margin-bottom: 30px;
}

.toc-header h2 {
  font-size: 2rem;
  color: #fff;
  margin-bottom: 10px;
}

.toc-desc {
  color: #999;
  font-size: 1rem;
}

.toc-content {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 15px;
  padding: 30px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.toc-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.toc-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.toc-item:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateX(10px);
  border-color: rgba(77, 208, 225, 0.3);
}

.toc-item.active {
  background: linear-gradient(90deg, rgba(77, 208, 225, 0.15), rgba(77, 208, 225, 0.25));
  border-color: rgba(77, 208, 225, 0.4);
}

.toc-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 35px;
  height: 35px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  font-weight: bold;
  font-size: 1.1rem;
  margin-right: 15px;
}

.toc-title {
  flex: 1;
  color: #e0e0e0;
  font-size: 1.1rem;
}

.toc-time {
  color: #666;
  font-size: 0.85rem;
}

.toc-status {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.toc-status p {
  color: #999;
  margin: 5px 0;
  font-size: 0.95rem;
}

.current-position {
  color: #4dd0e1;
  font-weight: bold;
}

.progress {
  color: #f5576c;
  font-weight: bold;
}
</style>