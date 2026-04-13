<template>
  <div class="all-following-container">
    <button class="back-btn" @click="goBack">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M19 12H5M12 19l-7-7 7-7"/>
      </svg>
      返回
    </button>

    <div class="page-header">
      <h1 class="page-title">
        <span class="title-icon">👥</span>
        已关注博主
      </h1>
      <span class="count-badge">{{ following.length }} 位</span>
    </div>

    <div v-if="following.length > 0" class="following-list">
      <div v-for="author in following" :key="author.id" class="author-item">
        <div class="author-avatar">
          <img :src="author.avatar || '/images/default-avatar.svg'" :alt="author.nickname" />
        </div>
        <div class="author-info">
          <h3 class="author-name">{{ author.nickname }}</h3>
          <p class="author-id">ID: {{ author.id }}</p>
          <p class="author-stats">
            <span :title="author.articleCount + ' 篇文章'">📝 {{ author.articleCount || 0 }}</span>
            <span :title="author.likeCount + ' 个点赞'">❤ {{ author.likeCount || 0 }}</span>
          </p>
        </div>
        <div class="author-actions">
          <button @click="sendMessage(author)" class="message-btn">发消息</button>
          <button @click="unfollow(author.id)" class="unfollow-btn">取消关注</button>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <div class="empty-icon">👥</div>
      <p>暂无关注的博主</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFollowing, unfollowUser } from '@/api/user'

const router = useRouter()
const following = ref([])

const goBack = () => {
  router.back()
}

const sendMessage = (author) => {
  router.push(`/chat/${author.id}`)
}

const unfollow = async (authorId) => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    await unfollowUser(user.id, authorId)
    const index = following.value.findIndex(a => a.id === authorId)
    if (index > -1) {
      following.value.splice(index, 1)
    }
  } catch (error) {
    console.error('取消关注失败:', error)
  }
}

const fetchFollowing = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!user.id) {
      router.push('/login')
      return
    }
    const res = await getFollowing(user.id)
    following.value = res.data || []
  } catch (error) {
    console.error('获取关注列表失败:', error)
    following.value = []
  }
}

onMounted(() => {
  fetchFollowing()
})
</script>

<style scoped>
.all-following-container {
  min-height: 100vh;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.back-btn {
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
  margin-bottom: 20px;
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

.following-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.author-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.2rem;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid rgba(123, 31, 162, 0.3);
}

.author-item:hover {
  transform: translateX(5px);
  box-shadow: 0 4px 20px rgba(123, 31, 162, 0.3);
  border-color: rgba(123, 31, 162, 0.6);
  background: rgba(123, 31, 162, 0.1);
}

.author-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  border: 3px solid rgba(123, 31, 162, 0.5);
  box-shadow: 0 4px 15px rgba(123, 31, 162, 0.3);
}

.author-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.author-info {
  flex: 1;
  min-width: 0;
}

.author-name {
  font-size: 1.15rem;
  font-weight: bold;
  color: #4dd0e1;
  margin-bottom: 0.3rem;
  text-shadow: 0 0 12px rgba(77, 208, 225, 0.4);
}

.author-id {
  color: #a0a0a0;
  font-size: 0.85rem;
  margin-bottom: 0.3rem;
}

.author-stats {
  display: flex;
  gap: 1rem;
  font-size: 0.85rem;
  color: #b0b0b0;
}

.author-stats span {
  background: rgba(156, 39, 176, 0.2);
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
  color: #9c27b0;
}

.author-actions {
  display: flex;
  gap: 0.8rem;
  margin-left: auto;
}

.message-btn,
.unfollow-btn {
  padding: 0.5rem 1.2rem;
  border: none;
  color: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  font-size: 0.9rem;
  min-width: 100px;
  text-align: center;
}

.message-btn {
  background: linear-gradient(135deg, #4dd0e1 0%, #00bcd4 100%);
  box-shadow: 0 4px 15px rgba(77, 208, 225, 0.2);
}

.message-btn:hover {
  background: linear-gradient(135deg, #26c6da 0%, #00acc1 100%);
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(77, 208, 225, 0.3);
}

.unfollow-btn {
  background: linear-gradient(135deg, #ff4757 0%, #ff6b7a 100%);
  box-shadow: 0 4px 15px rgba(255, 71, 87, 0.2);
}

.unfollow-btn:hover {
  background: linear-gradient(135deg, #ff3344 0%, #ff5261 100%);
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(255, 71, 87, 0.3);
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #a0a0a0;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.empty-state p {
  font-size: 1.2rem;
}
</style>