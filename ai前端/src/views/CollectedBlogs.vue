<template>
  <div class="collected-blogs-container">
    <button class="back-btn" @click="goBack">← 返回</button>
    <div class="collected-blogs-content">
      <h2 class="page-title">⭐ 收藏的博客</h2>

      <div class="blog-grid" v-if="blogs.length > 0">
        <router-link 
          v-for="blog in blogs" 
          :key="blog.id" 
          :to="`/blog/${blog.id}`" 
          class="blog-card"
        >
          <div class="blog-cover">
            <img :src="blog.coverImage || '/images/default-cover.svg'" :alt="blog.title" />
            <span class="tag-badge">{{ blog.tags ? (Array.isArray(blog.tags) ? blog.tags[0] : blog.tags.split(',')[0]) : 'tech' }}</span>
          </div>
          <div class="blog-content">
            <h4 class="blog-title">{{ blog.title }}</h4>
            <p class="blog-summary">{{ blog.summary || '暂无摘要' }}</p>
            <div class="blog-meta">
              <span class="blog-time">{{ formatTime(blog.createTime) }}</span>
              <span class="blog-views">👁 {{ blog.views || 0 }}</span>
              <span class="blog-likes">❤ {{ blog.likes || 0 }}</span>
            </div>
          </div>
          <button @click.prevent="uncollectBlog(blog)" class="uncollect-btn">取消收藏</button>
        </router-link>
      </div>

      <div v-else class="empty-state">
        <h3>暂无收藏内容</h3>
        <p>去收藏一些博客吧</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCollectedBlogs } from '@/api/user'
import { toggleCollect } from '@/api/blog'

const router = useRouter()
const blogs = ref([])

const goBack = () => {
  router.back()
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString()
}

const fetchCollectedBlogs = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!user.id) {
      router.push('/login')
      return
    }
    const res = await getCollectedBlogs(user.id)
    blogs.value = res.data || []
  } catch (error) {
    console.error('获取收藏博客失败:', error)
    blogs.value = []
  }
}

const uncollectBlog = async (blog) => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    await toggleCollect(blog.id, user.id, 'remove')
    blogs.value = blogs.value.filter(b => b.id !== blog.id)
  } catch (error) {
    console.error('取消收藏失败:', error)
  }
}

onMounted(() => {
  fetchCollectedBlogs()
})
</script>

<style scoped>
.collected-blogs-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 50%, #0a0a1a 100%);
  padding: 20px;
  position: relative;
  overflow: auto;
  box-sizing: border-box;
}

.collected-blogs-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 30%, rgba(123, 31, 162, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(25, 118, 210, 0.15) 0%, transparent 50%);
  z-index: 0;
  pointer-events: none;
}

.back-btn {
  position: absolute;
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
  z-index: 10;
}

.back-btn:hover {
  background: rgba(123, 31, 162, 0.5);
  border-color: #9c27b0;
}

.collected-blogs-content {
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
  min-height: calc(100vh - 40px);
  padding-top: 60px;
}

.back-btn {
  padding: 8px 16px;
  background: rgba(123, 31, 162, 0.3);
  border: 1px solid rgba(123, 31, 162, 0.5);
  border-radius: 25px;
  color: #e0e0e0;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 1rem;
}

.back-btn:hover {
  background: rgba(123, 31, 162, 0.5);
  border-color: #9c27b0;
}

.page-title {
  font-size: 1.5rem;
  color: #e0e0e0;
  margin-bottom: 1.5rem;
  text-align: center;
}

.blog-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.blog-card {
  background: rgba(10, 10, 26, 0.85);
  border-radius: 15px;
  overflow: hidden;
  text-decoration: none;
  color: inherit;
  transition: all 0.3s ease;
  position: relative;
  border: 1px solid rgba(123, 31, 162, 0.3);
}

.blog-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(123, 31, 162, 0.3);
  border-color: rgba(123, 31, 162, 0.5);
}

.blog-cover {
  width: 100%;
  height: 200px;
  position: relative;
  overflow: hidden;
  background-color: rgba(40, 40, 60, 0.8);
}

.blog-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.tag-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background: rgba(123, 31, 162, 0.8);
  color: white;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
}

.blog-content {
  padding: 20px;
}

.blog-title {
  font-size: 1.3rem;
  color: #e0e0e0;
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.blog-summary {
  font-size: 0.95rem;
  color: #a0a0a0;
  margin-bottom: 15px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.blog-meta {
  display: flex;
  gap: 15px;
  font-size: 0.85rem;
  color: #888;
}

.uncollect-btn {
  position: absolute;
  bottom: 20px;
  right: 20px;
  padding: 8px 16px;
  background: rgba(255, 193, 7, 0.2);
  border: 1px solid rgba(255, 193, 7, 0.4);
  border-radius: 20px;
  color: #ffc107;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.uncollect-btn:hover {
  background: rgba(255, 193, 7, 0.4);
  border-color: #ff9800;
}

.empty-state {
  text-align: center;
  padding: 100px 20px;
  color: #a0a0a0;
}

.empty-state h3 {
  font-size: 1.5rem;
  margin-bottom: 10px;
  color: #e0e0e0;
}

.empty-state p {
  font-size: 1rem;
  color: #888;
}
</style>