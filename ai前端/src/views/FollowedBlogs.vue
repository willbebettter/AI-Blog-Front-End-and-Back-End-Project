<template>
  <div class="followed-container">
    <div class="container">
      <div class="page-header">
        <h1>📚 我关注的博客</h1>
        <p class="subtitle">查看你关注的博主发布的所有博客</p>
      </div>

      <div class="followed-grid">
        <router-link 
          v-for="blog in followedBlogs" 
          :key="blog.id"
          :to="`/blog/${blog.id}`"
          class="followed-card"
        >
          <div class="blog-cover" :style="`background-image: url('${blog.coverImage || '/images/default-cover.svg'}')`">
            <span class="blog-tag">{{ getTagColor(blog.tags?.[0] || 'tech') }}</span>
          </div>
          <div class="blog-info">
            <h3 class="blog-title">{{ blog.title }}</h3>
            <p class="blog-summary">{{ blog.summary || '暂无摘要...' }}</p>
            <div class="blog-meta">
              <span class="blog-author">👤 {{ blog.author?.nickname || '作者' }}</span>
              <span class="blog-time">{{ formatTime(blog.createTime) }}</span>
              <span class="blog-views">👁️ {{ blog.views || 0 }}</span>
              <span class="blog-likes">❤️ {{ blog.likes || 0 }}</span>
            </div>
          </div>
        </router-link>
      </div>

      <div v-if="followedBlogs.length === 0" class="empty-state">
        <div class="empty-icon">👤</div>
        <h3>你还没有关注任何博主</h3>
        <p>去浏览博客，关注你喜欢的博主吧！</p>
        <router-link to="/blog" class="explore-btn">开始浏览</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getFollowedBlogs } from '@/api/user'

const followedBlogs = ref([])

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString()
}

const getTagColor = (tag) => {
  const colors = {
    'tech': '#4dd0e1',
    'life': '#f093fb',
    'code': '#f5a623',
    'design': '#74b9ff',
    'share': '#55efc4',
    'other': '#95a5a6'
  }
  return colors[tag] || '#4dd0e1'
}

const fetchFollowedBlogs = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!user.id) return
    const res = await getFollowedBlogs(user.id)
    followedBlogs.value = res.data || []
  } catch (error) {
    console.error('获取关注博客失败:', error)
  }
}

onMounted(() => {
  fetchFollowedBlogs()
})
</script>

<style scoped>
.followed-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a3e 0%, #2d2d5a 50%, #1a1a3e 100%);
  padding: 2rem 0;
  color: #fff;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

.page-header {
  text-align: center;
  margin-bottom: 3rem;
}

.page-header h1 {
  font-size: 2.5rem;
  color: #4dd0e1;
  margin-bottom: 0.5rem;
}

.subtitle {
  color: #888;
  font-size: 1.1rem;
}

.followed-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 2rem;
}

.followed-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  overflow: hidden;
  text-decoration: none;
  color: inherit;
  transition: all 0.3s ease;
  border: 1px solid rgba(77, 208, 225, 0.1);
  display: block;
}

.followed-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(77, 208, 225, 0.2);
}

.blog-cover {
  height: 180px;
  background-size: cover;
  background-position: center;
  position: relative;
}

.blog-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  color: white;
  font-weight: 600;
  background: rgba(0, 0, 0, 0.7);
}

.blog-info {
  padding: 1.5rem;
}

.blog-title {
  font-size: 1.3rem;
  color: #4dd0e1;
  margin-bottom: 0.8rem;
  line-height: 1.4;
}

.blog-summary {
  color: #888;
  font-size: 0.9rem;
  line-height: 1.6;
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.blog-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  color: #666;
  padding-top: 10px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.blog-author {
  color: #4dd0e1;
}

.blog-time {
  color: #777;
}

.blog-views,
.blog-likes {
  color: #888;
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  border: 1px dashed rgba(77, 208, 225, 0.3);
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.empty-state h3 {
  color: #4dd0e1;
  margin-bottom: 0.5rem;
}

.empty-state p {
  color: #888;
  margin-bottom: 1.5rem;
}

.explore-btn {
  display: inline-block;
  padding: 12px 30px;
  background: linear-gradient(90deg, #4dd0e1, #00bcd4);
  color: white;
  text-decoration: none;
  border-radius: 25px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.explore-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(77, 208, 225, 0.4);
}

@media (max-width: 768px) {
  .followed-grid {
    grid-template-columns: 1fr;
  }
  
  .page-header h1 {
    font-size: 2rem;
  }
}
</style>
