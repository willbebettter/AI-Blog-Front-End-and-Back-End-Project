<template>
  <div class="blog-management-page">
    <button class="back-btn" @click="goBack">← 返回</button>
    <div class="blog-management-container">
      <h1 class="page-title">
        <span class="title-icon">📝</span>
        管理博客
      </h1>
      
      <div v-if="blogs.length > 0" class="blog-list">
        <div v-for="blog in blogs" :key="blog.id" class="blog-item" @click="viewBlog(blog)">
          <div class="blog-cover">
            <img :src="blog.coverImage || '/images/default-cover.svg'" :alt="blog.title" />
          </div>
          <div class="blog-info">
            <h3 class="blog-title">{{ blog.title }}</h3>
            <p class="blog-summary">{{ blog.summary }}</p>
            <div class="blog-meta">
              <span class="blog-date">{{ formatDate(blog.createTime) }}</span>
              <span class="blog-views">👁 {{ blog.views || 0 }}</span>
              <span class="blog-likes">❤ {{ blog.likes || 0 }}</span>
              <span v-if="blog.newCommentCount > 0" class="blog-comments-new">💬 {{ blog.newCommentCount }} 条新评论</span>
            </div>
            <div v-if="blog.tags" class="blog-tags">
              <span v-for="(tag, index) in getTags(blog.tags)" :key="index" class="tag">{{ tag }}</span>
            </div>
          </div>
          <div v-if="blog.newCommentCount > 0" class="new-comment-badge">{{ blog.newCommentCount }}</div>
          <div class="blog-actions" @click.stop>
            <button @click="editBlog(blog)" class="action-btn small primary">编辑</button>
            <button @click="deleteBlogAction(blog.id)" class="action-btn small danger">删除</button>
          </div>
        </div>
      </div>
      
      <div v-else class="empty-state">
        <div class="empty-icon">📝</div>
        <p>暂无博客，点击上方按钮开始写博客吧！</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserBlogs } from '@/api/user'
import { deleteBlog, getComments } from '@/api/blog'
import { showToast } from '@/utils/toast'

const router = useRouter()
const blogs = ref([])

const goBack = () => {
  router.back()
}

const loadBlogs = async () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (!user.id) {
    router.push('/login')
    return
  }
  try {
    const res = await getUserBlogs(user.id)
    blogs.value = res.data || []
    loadNewCommentCounts()
  } catch (e) {
    console.error('获取博客列表失败', e)
  }
}

const loadNewCommentCounts = async () => {
  const viewedComments = JSON.parse(localStorage.getItem('viewedComments') || '{}')
  for (const blog of blogs.value) {
    try {
      const res = await getComments(blog.id)
      const comments = res.data || []
      const lastViewedTime = viewedComments[blog.id] || 0
      const newCount = comments.filter(c => new Date(c.createTime).getTime() > lastViewedTime).length
      blog.newCommentCount = newCount
    } catch (e) {
      blog.newCommentCount = 0
    }
  }
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const getTags = (tags) => {
  if (!tags) return []
  if (Array.isArray(tags)) return tags
  return tags.split(',').map(tag => tag.trim()).filter(Boolean)
}

const viewBlog = (blog) => {
  const viewedComments = JSON.parse(localStorage.getItem('viewedComments') || '{}')
  viewedComments[blog.id] = Date.now()
  localStorage.setItem('viewedComments', JSON.stringify(viewedComments))
  blog.newCommentCount = 0
  router.push(`/blog/${blog.id}`)
}

const editBlog = (blog) => {
  router.push(`/blog/edit/${blog.id}`)
}

const deleteBlogAction = async (blogId) => {
  try {
    await deleteBlog(blogId)
    blogs.value = blogs.value.filter(blog => blog.id !== blogId)
    showToast('博客删除成功', 'success')
  } catch (e) {
    console.error('删除博客失败', e)
    showToast('删除失败，请重试', 'error')
  }
}

onMounted(() => {
  loadBlogs()
})
</script>

<style scoped>
.blog-management-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 25%, #2a2a4a 50%, #1a1a3a 75%, #0a0a1a 100%);
  padding: 20px;
  position: relative;
  box-sizing: border-box;
}

.blog-management-page::before {
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

.blog-management-container {
  max-width: 1200px;
  margin: 0 auto;
  background: rgba(10, 10, 26, 0.85);
  backdrop-filter: blur(15px);
  padding: 40px 20px 20px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3), 0 0 15px rgba(123, 31, 162, 0.1);
  border: 1px solid rgba(123, 31, 162, 0.4);
  position: relative;
  z-index: 1;
}

.page-title {
  font-size: 2rem;
  font-weight: bold;
  color: white;
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.title-icon {
  font-size: 1.8rem;
}

.blog-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.blog-item {
  display: flex;
  gap: 1.5rem;
  padding: 1.5rem;
  background: rgba(40, 40, 40, 0.6);
  border-radius: 16px;
  border: 1px solid rgba(123, 31, 162, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.blog-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(123, 31, 162, 0.3);
  border-color: rgba(123, 31, 162, 0.5);
}

.blog-cover {
  width: 150px;
  height: 100px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.blog-item:hover .blog-cover {
  transform: scale(1.05);
}

.blog-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.blog-info {
  flex: 1;
  min-width: 0;
}

.blog-title {
  font-size: 1.2rem;
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
  color: #a0a0a0;
  font-size: 0.95rem;
  margin-bottom: 1rem;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.blog-meta {
  display: flex;
  gap: 1.5rem;
  font-size: 0.85rem;
  color: #888;
  margin-bottom: 0.8rem;
  flex-wrap: wrap;
}

.blog-comments-new {
  color: #ff4757;
  font-weight: 600;
}

.blog-tags {
  display: flex;
  gap: 0.8rem;
  flex-wrap: wrap;
}

.tag {
  background: rgba(123, 31, 162, 0.2);
  color: #9c27b0;
  padding: 0.3rem 0.8rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
}

.new-comment-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  min-width: 24px;
  height: 24px;
  border-radius: 12px;
  background: #ff4757;
  color: white;
  font-size: 0.75rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
  box-shadow: 0 2px 8px rgba(255, 71, 87, 0.4);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.blog-actions {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  flex-shrink: 0;
}

.action-btn {
  padding: 1rem 2rem;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1rem;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  text-decoration: none;
}

.action-btn.primary {
  background: linear-gradient(135deg, #9c27b0 0%, #03a9f4 100%);
  color: white;
}

.action-btn.primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(123, 31, 162, 0.4);
}

.action-btn.small {
  padding: 0.8rem 1.2rem;
  font-size: 0.9rem;
  white-space: nowrap;
}

.action-btn.danger {
  background: linear-gradient(135deg, #ff4757 0%, #ff6b7a 100%);
  color: white;
}

.action-btn.danger:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 71, 87, 0.4);
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #888;
}

.empty-icon {
  font-size: 4rem;
  display: block;
  margin-bottom: 1.5rem;
  opacity: 0.5;
}

.empty-state p {
  font-size: 1.1rem;
  margin: 0;
  color: #a0a0a0;
}

@media (max-width: 768px) {
  .blog-management-page { padding: 15px; }
  .blog-management-container { padding: 1.5rem; }
  .page-title { font-size: 1.5rem; }
  .blog-item { flex-direction: column; align-items: flex-start; }
  .blog-cover { width: 100%; height: 150px; }
  .blog-actions { flex-direction: row; width: 100%; justify-content: flex-end; }
}
</style>
