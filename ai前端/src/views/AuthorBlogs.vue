<template>
  <div class="author-blogs-container">
    <!-- 顶部导航 -->
    <div class="author-header">
      <button class="back-btn" @click="goBack">
        <span class="back-icon">←</span>
        <span class="back-text">返回</span>
      </button>
      
      <div class="author-profile">
        <div class="avatar-container">
          <img :src="author?.avatar || '/images/default-avatar.svg'" :alt="author?.nickname" class="author-avatar" />
          <div class="avatar-glow"></div>
        </div>
        
        <div class="author-info">
          <h1 class="author-name">{{ author?.nickname || '未知作者' }}</h1>
          <p class="author-bio">{{ author?.bio || '该作者还没有添加个人简介' }}</p>
          <div class="author-stats">
            <div class="stat-item">
              <span class="stat-number">{{ blogs.length }}</span>
              <span class="stat-label">博客</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ totalViews }}</span>
              <span class="stat-label">浏览</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ totalLikes }}</span>
              <span class="stat-label">点赞</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 博客列表 -->
    <div class="blogs-section">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-icon">📝</span>
          {{ author?.nickname || '作者' }}的博客
        </h2>
        <div class="section-subtitle">
          分享知识与见解的精彩内容
        </div>
      </div>

      <div v-if="blogs.length > 0" class="blogs-grid">
        <router-link 
          v-for="blog in blogs" 
          :key="blog.id" 
          :to="`/blog/${blog.id}`" 
          class="blog-card"
        >
          <div class="card-header">
            <div class="blog-cover" :style="`background-image: url('${blog.coverImage || '/images/default-cover.svg'}')`">
              <div class="cover-overlay">
                <span class="blog-tag">{{ blog.tags?.[0] || '技术' }}</span>
              </div>
            </div>
          </div>
          
          <div class="card-body">
            <h3 class="blog-title">{{ blog.title }}</h3>
            <p class="blog-summary">{{ blog.summary || '这是一篇精彩的博客内容，快来阅读吧！' }}</p>
            
            <div class="blog-meta">
              <span class="meta-item">
                <i class="meta-icon">📅</i>
                {{ formatTime(blog.createTime) }}
              </span>
              <span class="meta-item">
                <i class="meta-icon">👁️</i>
                {{ blog.views || 0 }}
              </span>
              <span class="meta-item">
                <i class="meta-icon">❤️</i>
                {{ blog.likes || 0 }}
              </span>
            </div>
          </div>
        </router-link>
      </div>
      
      <div v-else class="empty-state">
        <div class="empty-icon">📭</div>
        <h3 class="empty-title">暂无博客</h3>
        <p class="empty-message">该作者还没有发布任何博客</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserInfo } from '@/api/user'
import { getBlogsByAuthor } from '@/api/blog'

const route = useRoute()
const router = useRouter()
const authorId = route.params.id
const author = ref(null)
const blogs = ref([])

const goBack = () => {
  router.back()
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const totalViews = computed(() => {
  return blogs.value.reduce((sum, blog) => sum + (blog.views || 0), 0)
})

const totalLikes = computed(() => {
  return blogs.value.reduce((sum, blog) => sum + (blog.likes || 0), 0)
})

const fetchAuthorData = async () => {
  try {
    const res = await getUserInfo(authorId)
    author.value = res.data
  } catch (error) {
    author.value = { nickname: '未知作者', bio: '' }
  }
}

const fetchAuthorBlogs = async () => {
  try {
    const res = await getBlogsByAuthor(authorId)
    blogs.value = (res.data || []).map(blog => ({
      ...blog,
      tags: blog.tags ? (Array.isArray(blog.tags) ? blog.tags : blog.tags.split(',').map(t => t.trim()).filter(Boolean)) : []
    }))
  } catch (error) {
    blogs.value = []
  }
}

onMounted(() => {
  fetchAuthorData()
  fetchAuthorBlogs()
})
</script>

<style scoped>
.author-blogs-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 25%, #2a2a4a 50%, #1a1a3a 75%, #0a0a1a 100%);
  padding: 30px 20px;
  position: relative;
  overflow-y: auto;
  overflow-x: hidden;
  box-sizing: border-box;
}

.author-blogs-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 30%, rgba(123, 31, 162, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(25, 118, 210, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 50% 80%, rgba(0, 188, 212, 0.1) 0%, transparent 50%);
  z-index: 0;
  pointer-events: none;
}

.author-blogs-container::-webkit-scrollbar {
  width: 8px;
}

.author-blogs-container::-webkit-scrollbar-track {
  background: rgba(10, 10, 26, 0.6);
  border-radius: 4px;
}

.author-blogs-container::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, rgba(77, 208, 225, 0.4), rgba(123, 31, 162, 0.4));
  border-radius: 4px;
  transition: all 0.3s ease;
}

.author-blogs-container::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, rgba(77, 208, 225, 0.6), rgba(123, 31, 162, 0.6));
}

/* 顶部导航 */
.author-header {
  background: rgba(10, 10, 26, 0.9);
  border: 1px solid rgba(123, 31, 162, 0.4);
  border-radius: 24px;
  padding: 30px;
  margin-bottom: 40px;
  display: flex;
  align-items: flex-start;
  gap: 30px;
  position: relative;
  z-index: 1;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.4), 0 0 20px rgba(123, 31, 162, 0.2);
  backdrop-filter: blur(20px);
}

.back-btn {
  background: linear-gradient(135deg, rgba(123, 31, 162, 0.3), rgba(25, 118, 210, 0.3));
  color: #e0e0e0;
  border: 1px solid rgba(123, 31, 162, 0.5);
  padding: 12px 24px;
  border-radius: 28px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.back-btn:hover {
  background: linear-gradient(135deg, rgba(123, 31, 162, 0.5), rgba(25, 118, 210, 0.5));
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(123, 31, 162, 0.4);
  border-color: rgba(123, 31, 162, 0.7);
}

.back-icon {
  font-size: 1.1rem;
  font-weight: bold;
}

.author-profile {
  display: flex;
  align-items: center;
  gap: 30px;
  flex: 1;
}

.avatar-container {
  position: relative;
  flex-shrink: 0;
}

.author-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid rgba(123, 31, 162, 0.6);
  box-shadow: 0 6px 20px rgba(123, 31, 162, 0.4);
  transition: all 0.3s ease;
}

.avatar-container:hover .author-avatar {
  transform: scale(1.05);
  box-shadow: 0 8px 25px rgba(123, 31, 162, 0.5);
}

.avatar-glow {
  position: absolute;
  top: -8px;
  left: -8px;
  right: -8px;
  bottom: -8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #9c27b0, #03a9f4);
  opacity: 0.3;
  animation: glowPulse 3s ease-in-out infinite;
}

@keyframes glowPulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.3;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.5;
  }
}

.author-info {
  flex: 1;
}

.author-name {
  font-size: 2rem;
  font-weight: bold;
  color: #4dd0e1;
  margin: 0 0 12px 0;
  text-shadow: 0 2px 10px rgba(77, 208, 225, 0.3);
}

.author-bio {
  color: #a0a0a0;
  margin: 0 0 20px 0;
  font-size: 1.1rem;
  line-height: 1.5;
  max-width: 600px;
}

.author-stats {
  display: flex;
  gap: 40px;
  padding-top: 15px;
  border-top: 1px solid rgba(123, 31, 162, 0.2);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.stat-number {
  font-size: 1.5rem;
  font-weight: bold;
  color: #4dd0e1;
  text-shadow: 0 0 10px rgba(77, 208, 225, 0.3);
}

.stat-label {
  font-size: 0.9rem;
  color: #808080;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 博客列表 */
.blogs-section {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  margin-bottom: 40px;
  text-align: center;
}

.section-title {
  font-size: 2rem;
  font-weight: bold;
  color: #ffffff;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.4);
}

.title-icon {
  font-size: 1.8rem;
}

.section-subtitle {
  font-size: 1.1rem;
  color: #a0a0a0;
  margin: 0;
}

.blogs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 40px;
}

.blog-card {
  background: rgba(10, 10, 26, 0.85);
  border-radius: 20px;
  overflow: hidden;
  text-decoration: none;
  color: inherit;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(123, 31, 162, 0.4);
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(15px);
  transform: translateY(0);
}

.blog-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.5), 0 0 30px rgba(123, 31, 162, 0.3);
  border-color: #9c27b0;
}

.card-header {
  position: relative;
  overflow: hidden;
}

.blog-cover {
  height: 220px;
  background-size: cover;
  background-position: center;
  transition: transform 0.5s ease;
}

.blog-card:hover .blog-cover {
  transform: scale(1.05);
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(123, 31, 162, 0.3), rgba(25, 118, 210, 0.3));
  display: flex;
  align-items: flex-start;
  padding: 20px;
}

.blog-tag {
  background: linear-gradient(135deg, #9c27b0, #03a9f4);
  color: white;
  padding: 8px 20px;
  border-radius: 25px;
  font-size: 0.85rem;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
}

.card-body {
  padding: 25px;
}

.blog-title {
  font-size: 1.3rem;
  font-weight: bold;
  color: #e0e0e0;
  margin: 0 0 15px 0;
  line-height: 1.4;
  transition: color 0.3s ease;
}

.blog-card:hover .blog-title {
  color: #4dd0e1;
}

.blog-summary {
  color: #a0a0a0;
  margin: 0 0 20px 0;
  font-size: 0.95rem;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.blog-meta {
  display: flex;
  gap: 20px;
  padding-top: 15px;
  border-top: 1px solid rgba(123, 31, 162, 0.2);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.85rem;
  color: #808080;
  transition: color 0.3s ease;
}

.blog-card:hover .meta-item {
  color: #4dd0e1;
}

.meta-icon {
  font-size: 1rem;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #a0a0a0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  background: rgba(10, 10, 26, 0.6);
  border: 2px dashed rgba(123, 31, 162, 0.3);
  border-radius: 20px;
  margin: 40px 0;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-title {
  font-size: 1.5rem;
  font-weight: bold;
  color: #e0e0e0;
  margin: 0 0 10px 0;
}

.empty-message {
  font-size: 1.1rem;
  margin: 0;
  opacity: 0.8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .author-blogs-container {
    padding: 20px 15px;
  }
  
  .author-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 20px;
  }
  
  .author-profile {
    flex-direction: column;
    text-align: center;
  }
  
  .author-stats {
    justify-content: center;
    gap: 30px;
  }
  
  .blogs-grid {
    grid-template-columns: 1fr;
    gap: 30px;
  }
  
  .section-title {
    font-size: 1.8rem;
  }
}
</style>
