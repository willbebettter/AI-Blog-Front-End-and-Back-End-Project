<template>
  <div class="all-blogs-container">
    <button class="back-btn" @click="goBack">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M19 12H5M12 19l-7-7 7-7"/>
      </svg>
      返回
    </button>

    <div class="page-header">
      <h1 class="page-title">
        <span class="title-icon">⭐</span>
        收藏的博客
      </h1>
      <span class="count-badge">{{ blogs.length }} 篇</span>
    </div>

    <div v-if="blogs.length > 0" class="blog-list">
      <div v-for="blog in blogs" :key="blog.id" class="blog-item" @click="viewBlog(blog)">
        <div class="blog-cover">
          <img :src="blog.coverImage || '/images/default-cover.svg'" :alt="blog.title" />
          <div class="cover-overlay">
            <span class="cover-badge">⭐</span>
          </div>
        </div>
        <div class="blog-info">
          <h3 class="blog-title">{{ blog.title }}</h3>
          <p class="blog-summary">{{ blog.summary }}</p>
          <div class="blog-meta">
            <span class="blog-date">{{ formatDate(blog.createTime) }}</span>
            <span class="blog-views">👁 {{ blog.views || 0 }}</span>
            <span class="blog-likes">❤ {{ blog.likes || 0 }}</span>
          </div>
          <div v-if="blog.tags" class="blog-tags">
            <span v-for="(tag, index) in getTags(blog.tags)" :key="index" class="tag">{{ tag }}</span>
          </div>
        </div>
        <button @click.stop="uncollect(blog.id)" class="uncollect-btn">取消收藏</button>
      </div>
    </div>

    <div v-else class="empty-state">
      <div class="empty-icon">⭐</div>
      <p>暂无收藏的博客</p>
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

const getTags = (tags) => {
  if (typeof tags === 'string') {
    try {
      return JSON.parse(tags)
    } catch {
      return tags.split(',').map(t => t.trim()).filter(Boolean)
    }
  }
  return tags || []
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString()
}

const viewBlog = (blog) => {
  router.push(`/blog/${blog.id}`)
}

const uncollect = async (blogId) => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    await toggleCollect(blogId, user.id, 'remove')
    const index = blogs.value.findIndex(b => b.id === blogId)
    if (index > -1) {
      blogs.value.splice(index, 1)
    }
  } catch (error) {
    console.error('取消收藏失败:', error)
  }
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

onMounted(() => {
  fetchCollectedBlogs()
})
</script>

<style scoped>
.all-blogs-container {
  min-height: 100vh;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
}

.back-btn {
  position: absolute;
  top: 20px;
  left: 20px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  background: linear-gradient(135deg, rgba(123, 31, 162, 0.7), rgba(25, 118, 210, 0.7));
  color: #fff;
  border: none;
  border-radius: 20px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;
}

.back-btn:hover {
  background: linear-gradient(135deg, rgba(123, 31, 162, 0.9), rgba(25, 118, 210, 0.9));
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(123, 31, 162, 0.4);
}

.page-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid rgba(123, 31, 162, 0.3);
}

.page-title {
  font-size: 2rem;
  font-weight: bold;
  color: #e0e0e0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.title-icon {
  font-size: 1.8rem;
}

.count-badge {
  background: linear-gradient(135deg, #9c27b0, #03a9f4);
  color: white;
  padding: 0.4rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
}

.blog-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.blog-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.2rem;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid rgba(123, 31, 162, 0.3);
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
  background: linear-gradient(90deg, transparent, rgba(123, 31, 162, 0.1), transparent);
  transform: translateX(-100%);
  transition: transform 0.5s ease;
}

.blog-item:hover::before {
  transform: translateX(100%);
}

.blog-item:hover {
  transform: translateX(5px);
  box-shadow: 0 4px 20px rgba(123, 31, 162, 0.3);
  border-color: rgba(123, 31, 162, 0.6);
  background: rgba(123, 31, 162, 0.1);
}

.blog-cover {
  width: 120px;
  height: 80px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 4px 15px rgba(123, 31, 162, 0.3);
  transition: all 0.3s ease;
  position: relative;
}

.blog-item:hover .blog-cover {
  transform: scale(1.05);
}

.blog-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(156, 39, 176, 0.8), rgba(3, 169, 244, 0.8));
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.blog-item:hover .cover-overlay {
  opacity: 1;
}

.cover-badge {
  font-size: 2rem;
  color: white;
  filter: drop-shadow(0 2px 10px rgba(255, 255, 255, 0.5));
}

.blog-info {
  flex: 1;
  min-width: 0;
}

.blog-title {
  font-size: 1.15rem;
  font-weight: bold;
  color: #e0e0e0;
  margin-bottom: 0.5rem;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.blog-summary {
  color: #b0b0b0;
  font-size: 0.9rem;
  margin-bottom: 0.8rem;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.blog-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.85rem;
  color: #a0a0a0;
  margin-bottom: 0.5rem;
}

.blog-tags {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.tag {
  background: rgba(156, 39, 176, 0.2);
  color: #9c27b0;
  padding: 0.3rem 0.8rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
}

.uncollect-btn {
  padding: 0.5rem 1rem;
  border: none;
  background: rgba(231, 76, 60, 0.2);
  color: #ff7675;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.85rem;
}

.uncollect-btn:hover {
  background: rgba(231, 76, 60, 0.4);
  transform: scale(1.05);
}

.empty-state {
  text-align: center;
  padding: 10rem 2rem;
  color: #a0a0a0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1.5rem;
}

.empty-state p {
  font-size: 1.2rem;
  margin-top: 20px;
}
</style>