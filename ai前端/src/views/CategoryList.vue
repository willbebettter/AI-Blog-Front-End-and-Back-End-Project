<template>
  <div class="category-list-container">
    <div class="category-list-content">
      <div class="category-header">
        <h1>📂 分类目录</h1>
        <p>浏览不同主题的博客文章</p>
      </div>

      <div class="category-grid">
        <div 
          v-for="category in categories" 
          :key="category.id" 
          class="category-card"
          @click="filterByCategory(category.name)"
        >
          <div class="category-icon">{{ getCategoryIcon(category.name) }}</div>
          <h3 class="category-name">{{ category.name }}</h3>
          <p class="category-description">{{ category.description }}</p>
          <span class="category-count">{{ category.count || 0 }} 篇文章</span>
        </div>
      </div>

      <div v-if="categories.length === 0" class="empty-state">
        <h3>暂无分类</h3>
        <p>管理员尚未创建任何分类</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCategories } from '@/api/blog'

const router = useRouter()
const categories = ref([])

const getCategoryIcon = (name) => {
  const icons = {
    '技术': '💻',
    '生活': '🏠',
    '编程': '👨‍💻',
    '设计': '🎨',
    '学习': '📚',
    '分享': '📝',
    '其他': '📦'
  }
  return icons[name] || '📄'
}

const fetchCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const filterByCategory = (categoryName) => {
  router.push(`/blog?category=${encodeURIComponent(categoryName)}`)
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.category-list-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f0f23 0%, #1a1a3e 100%);
  padding: 40px 20px;
}

.breadcrumb {
  padding: 15px 0;
  font-size: 0.9rem;
  color: #a0a0a0;
}

.breadcrumb-item {
  color: #4dd0e1;
  text-decoration: none;
}

.breadcrumb-item:hover {
  text-decoration: underline;
}

.current {
  color: #e0e0e0;
}

.category-list-content {
  max-width: 1200px;
  margin: 0 auto;
}

.category-header {
  text-align: center;
  margin-bottom: 40px;
}

.category-header h1 {
  font-size: 2rem;
  color: #4dd0e1;
  margin-bottom: 10px;
}

.category-header p {
  color: #a0a0a0;
  font-size: 1rem;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.category-card {
  background: rgba(255, 255, 255, 0.05);
  padding: 30px 20px;
  border-radius: 15px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(77, 208, 225, 0.1);
}

.category-card:hover {
  background: rgba(77, 208, 225, 0.1);
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(77, 208, 225, 0.2);
}

.category-icon {
  font-size: 3rem;
  display: block;
  margin-bottom: 15px;
}

.category-name {
  font-size: 1.3rem;
  color: #4dd0e1;
  margin-bottom: 10px;
}

.category-description {
  color: #888;
  font-size: 0.9rem;
  margin-bottom: 15px;
  line-height: 1.5;
}

.category-count {
  display: inline-block;
  padding: 5px 15px;
  background: rgba(77, 208, 225, 0.2);
  color: #4dd0e1;
  border-radius: 20px;
  font-size: 0.85rem;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #777;
}

.empty-state h3 {
  font-size: 1.5rem;
  color: #a0a0a0;
  margin-bottom: 10px;
}
</style>