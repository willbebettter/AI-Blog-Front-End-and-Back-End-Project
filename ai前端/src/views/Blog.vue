<template>
  <div class="blog-list-container">
    <div v-if="loading" class="loading-overlay">
      <div class="loading-content">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
    </div>

    <div class="blog-list-content">
      <div class="search-section">
        <div class="search-wrapper">
          <input 
            type="text" 
            v-model="searchKeyword" 
            placeholder="🔍 搜索文章、标签或作者..."
            class="search-input"
            @keyup.enter="search"
          />
          <button class="search-btn" @click="search">
            <span class="search-btn-icon">🔍</span>
          </button>
        </div>
      </div>
      
      <div class="filter-section">
        <select v-model="selectedCategory" class="filter-select" @change="search">
          <option value="">全部分类</option>
          <option value="tech">技术</option>
          <option value="life">生活</option>
          <option value="code">编程</option>
          <option value="design">设计</option>
          <option value="share">分享</option>
          <option value="other">其他</option>
        </select>
        
        <select v-model="sortBy" class="filter-select" @change="search">
          <option value="createTime">最新</option>
          <option value="views">最多浏览</option>
          <option value="likes">最多点赞</option>
        </select>
        
        <select v-model="sortOrder" class="filter-select" @change="search">
          <option value="DESC">降序</option>
          <option value="ASC">升序</option>
        </select>
        
        <button class="reset-btn" @click="resetFilters">
          <span class="reset-icon">🔄</span>
          重置
        </button>
      </div>

      <div v-if="filterTag" class="tag-filter-indicator">
        <span class="tag-filter-label">标签筛选:</span>
        <span class="tag-filter-value">{{ filterTag }}</span>
        <button class="tag-filter-remove" @click="clearTagFilter">×</button>
      </div>

      <div class="blog-list" v-if="blogs.length > 0">
        <router-link 
          v-for="(blog, index) in blogs" 
          :key="blog.id"
          :to="`/blog/${blog.id}`"
          class="blog-item fade-in"
          :style="{ animationDelay: `${index * 0.05}s` }"
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
                @click.stop="goToTag(tag)"
              >
                {{ tag }}
              </span>
            </div>
            <div class="blog-item-meta">
              <div class="meta-item">
                <span class="meta-icon">👤</span>
                <span class="meta-text">{{ blog.author?.username || '作者' }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-icon">📅</span>
                <span class="meta-text">{{ formatTime(blog.createTime) }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-icon">👁️</span>
                <span class="meta-text">{{ blog.views || 0 }}</span>
              </div>
              <div class="meta-item like-btn" @click.stop="handleLike(blog)">
                <span class="meta-icon">{{ blog.isLiked ? '❤️' : '🤍' }}</span>
                <span class="meta-text">{{ blog.likes || 0 }}</span>
              </div>
            </div>
          </div>
        </router-link>
      </div>

      <div v-if="blogs.length === 0 && !loading" class="empty-state">
        <div class="empty-icon">🔍</div>
        <h3>没有找到相关博客</h3>
        <p>尝试更换关键词或标签</p>
      </div>

      <div v-if="totalPages > 1" class="pagination">
        <button 
          :disabled="currentPage === 1" 
          @click="goToPage(currentPage - 1)"
          class="pagination-btn"
        >
          <span class="pagination-icon">←</span>
          上一页
        </button>
        
        <div class="pagination-pages">
          <button 
            v-for="p in displayPages" 
            :key="p"
            :class="['page-btn', { active: p === currentPage }]"
            @click="goToPage(p)"
          >
            {{ p }}
          </button>
        </div>
        
        <button 
          :disabled="currentPage === totalPages" 
          @click="goToPage(currentPage + 1)"
          class="pagination-btn"
        >
          下一页
          <span class="pagination-icon">→</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getBlogList, searchBlogs, getBlogsByTag, toggleLike as apiToggleLike, isBlogLiked } from '@/api/blog'
import { showToast } from '@/utils/toast'

const route = useRoute()
const router = useRouter()
const blogs = ref([])
const searchKeyword = ref('')
const selectedCategory = ref('')
const sortBy = ref('createTime')
const sortOrder = ref('DESC')
const currentPage = ref(1)
const pageSize = 10
const totalElements = ref(0)
const totalPages = ref(0)
const loading = ref(false)
const filterAuthorId = ref('')
const filterTag = ref('')

const parseTags = (tags) => {
  if (!tags) return []
  if (Array.isArray(tags)) return tags
  return tags.split(',').map(t => t.trim()).filter(Boolean)
}

onMounted(() => {
  if (route.query.authorId) {
    filterAuthorId.value = route.query.authorId
  }
  if (route.query.tag) {
    filterTag.value = route.query.tag
  }
  fetchBlogs()
})

watch(() => route.query.authorId, (newVal) => {
  if (newVal) {
    filterAuthorId.value = newVal
    currentPage.value = 1
    fetchBlogs()
  }
})

watch(() => route.query.tag, (newVal) => {
  if (newVal) {
    filterTag.value = newVal
    currentPage.value = 1
    fetchBlogs()
  }
})

const displayPages = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value
  
  if (total <= 7) {
    for (let i = 1; i <= total; i++) pages.push(i)
  } else {
    pages.push(1)
    if (current > 3) pages.push('...')
    for (let i = Math.max(2, current - 1); i <= Math.min(total - 1, current + 1); i++) {
      pages.push(i)
    }
    if (current < total - 2) pages.push('...')
    pages.push(total)
  }
  
  return pages.filter(p => p !== '...')
})

const goToPage = (page) => {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  fetchBlogs()
}

const fetchBlogs = async () => {
  loading.value = true
  try {
    let res
    if (searchKeyword.value) {
      res = await searchBlogs(searchKeyword.value, currentPage.value, pageSize, sortBy.value, sortOrder.value)
    } else if (filterTag.value) {
      res = await getBlogsByTag(filterTag.value, currentPage.value, pageSize, sortBy.value, sortOrder.value)
    } else {
      const params = {
        page: currentPage.value,
        size: pageSize,
        sortBy: sortBy.value,
        order: sortOrder.value
      }
      if (selectedCategory.value) params.category = selectedCategory.value
      if (filterAuthorId.value) params.authorId = filterAuthorId.value
      res = await getBlogList(params)
    }
    
    const data = res.data || {}
    blogs.value = (data.content || []).map(blog => ({
      ...blog,
      tags: parseTags(blog.tags)
    }))
    totalElements.value = data.totalElements || 0
    totalPages.value = data.totalPages || 0
  } catch (error) {
    console.error('获取博客列表失败:', error)
  } finally {
    loading.value = false
  }
}

const search = () => {
  currentPage.value = 1
  fetchBlogs()
}

const resetFilters = () => {
  searchKeyword.value = ''
  selectedCategory.value = ''
  sortBy.value = 'createTime'
  sortOrder.value = 'DESC'
  filterTag.value = ''
  currentPage.value = 1
  fetchBlogs()
}

const clearTagFilter = () => {
  filterTag.value = ''
  currentPage.value = 1
  fetchBlogs()
}

const goToTag = (tag) => {
  filterTag.value = tag
  currentPage.value = 1
  fetchBlogs()
}

const handleLike = async (blog) => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (!user.id) {
    showToast('请先登录', 'warning')
    return
  }
  try {
    const res = await apiToggleLike(blog.id, user.id)
    blog.isLiked = res.data.liked
    blog.likes = res.data.likes
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

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
</script>

<style scoped>
.blog-list-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 25%, #2a2a4a 50%, #1a1a3a 75%, #0a0a1a 100%);
  padding: 100px 20px 40px;
  position: relative;
  box-sizing: border-box;
}

.blog-list-container::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 30%, rgba(156, 39, 176, 0.2) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(0, 188, 212, 0.2) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

.blog-list-content {
  position: relative;
  z-index: 1;
  max-width: 1000px;
  margin: 0 auto;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(10, 10, 26, 0.9);
  backdrop-filter: blur(15px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  background: rgba(22, 22, 41, 0.8);
  padding: 40px;
  border-radius: 20px;
  border: 1px solid rgba(156, 39, 176, 0.3);
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(156, 39, 176, 0.3);
  border-top: 4px solid #00bcd4;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-content p {
  color: #e0e0e0;
  font-size: 1.1rem;
}

.search-section {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.search-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  max-width: 600px;
}

.search-input {
  flex: 1;
  padding: 14px 20px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 30px;
  color: #e0e0e0;
  font-size: 1rem;
  outline: none;
  transition: all 0.3s ease;
}

.search-input:focus {
  border-color: #9c27b0;
  background: rgba(255, 255, 255, 0.08);
}

.search-input::placeholder {
  color: #666;
}

.search-btn {
  padding: 14px 24px;
  background: linear-gradient(135deg, #9c27b0, #00bcd4);
  border: none;
  border-radius: 30px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(156, 39, 176, 0.4);
}

.search-btn-icon {
  font-size: 1.1rem;
}

.filter-section {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
  justify-content: center;
  margin-bottom: 30px;
}

.filter-select {
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  color: #e0e0e0;
  outline: none;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-select:hover {
  border-color: #9c27b0;
}

.filter-select option {
  background: #1a1a3a;
  color: #e0e0e0;
}

.reset-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: rgba(77, 208, 225, 0.2);
  border: 1px solid rgba(77, 208, 225, 0.4);
  border-radius: 20px;
  color: #4dd0e1;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  background: rgba(77, 208, 225, 0.3);
  transform: translateY(-2px);
}

.reset-icon {
  font-size: 1rem;
}

.blog-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 30px;
}

.blog-item {
  display: flex;
  gap: 20px;
  background: rgba(22, 22, 41, 0.7);
  border-radius: 16px;
  overflow: hidden;
  text-decoration: none;
  color: inherit;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(15px);
  min-height: 140px;
}

.blog-item:hover {
  transform: translateX(6px);
  border-color: rgba(156, 39, 176, 0.4);
  box-shadow: 0 4px 20px rgba(156, 39, 176, 0.15);
}

.blog-item-cover {
  width: 200px;
  min-width: 200px;
  height: 140px;
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
  padding: 16px 16px 16px 0;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.blog-item-title {
  font-size: 1.15rem;
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

.blog-item:hover .blog-item-summary {
  color: #ccc;
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
  cursor: pointer;
  transition: all 0.2s ease;
}

.blog-tag-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.tag-filter-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: rgba(156, 39, 176, 0.15);
  border: 1px solid rgba(156, 39, 176, 0.3);
  border-radius: 20px;
  margin-bottom: 20px;
  justify-content: center;
}

.tag-filter-label {
  color: #ce93d8;
  font-size: 0.9rem;
}

.tag-filter-value {
  padding: 3px 12px;
  background: linear-gradient(90deg, #9c27b0, #e040fb);
  border-radius: 12px;
  color: white;
  font-size: 0.85rem;
  font-weight: 600;
}

.tag-filter-remove {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.1);
  color: #e0e0e0;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.tag-filter-remove:hover {
  background: rgba(231, 76, 60, 0.3);
  border-color: #e74c3c;
}

.blog-item-meta {
  display: flex;
  gap: 14px;
  font-size: 0.8rem;
  margin-top: auto;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #888;
}

.blog-item:hover .meta-item {
  color: #bbb;
}

.meta-item.like-btn {
  cursor: pointer;
  transition: all 0.2s ease;
}

.meta-item.like-btn:hover {
  transform: scale(1.15);
}

.meta-icon {
  font-size: 0.85rem;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(22, 22, 41, 0.7);
  border-radius: 16px;
  border: 1px solid rgba(156, 39, 176, 0.3);
}

.pagination-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #9c27b0, #00bcd4);
  border: none;
  border-radius: 20px;
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.pagination-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(156, 39, 176, 0.4);
}

.pagination-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.pagination-icon {
  font-size: 0.9rem;
}

.pagination-pages {
  display: flex;
  gap: 6px;
}

.page-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  color: #aaa;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
}

.page-btn:hover {
  border-color: #9c27b0;
  color: #e0e0e0;
}

.page-btn.active {
  background: linear-gradient(135deg, #9c27b0, #00bcd4);
  border-color: transparent;
  color: white;
  font-weight: 700;
}

.empty-state {
  text-align: center;
  padding: 100px 20px;
  color: #888;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 1.5rem;
  color: #e0e0e0;
  margin-bottom: 10px;
}

.empty-state p {
  font-size: 1rem;
  color: #888;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-in {
  animation: fadeIn 0.4s ease-out both;
}

@media (max-width: 768px) {
  .blog-list-container {
    padding: 80px 12px 30px;
  }

  .blog-item {
    flex-direction: column;
    min-height: auto;
  }

  .blog-item-cover {
    width: 100%;
    min-width: unset;
    height: 160px;
  }

  .blog-item-info {
    padding: 14px;
  }

  .filter-section {
    gap: 8px;
  }

  .filter-select {
    padding: 8px 12px;
    font-size: 0.85rem;
  }

  .pagination {
    flex-wrap: wrap;
    gap: 10px;
  }

  .pagination-pages {
    order: -1;
    width: 100%;
    justify-content: center;
  }
}
</style>
