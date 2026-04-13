<template>
  <div class="following-container">
    <h1 class="page-title">
      <span class="title-icon">🔗</span>
      关注的博主最新博客
    </h1>
    
    <div v-if="sortedBlogs.length > 0" class="blog-list">
      <div v-for="blog in sortedBlogs" :key="blog.id" class="blog-item">
        <div class="blog-header">
          <div class="author-info">
            <img :src="blog.authorAvatar || '/images/default-avatar.svg'" :alt="blog.authorName" class="author-avatar" />
            <div class="author-details">
              <span class="author-name">{{ blog.authorName }}</span>
              <span class="blog-time">{{ formatDate(blog.createTime) }}</span>
            </div>
          </div>
        </div>
        <h3 class="blog-title">{{ blog.title }}</h3>
        <p class="blog-summary">{{ blog.summary }}</p>
        <div class="blog-stats">
          <span class="stat-item">
            <span class="stat-icon">👁</span>
            <span class="stat-value">{{ blog.views }}</span>
          </span>
          <span class="stat-item">
            <span class="stat-icon">❤</span>
            <span class="stat-value">{{ blog.likes }}</span>
          </span>
        </div>
        <button @click="viewBlog(blog)" class="view-btn">查看详情</button>
      </div>
    </div>
    
    <div v-else class="empty-state">
      <div class="empty-icon">🔗</div>
      <p>暂无关注的博主的最新博客</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getFollowedBlogs } from '@/api/user'

const router = useRouter()

const latestBlogs = ref([])

const sortedBlogs = computed(() => {
  return [...latestBlogs.value].sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
})

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

const viewBlog = (blog) => {
  router.push(`/blog/${blog.id}`)
}

const fetchFollowedBlogs = async () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (!user.id) return
  try {
    const res = await getFollowedBlogs(user.id)
    latestBlogs.value = (res.data || []).map(blog => ({
      ...blog,
      authorName: blog.author?.nickname || '作者',
      authorAvatar: blog.author?.avatar || ''
    }))
  } catch (e) {
    console.error('获取关注博客失败', e)
  }
}

onMounted(() => {
  fetchFollowedBlogs()
})
</script>

<style scoped>
.following-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 25%, #2a2a4a 50%, #1a1a3a 75%, #0a0a1a 100%);
  padding: 120px 20px 40px;
  position: relative;
  color: #fff;
  overflow: hidden;
}

.following-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 30%, rgba(123, 31, 162, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(25, 118, 210, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(0, 188, 212, 0.1) 0%, transparent 50%),
    linear-gradient(45deg, transparent 49%, rgba(123, 31, 162, 0.1) 50%, transparent 51%),
    linear-gradient(-45deg, transparent 49%, rgba(25, 118, 210, 0.1) 50%, transparent 51%);
  z-index: 0;
  pointer-events: none;
}

.page-title {
  font-size: 2.5rem;
  font-weight: bold;
  text-align: center;
  margin-bottom: 3rem;
  color: white;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  animation: fadeIn 0.5s ease-out;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.title-icon {
  font-size: 2rem;
  animation: pulse 2s ease-in-out infinite;
}

.blog-list {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.blog-item {
  padding: 2rem;
  background: rgba(40, 40, 40, 0.6);
  border-radius: 16px;
  border: 1px solid rgba(123, 31, 162, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.blog-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent, rgba(123, 31, 162, 0.05), transparent);
  transform: translateX(-100%);
  transition: transform 0.5s ease;
}

.blog-item:hover::before {
  transform: translateX(100%);
}

.blog-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(123, 31, 162, 0.3);
  border-color: rgba(123, 31, 162, 0.5);
}

.blog-header {
  margin-bottom: 1rem;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.author-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 2px solid white;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.blog-item:hover .author-avatar {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(123, 31, 162, 0.4);
}

.author-details {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-size: 1rem;
  font-weight: bold;
  color: #4dd0e1;
  text-shadow: 0 0 12px rgba(77, 208, 225, 0.4);
}

.blog-time {
  font-size: 0.85rem;
  color: #888;
  text-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
}

.blog-title {
  font-size: 1.4rem;
  font-weight: bold;
  color: #e0e0e0;
  margin-bottom: 1rem;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
  line-height: 1.4;
}

.blog-summary {
  font-size: 1rem;
  color: #a0a0a0;
  margin-bottom: 1.5rem;
  line-height: 1.6;
  text-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
}

.blog-stats {
  display: flex;
  gap: 2rem;
  margin-bottom: 1.5rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: #888;
  text-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
}

.stat-icon {
  font-size: 1.1rem;
}

.stat-value {
  font-weight: bold;
  color: #e0e0e0;
}

.view-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 25px;
  padding: 0.8rem 2rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.95rem;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.view-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.empty-state {
  text-align: center;
  padding: 4rem 1rem;
  color: #999;
  max-width: 600px;
  margin: 0 auto;
}

.empty-icon {
  font-size: 4rem;
  display: block;
  margin-bottom: 1rem;
  opacity: 0.5;
  animation: pulse 2s ease-in-out infinite;
}

.empty-state p {
  font-size: 1.1rem;
  margin: 0;
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .following-container {
    padding: 100px 15px 30px;
  }
  
  .page-title {
    font-size: 2rem;
    margin-bottom: 2rem;
  }
  
  .blog-item {
    padding: 1.5rem;
  }
  
  .blog-title {
    font-size: 1.2rem;
  }
  
  .blog-summary {
    font-size: 0.95rem;
  }
}
</style>