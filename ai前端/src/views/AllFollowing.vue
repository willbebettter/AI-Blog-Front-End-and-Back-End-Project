<template>
  <div class="all-following-container">
    <button class="back-btn" @click="goBack">← 返回</button>
    <div class="all-following-content">
      <h2 class="page-title">👥 已关注的博主</h2>

      <div class="author-grid" v-if="followingAuthors.length > 0">
        <div
          v-for="author in followingAuthors"
          :key="author.id"
          class="author-card"
        >
          <div class="author-avatar">
            <img :src="author.avatar || '/images/default-avatar.svg'" :alt="author.nickname" />
          </div>
          <h4 class="author-name">{{ author.nickname }}</h4>
          <p class="author-stats">{{ author.articleCount || 0 }} 篇文章 · {{ author.likeCount || 0 }} 次点赞</p>
          <div class="author-actions">
            <button @click="viewAuthorBlogs(author)" class="action-btn secondary">查看博客</button>
            <button @click="sendMessage(author)" class="action-btn primary">发消息</button>
            <button @click="unfollow(author.id)" class="action-btn danger">取消关注</button>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <h3>暂无关注的博主</h3>
        <p>去发现并关注感兴趣的博主吧</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFollowing, unfollowUser } from '@/api/user'

const router = useRouter()
const followingAuthors = ref([])

const goBack = () => {
  router.back()
}

const fetchFollowing = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!user.id) {
      router.push('/login')
      return
    }
    const res = await getFollowing(user.id)
    followingAuthors.value = res.data || []
  } catch (error) {
    console.error('获取关注列表失败:', error)
    followingAuthors.value = []
  }
}

const viewAuthorBlogs = (author) => {
  router.push(`/author/${author.id}`)
}

const sendMessage = (author) => {
  router.push(`/chat/${author.id}`)
}

const unfollow = async (authorId) => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    await unfollowUser(user.id, authorId)
    followingAuthors.value = followingAuthors.value.filter(a => a.id !== authorId)
  } catch (error) {
    console.error('取消关注失败:', error)
  }
}

onMounted(() => {
  fetchFollowing()
})
</script>

<style scoped>
.all-following-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 50%, #0a0a1a 100%);
  padding: 20px;
  position: relative;
  overflow: auto;
  box-sizing: border-box;
}

.all-following-container::before {
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
  padding: 10px 20px;
  background: rgba(123, 31, 162, 0.3);
  border: 1px solid rgba(123, 31, 162, 0.5);
  border-radius: 25px;
  color: #e0e0e0;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.back-btn:hover {
  background: rgba(123, 31, 162, 0.5);
  border-color: #9c27b0;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(123, 31, 162, 0.4);
}

.all-following-content {
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
  min-height: calc(100vh - 40px);
  padding-top: 80px;
}

.page-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 20px;
  background: linear-gradient(90deg, #9c27b0, #03a9f4, #9c27b0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 30px rgba(123, 31, 162, 0.6);
  letter-spacing: 2px;
}

.author-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.author-card {
  background: rgba(10, 10, 26, 0.85);
  backdrop-filter: blur(15px);
  border-radius: 16px;
  padding: 1.5rem;
  border: 1px solid rgba(123, 31, 162, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.author-card::before {
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

.author-card:hover::before {
  transform: translateX(100%);
}

.author-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(123, 31, 162, 0.3);
  border-color: rgba(123, 31, 162, 0.5);
}

.author-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 auto 1rem;
  border: 3px solid rgba(123, 31, 162, 0.5);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.author-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.author-name {
  font-size: 1.3rem;
  color: #4dd0e1;
  text-align: center;
  margin-bottom: 0.5rem;
  text-shadow: 0 0 12px rgba(77, 208, 225, 0.4);
}

.author-stats {
  font-size: 0.9rem;
  color: #888;
  text-align: center;
  margin-bottom: 1.5rem;
}

.author-actions {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.action-btn {
  padding: 0.6rem 1.2rem;
  border: none;
  border-radius: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.action-btn.primary {
  background: linear-gradient(135deg, #4dd0e1 0%, #00bcd4 100%);
  color: white;
}

.action-btn.primary:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 15px rgba(77, 208, 225, 0.3);
}

.action-btn.secondary {
  background: rgba(40, 40, 40, 0.8);
  color: #e0e0e0;
  border: 1px solid rgba(123, 31, 162, 0.3);
}

.action-btn.secondary:hover {
  background: rgba(50, 50, 50, 1);
  border-color: #9c27b0;
}

.action-btn.danger {
  background: rgba(255, 71, 87, 0.2);
  color: #ff6b7a;
  border: 1px solid rgba(255, 71, 87, 0.3);
}

.action-btn.danger:hover {
  background: rgba(255, 71, 87, 0.4);
  border-color: #ff3b30;
}

.empty-state {
  text-align: center;
  padding: 150px 20px;
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
  margin-top: 20px;
}
</style>