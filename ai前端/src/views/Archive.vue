<template>
  <div class="archive-container">
    <div class="archive-header">
      <h1>📂 文章归档</h1>
      <p>共计 {{ totalPosts }} 篇文章</p>
    </div>

    <div class="archive-list">
      <div v-for="(year, index) in archives" :key="index" class="archive-year">
        <h2 class="year-title">{{ year.year }}</h2>
        <div class="archive-items">
          <div v-for="post in year.posts" :key="post.id" class="archive-item">
            <span class="post-date">{{ post.date }}</span>
            <router-link :to="`/blog/${post.id}`" class="post-link">
              {{ post.title }}
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getBlogList } from '@/api/blog'

const totalPosts = ref(0)
const archives = ref([])

const fetchArchives = async () => {
  try {
    const res = await getBlogList()
    const blogs = res.data || []
    totalPosts.value = blogs.length

    const grouped = {}
    blogs.forEach(blog => {
      const year = new Date(blog.createTime).getFullYear().toString()
      if (!grouped[year]) {
        grouped[year] = []
      }
      grouped[year].push({
        id: blog.id,
        title: blog.title,
        date: `${String(new Date(blog.createTime).getMonth() + 1).padStart(2, '0')}-${String(new Date(blog.createTime).getDate()).padStart(2, '0')}`
      })
    })

    archives.value = Object.entries(grouped)
      .map(([year, posts]) => ({ year, posts }))
      .sort((a, b) => b.year.localeCompare(a.year))
  } catch (e) {
    console.error('获取归档数据失败', e)
  }
}

onMounted(() => {
  fetchArchives()
})
</script>

<style scoped>
.archive-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.archive-header {
  margin-bottom: 40px;
  text-align: center;
}

.archive-year {
  margin-bottom: 30px;
}

.year-title {
  color: #333;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
  margin-bottom: 20px;
}

.archive-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.archive-item {
  display: flex;
  align-items: center;
  gap: 20px;
}

.post-date {
  color: #999;
  font-family: monospace;
}

.post-link {
  color: #409eff;
  text-decoration: none;
  font-size: 16px;
}

.post-link:hover {
  text-decoration: underline;
}
</style>
