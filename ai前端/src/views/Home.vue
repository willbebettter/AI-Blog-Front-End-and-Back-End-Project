<template>
  <div class="home-container">
    <!-- 欢迎信息 -->
    <section class="welcome-section">
      <div class="welcome-content fade-in">
        <div class="welcome-header">
          <h1 class="welcome-title">
            <span class="title-icon">🚀</span>
            <span class="title-text">欢迎来到 <span class="gradient-text">StellarBlog</span></span>
          </h1>
          <p class="welcome-subtitle">探索知识的宇宙，分享每一个精彩瞬间</p>
        </div>
        <div class="welcome-description">
          <p>这是一个基于 Vue 3 + Spring Boot 的现代化博客系统</p>
          <p>包含博客管理、用户系统、关注管理、实时聊天等功能</p>
        </div>
        <div class="welcome-actions">
          <router-link to="/blog" class="cta-btn primary">
            <span class="btn-icon">📖</span>
            开始浏览
          </router-link>
          <router-link to="/blog/create" class="cta-btn secondary">
            <span class="btn-icon">✏️</span>
            写博客
          </router-link>
        </div>
        <div class="welcome-stats">
          <div class="stat-item">
            <span class="stat-number">{{ stats.blogs }}</span>
            <span class="stat-label">博客文章</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ stats.users }}</span>
            <span class="stat-label">注册用户</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ stats.comments }}</span>
            <span class="stat-label">互动评论</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 特色功能 -->
    <section class="features-section">
      <h2 class="section-title">
        <span class="section-icon">✨</span>
        系统特色
      </h2>
      <div class="features-grid">
        <div class="feature-card fade-in" v-for="(feature, index) in features" :key="index" :style="{ animationDelay: `${index * 0.1}s` }">
          <div class="feature-icon">{{ feature.icon }}</div>
          <h3 class="feature-title">{{ feature.title }}</h3>
          <p class="feature-description">{{ feature.description }}</p>
          <div class="feature-glow"></div>
        </div>
      </div>
    </section>

    <!-- 热门博客 -->
    <section class="popular-section">
      <h2 class="section-title">
        <span class="section-icon">🔥</span>
        热门博客
      </h2>
      <div class="popular-grid" v-if="popularBlogs.length > 0">
        <router-link 
          v-for="(blog, index) in popularBlogs" 
          :key="blog.id"
          :to="`/blog/${blog.id}`"
          class="popular-card fade-in"
          :style="{ animationDelay: `${index * 0.1}s` }"
        >
          <div class="popular-cover" :style="`background-image: url('${blog.coverImage || '/images/default-cover.svg'}')`">
            <span class="popular-tag" :style="{ backgroundColor: getTagColor(blog.tags?.[0] || 'tech') }">{{ blog.tags?.[0] || 'tech' }}</span>
            <div class="popular-cover-overlay"></div>
          </div>
          <div class="popular-content">
            <h3 class="popular-title">{{ blog.title }}</h3>
            <p class="popular-summary">{{ blog.summary || '暂无摘要...' }}</p>
            <div class="popular-meta">
              <div class="meta-item">
                <span class="meta-icon">👤</span>
                <span class="meta-text">{{ blog.author?.nickname || '作者' }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-icon">📅</span>
                <span class="meta-text">{{ formatTime(blog.createTime) }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-icon">👁️</span>
                <span class="meta-text">{{ blog.views || 0 }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-icon">❤️</span>
                <span class="meta-text">{{ blog.likes || 0 }}</span>
              </div>
            </div>
          </div>
          <div class="popular-glow"></div>
        </router-link>
      </div>
      <div class="empty-state" v-else>
        <span class="empty-icon">📭</span>
        <p class="empty-text">暂无博客数据</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onActivated } from 'vue'
import { getPopularBlogs, getBlogList } from '@/api/blog'
import { showToast } from '@/utils/toast'

const popularBlogs = ref([])
const stats = ref({
  blogs: 0,
  users: 0,
  comments: 0
})

const features = ref([
  {
    icon: '📝',
    title: '博客管理',
    description: '创建、编辑、删除博客文章，支持富文本编辑'
  },
  {
    icon: '👤',
    title: '用户系统',
    description: '注册、登录、个人中心，完善的用户管理功能'
  },
  {
    icon: '📊',
    title: '数据统计',
    description: '浏览量、点赞数统计，了解文章受欢迎程度'
  },
  {
    icon: '💬',
    title: '实时聊天',
    description: '与关注的好友实时交流，分享想法和见解'
  },
  {
    icon: '📚',
    title: '关注管理',
    description: '查看关注博主的所有博客，不错过任何更新'
  },
  {
    icon: '🔍',
    title: '智能搜索',
    description: '快速找到感兴趣的内容，支持标签和关键词搜索'
  }
])

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString()
}

const getTagColor = (tag) => {
  const colors = {
    'tech': 'linear-gradient(90deg, #4dd0e1, #00bcd4)',
    'life': 'linear-gradient(90deg, #f093fb, #f5576c)',
    'code': 'linear-gradient(90deg, #f5a623, #f7d794)',
    'design': 'linear-gradient(90deg, #74b9ff, #0984e3)',
    'share': 'linear-gradient(90deg, #55efc4, #00b894)',
    'other': 'linear-gradient(90deg, #95a5a6, #7f8c8d)'
  }
  return colors[tag] || colors['tech']
}

const fetchPopularBlogs = async () => {
  try {
    const res = await getBlogList({ page: 1, size: 6, sortBy: 'views', order: 'DESC' })
    popularBlogs.value = (res.data?.content || []).map(blog => ({
      ...blog,
      tags: blog.tags ? (Array.isArray(blog.tags) ? blog.tags : blog.tags.split(',').map(t => t.trim()).filter(Boolean)) : []
    }))
  } catch (error) {
    console.error('获取热门博客失败:', error)
  }
}

const fetchStats = async () => {
  try {
    const res = await getBlogStats()
    if (res.data) {
      animateStats(res.data)
    }
  } catch (e) {
    console.error('获取统计信息失败', e)
  }
}

const animateStats = (targets) => {
  const duration = 2000
  const startTime = Date.now()
  
  const updateStats = () => {
    const elapsed = Date.now() - startTime
    const progress = Math.min(elapsed / duration, 1)
    
    stats.value.blogs = Math.floor(progress * (targets.blogs || 0))
    stats.value.users = Math.floor(progress * (targets.users || 0))
    stats.value.comments = Math.floor(progress * (targets.comments || 0))
    
    if (progress < 1) {
      requestAnimationFrame(updateStats)
    }
  }
  
  requestAnimationFrame(updateStats)
}

onMounted(() => {
  fetchPopularBlogs()
  fetchStats()
})

onActivated(() => {
  fetchPopularBlogs()
  fetchStats()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 25%, #2a2a4a 50%, #1a1a3a 75%, #0a0a1a 100%);
  padding: 0;
  position: relative;
  color: #fff;
  overflow: hidden;
}

.home-container::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 30%, rgba(156, 39, 176, 0.2) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(0, 188, 212, 0.2) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(3, 169, 244, 0.15) 0%, transparent 50%),
    linear-gradient(45deg, transparent 49%, rgba(156, 39, 176, 0.15) 50%, transparent 51%),
    linear-gradient(-45deg, transparent 49%, rgba(0, 188, 212, 0.15) 50%, transparent 51%);
  pointer-events: none;
  z-index: 0;
  animation: backgroundShift 20s ease-in-out infinite;
}

/* 欢迎区域 */
.welcome-section {
  padding: 120px 20px 80px;
  text-align: center;
  position: relative;
  z-index: 1;
  background: rgba(10, 10, 26, 0.8);
  backdrop-filter: blur(15px);
  border-bottom: 1px solid rgba(156, 39, 176, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 80vh;
  overflow: hidden;
}

.welcome-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(156, 39, 176, 0.1) 0%, rgba(0, 188, 212, 0.1) 100%);
  z-index: -1;
}

.welcome-content {
  max-width: 800px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.welcome-header {
  margin-bottom: 30px;
}

.welcome-title {
  font-size: 3.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
  text-shadow: 0 0 30px rgba(156, 39, 176, 0.6);
  letter-spacing: 2px;
}

.title-icon {
  font-size: 4rem;
  filter: drop-shadow(0 0 20px rgba(156, 39, 176, 0.8));
  animation: pulse 2s infinite;
}

.title-text {
  background: linear-gradient(90deg, var(--color-accent-primary), var(--color-accent-secondary), var(--color-accent-primary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  background-size: 200% 100%;
  animation: gradientShift 3s ease infinite;
}

.welcome-subtitle {
  font-size: 1.5rem;
  color: var(--color-text-secondary);
  margin-bottom: 30px;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

.welcome-description {
  margin-bottom: 40px;
}

.welcome-description p {
  color: var(--color-text-secondary);
  font-size: 1.2rem;
  margin-bottom: 15px;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

.welcome-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-bottom: 50px;
  flex-wrap: wrap;
}

.cta-btn {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 16px 45px;
  border-radius: 35px;
  font-weight: 700;
  font-size: 1.1rem;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  text-decoration: none;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.cta-btn.primary {
  background: linear-gradient(90deg, var(--color-accent-primary), var(--color-accent-secondary));
  color: white;
  box-shadow: 0 10px 30px rgba(156, 39, 176, 0.4);
}

.cta-btn.secondary {
  background: transparent;
  color: var(--color-accent-primary);
  border: 2px solid var(--color-accent-primary);
  box-shadow: 0 10px 30px rgba(0, 188, 212, 0.3);
}

.cta-btn.large {
  padding: 20px 50px;
  font-size: 1.2rem;
}

.cta-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transform: translateX(-100%);
  transition: transform 0.4s ease;
}

.cta-btn:hover::before {
  transform: translateX(100%);
}

.cta-btn.primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 15px 40px rgba(156, 39, 176, 0.6);
}

.cta-btn.secondary:hover {
  background: var(--color-accent-primary);
  color: var(--color-background-primary);
  transform: translateY(-3px);
  box-shadow: 0 15px 40px rgba(0, 188, 212, 0.6);
}

.btn-icon {
  font-size: 1.2rem;
  transition: transform 0.3s ease;
}

.cta-btn:hover .btn-icon {
  transform: scale(1.2) rotate(5deg);
}

.welcome-stats {
  display: flex;
  justify-content: center;
  gap: 50px;
  margin-top: 40px;
  flex-wrap: wrap;
}

.stat-item {
  text-align: center;
  position: relative;
}

.stat-number {
  display: block;
  font-size: 2.5rem;
  font-weight: 800;
  background: linear-gradient(90deg, var(--color-accent-primary), var(--color-accent-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 10px;
  text-shadow: 0 0 20px rgba(156, 39, 176, 0.6);
}

.stat-label {
  display: block;
  font-size: 1rem;
  color: var(--color-text-secondary);
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

/* 通用部分标题 */
.section-title {
  font-size: 2.5rem;
  background: linear-gradient(90deg, var(--color-accent-primary), var(--color-accent-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-align: center;
  margin-bottom: 50px;
  text-shadow: 0 0 20px rgba(156, 39, 176, 0.5);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}

.section-icon {
  font-size: 2.5rem;
  filter: drop-shadow(0 0 15px rgba(156, 39, 176, 0.6));
  animation: pulse 2s infinite;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -15px;
  left: 50%;
  transform: translateX(-50%);
  width: 120px;
  height: 3px;
  background: linear-gradient(90deg, transparent, var(--color-accent-primary), var(--color-accent-secondary), transparent);
  border-radius: 2px;
}

/* 特色功能 */
.features-section {
  padding: 100px 20px;
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 40px;
}

.feature-card {
  background: rgba(22, 22, 41, 0.8);
  padding: 45px;
  border-radius: 25px;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  backdrop-filter: blur(20px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3), 0 0 20px rgba(156, 39, 176, 0.2);
  position: relative;
  overflow: hidden;
  border: 1px solid transparent;
  background-clip: padding-box;
  -webkit-background-clip: padding-box;
  animation: float 3s ease-in-out infinite;
  animation-delay: calc(var(--index) * 0.2s);
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--color-accent-primary), var(--color-accent-secondary), transparent);
  z-index: 1;
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.feature-card:hover::before {
  transform: scaleX(1);
}

.feature-card:hover {
  background: rgba(156, 39, 176, 0.1);
  transform: translateY(-10px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.4), 0 0 20px rgba(156, 39, 176, 0.2);
  border-color: rgba(156, 39, 176, 0.3);
}

.feature-icon {
  font-size: 4rem;
  display: block;
  margin-bottom: 25px;
  filter: drop-shadow(0 0 20px rgba(156, 39, 176, 0.6));
  transition: all 0.3s ease;
}

.feature-card:hover .feature-icon {
  transform: scale(1.1) rotate(5deg);
  filter: drop-shadow(0 0 30px rgba(156, 39, 176, 0.8));
}

.feature-title {
  font-size: 1.5rem;
  background: linear-gradient(90deg, var(--color-accent-primary), var(--color-accent-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 20px;
  text-shadow: 0 0 10px rgba(156, 39, 176, 0.3);
  transition: all 0.3s ease;
}

.feature-card:hover .feature-title {
  text-shadow: 0 0 20px rgba(156, 39, 176, 0.6);
}

.feature-description {
  color: var(--color-text-secondary);
  font-size: 1rem;
  line-height: 1.6;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
  transition: all 0.3s ease;
}

.feature-card:hover .feature-description {
  color: var(--color-text-primary);
}

.feature-glow {
  position: absolute;
  bottom: -50px;
  right: -50px;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(156, 39, 176, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  transition: all 0.3s ease;
}

.feature-card:hover .feature-glow {
  transform: scale(1.2);
  background: radial-gradient(circle, rgba(156, 39, 176, 0.2) 0%, transparent 70%);
}

/* 热门博客 */
.popular-section {
  padding: 100px 20px;
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
  background: rgba(10, 10, 26, 0.8);
  backdrop-filter: blur(15px);
  border-top: 1px solid rgba(156, 39, 176, 0.3);
  border-bottom: 1px solid rgba(156, 39, 176, 0.3);
}

.popular-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 40px;
}

.popular-card {
  background: rgba(22, 22, 41, 0.7);
  border-radius: 20px;
  overflow: hidden;
  text-decoration: none;
  color: inherit;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(15px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3), 0 0 15px rgba(156, 39, 176, 0.1);
  position: relative;
  overflow: hidden;
}

.popular-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--color-accent-primary), var(--color-accent-secondary), transparent);
  z-index: 1;
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.popular-card:hover::before {
  transform: scaleX(1);
}

.popular-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.4), 0 0 20px rgba(156, 39, 176, 0.2);
  border-color: rgba(156, 39, 176, 0.3);
}

.popular-cover {
  height: 220px;
  background-size: cover;
  background-position: center;
  position: relative;
  overflow: hidden;
}

.popular-cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(156, 39, 176, 0.3), rgba(0, 188, 212, 0.3));
  z-index: 1;
  transition: all 0.3s ease;
}

.popular-card:hover .popular-cover-overlay {
  background: linear-gradient(135deg, rgba(156, 39, 176, 0.4), rgba(0, 188, 212, 0.4));
}

.popular-tag {
  position: absolute;
  top: 20px;
  left: 20px;
  padding: 8px 16px;
  border-radius: 25px;
  font-size: 0.85rem;
  color: white;
  font-weight: 600;
  z-index: 2;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.popular-card:hover .popular-tag {
  transform: scale(1.05);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.4);
}

.popular-content {
  padding: 25px;
}

.popular-title {
  font-size: 1.4rem;
  background: linear-gradient(90deg, var(--color-accent-primary), var(--color-accent-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 15px;
  line-height: 1.4;
  text-shadow: 0 0 10px rgba(156, 39, 176, 0.3);
  transition: all 0.3s ease;
}

.popular-card:hover .popular-title {
  text-shadow: 0 0 20px rgba(156, 39, 176, 0.6);
}

.popular-summary {
  color: var(--color-text-secondary);
  font-size: 1rem;
  line-height: 1.6;
  margin-bottom: 20px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
  transition: all 0.3s ease;
}

.popular-card:hover .popular-summary {
  color: var(--color-text-primary);
}

.popular-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  font-size: 0.85rem;
  padding-top: 20px;
  border-top: 1px solid rgba(156, 39, 176, 0.2);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: var(--color-text-secondary);
  transition: all 0.3s ease;
}

.popular-card:hover .meta-item {
  color: var(--color-text-primary);
}

.meta-icon {
  font-size: 1rem;
  transition: transform 0.3s ease;
}

.popular-card:hover .meta-icon {
  transform: scale(1.1) rotate(5deg);
}

.popular-glow {
  position: absolute;
  bottom: -50px;
  right: -50px;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(0, 188, 212, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  transition: all 0.3s ease;
}

.popular-card:hover .popular-glow {
  transform: scale(1.2);
  background: radial-gradient(circle, rgba(0, 188, 212, 0.2) 0%, transparent 70%);
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 4rem;
  display: block;
  margin-bottom: 20px;
  opacity: 0.6;
}

.empty-text {
  font-size: 1.3rem;
  color: var(--color-text-secondary);
  opacity: 0.7;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .welcome-title {
    font-size: 2.5rem;
    flex-direction: column;
    gap: 10px;
  }
  
  .title-icon {
    font-size: 3rem;
  }
  
  .welcome-subtitle {
    font-size: 1.2rem;
  }
  
  .welcome-description p {
    font-size: 1rem;
  }
  
  .welcome-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .welcome-stats {
    gap: 30px;
  }
  
  .stat-number {
    font-size: 2rem;
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .section-icon {
    font-size: 2rem;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
    gap: 30px;
  }
  
  .popular-grid {
    grid-template-columns: 1fr;
    gap: 30px;
  }
  
  .feature-card,
  .popular-card {
    padding: 30px 20px;
  }
  
  .popular-cover {
    height: 180px;
  }
}

/* 动画 */
@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes gradientShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

@keyframes backgroundShift {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}
</style>