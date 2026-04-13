<template>
  <div class="author-profile-page">
    <button class="back-btn" @click="$router.back()">← 返回</button>
    
    <div v-if="author" class="author-profile-container">
      <div class="author-header fade-in">
        <div class="author-avatar-wrapper">
          <div class="avatar-frame">
            <img :src="author.avatar || '/images/default-avatar.svg'" :alt="author.username" class="avatar-img" />
          </div>
        </div>
        <div class="author-info">
          <h1 class="author-name">{{ author.username }}</h1>
          <p class="author-bio">{{ author.bio || '暂无简介' }}</p>
          <div class="author-stats-row">
            <div class="author-stat">
              <span class="stat-num">{{ activeDays }}</span>
              <span class="stat-label">活跃天数</span>
            </div>
            <div class="author-stat">
              <span class="stat-num">{{ author.articleCount || 0 }}</span>
              <span class="stat-label">文章</span>
            </div>
            <div class="author-stat">
              <span class="stat-num">{{ author.followersCount || 0 }}</span>
              <span class="stat-label">粉丝</span>
            </div>
          </div>
        </div>
      </div>

      <div class="author-blogs-section">
        <h2 class="section-title">
          <span class="title-icon">📝</span>
          发表的博客
        </h2>
        
        <div v-if="blogs.length > 0" class="blog-list">
          <div 
            v-for="(blog, index) in blogs" 
            :key="blog.id"
            class="blog-item fade-in"
            :style="{ animationDelay: `${index * 0.05}s` }"
            @click="goToBlog(blog.id)"
          >
            <div class="blog-item-cover">
              <img :src="blog.coverImage || '/images/default-cover.svg'" :alt="blog.title" />
            </div>
            <div class="blog-item-info">
              <h3 class="blog-item-title">{{ blog.title }}</h3>
              <p class="blog-item-summary">{{ blog.summary || '暂无摘要...' }}</p>
              <div class="blog-item-tags" v-if="blog.tags && blog.tags.length > 0">
                <span 
                  v-for="tag in blog.tags" 
                  :key="tag"
                  class="blog-tag-item"
                  :style="{ background: getTagColor(tag) }"
                >
                  {{ tag }}
                </span>
              </div>
              <div class="blog-item-meta">
                <span class="meta-item">📅 {{ formatTime(blog.createTime) }}</span>
                <span class="meta-item">👁️ {{ blog.views || 0 }}</span>
                <span class="meta-item">❤️ {{ blog.likes || 0 }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <div class="empty-icon">📝</div>
          <p>该博主暂未发表博客</p>
        </div>
      </div>
    </div>

    <div v-else class="loading-state">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserInfo } from '@/api/user'
import { getBlogList } from '@/api/blog'

const route = useRoute()
const router = useRouter()
const author = ref(null)
const blogs = ref([])

const activeDays = computed(() => {
  if (!author.value?.createTime) return 0
  const registerDate = new Date(author.value.createTime)
  const today = new Date()
  return Math.ceil(Math.abs(today - registerDate) / (1000 * 60 * 60 * 24))
})

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString()
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

const goToBlog = (blogId) => {
  router.push({ path: `/blog/${blogId}`, query: { from: 'author', authorId: route.params.id } })
}

const fetchAuthor = async () => {
  const authorId = Number(route.params.id)
  if (!authorId) return
  try {
    const res = await getUserInfo(authorId)
    author.value = res.data
  } catch (e) {
    console.error('获取博主信息失败', e)
  }
}

const fetchBlogs = async () => {
  const authorId = Number(route.params.id)
  if (!authorId) return
  try {
    const res = await getBlogList({ authorId, page: 1, size: 50 })
    blogs.value = (res.data?.content || []).map(b => ({
      ...b,
      tags: b.tags ? (Array.isArray(b.tags) ? b.tags : b.tags.split(',').map(t => t.trim()).filter(Boolean)) : []
    }))
  } catch (e) {
    console.error('获取博主博客失败', e)
  }
}

onMounted(() => {
  fetchAuthor()
  fetchBlogs()
})
</script>

<style scoped>
.author-profile-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 25%, #2a2a4a 50%, #1a1a3a 75%, #0a0a1a 100%);
  padding: 20px;
  position: relative;
  box-sizing: border-box;
}

.author-profile-page::before {
  content: '';
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: 
    radial-gradient(circle at 20% 30%, rgba(123, 31, 162, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(25, 118, 210, 0.15) 0%, transparent 50%);
  z-index: 0;
  pointer-events: none;
}

.back-btn {
  position: fixed;
  top: 20px;
  left: 20px;
  padding: 8px 16px;
  background: rgba(123, 31, 162, 0.3);
  border: 1px solid rgba(123, 31, 162, 0.5);
  border-radius: 25px;
  color: #e0e0e0;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 1000;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.back-btn:hover {
  background: rgba(123, 31, 162, 0.5);
  border-color: #9c27b0;
}

.author-profile-container {
  max-width: 1000px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.author-header {
  display: flex;
  align-items: center;
  gap: 30px;
  padding: 40px;
  background: rgba(10, 10, 26, 0.85);
  backdrop-filter: blur(15px);
  border-radius: 20px;
  border: 1px solid rgba(123, 31, 162, 0.4);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  margin-bottom: 30px;
}

.avatar-frame {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  padding: 4px;
  background: linear-gradient(135deg, #9c27b0, #00bcd4);
  flex-shrink: 0;
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(10, 10, 26, 0.8);
}

.author-info {
  flex: 1;
}

.author-name {
  font-size: 2rem;
  font-weight: bold;
  background: linear-gradient(90deg, #4dd0e1, #00bcd4);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
}

.author-bio {
  color: #aaa;
  font-size: 1rem;
  margin-bottom: 20px;
  line-height: 1.6;
}

.author-stats-row {
  display: flex;
  gap: 30px;
}

.author-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-num {
  font-size: 1.4rem;
  font-weight: 700;
  color: #4dd0e1;
}

.stat-label {
  font-size: 0.85rem;
  color: #888;
}

.section-title {
  font-size: 1.5rem;
  color: #e0e0e0;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  font-size: 1.2rem;
}

.blog-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.blog-item {
  display: flex;
  gap: 20px;
  background: rgba(22, 22, 41, 0.7);
  border-radius: 16px;
  overflow: hidden;
  color: inherit;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(15px);
  min-height: 120px;
  cursor: pointer;
}

.blog-item:hover {
  transform: translateX(6px);
  border-color: rgba(156, 39, 176, 0.4);
  box-shadow: 0 4px 20px rgba(156, 39, 176, 0.15);
}

.blog-item-cover {
  width: 180px;
  min-width: 180px;
  height: 120px;
  overflow: hidden;
  flex-shrink: 0;
}

.blog-item-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.blog-item:hover .blog-item-cover img {
  transform: scale(1.05);
}

.blog-item-info {
  flex: 1;
  padding: 14px 14px 14px 0;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.blog-item-title {
  font-size: 1.1rem;
  background: linear-gradient(90deg, #00bcd4, #9c27b0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.blog-item-summary {
  color: #aaa;
  font-size: 0.9rem;
  line-height: 1.5;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

.blog-item-tags {
  display: flex;
  gap: 6px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.blog-tag-item {
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 0.75rem;
  color: white;
  font-weight: 500;
}

.blog-item-meta {
  display: flex;
  gap: 14px;
  font-size: 0.8rem;
  color: #888;
  margin-top: auto;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #888;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 16px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 20px;
  color: #888;
  position: relative;
  z-index: 1;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(156, 39, 176, 0.3);
  border-top: 4px solid #00bcd4;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.fade-in {
  animation: fadeIn 0.4s ease-out both;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 768px) {
  .author-header {
    flex-direction: column;
    text-align: center;
    padding: 30px 20px;
  }
  .author-stats-row {
    justify-content: center;
  }
  .blog-item {
    flex-direction: column;
  }
  .blog-item-cover {
    width: 100%;
    min-width: unset;
    height: 160px;
  }
  .blog-item-info {
    padding: 14px;
  }
}
</style>
