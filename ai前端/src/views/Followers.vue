<template>
  <div class="followers-container">
    <button class="back-btn" @click="goBack">← 返回</button>
    <h1 class="page-title">
      <span class="title-icon">👥</span>
      我的粉丝
    </h1>
    
    <div v-if="followers.length > 0" class="followers-list">
      <div v-for="follower in followers" :key="follower.id" class="follower-item">
        <div class="follower-avatar">
          <img :src="follower.avatar || '/images/default-avatar.svg'" :alt="follower.username" />
        </div>
        <div class="follower-info">
          <h3 class="follower-name">{{ follower.username }}</h3>
          <p class="follower-bio">{{ follower.bio || '该用户暂无简介' }}</p>
          <div class="follower-stats">
            <span class="stat">{{ follower.articleCount }} 文章</span>
            <span class="stat">{{ follower.followerCount }} 粉丝</span>
          </div>
        </div>
        <div class="follower-actions">
          <button v-if="!follower.isFollowing" @click="follow(follower.id)" class="action-btn primary">关注</button>
          <button v-else @click="unfollow(follower.id)" class="action-btn secondary">已关注</button>
          <button @click="viewProfile(follower)" class="action-btn secondary">查看主页</button>
        </div>
      </div>
    </div>
    
    <div v-else class="empty-state">
      <div class="empty-icon">👥</div>
      <p>暂无粉丝，快去创作精彩内容吸引粉丝吧！</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFollowers, followUser, unfollowUser } from '@/api/user'

const router = useRouter()
const followers = ref([])

const goBack = () => {
  router.back()
}

const loadFollowers = async () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (!user.id) return
  try {
    const res = await getFollowers(user.id)
    followers.value = res.data || []
  } catch (e) {
    console.error('获取粉丝列表失败', e)
  }
}

const follow = async (userId) => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (!user.id) return
  try {
    await followUser(user.id, userId)
    const follower = followers.value.find(f => f.id === userId)
    if (follower) {
      follower.isFollowing = true
    }
  } catch (e) {
    console.error('关注失败', e)
  }
}

const unfollow = async (userId) => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (!user.id) return
  try {
    await unfollowUser(user.id, userId)
    const follower = followers.value.find(f => f.id === userId)
    if (follower) {
      follower.isFollowing = false
    }
  } catch (e) {
    console.error('取消关注失败', e)
  }
}

const viewProfile = (follower) => {
  router.push(`/author/${follower.id}`)
}

onMounted(() => {
  loadFollowers()
})
</script>

<style scoped>
.followers-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 25%, #2a2a4a 50%, #1a1a3a 75%, #0a0a1a 100%);
  padding: 20px;
  position: relative;
  overflow: auto;
  box-sizing: border-box;
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

.page-title {
  font-size: 2rem;
  font-weight: bold;
  color: white;
  margin: 2rem 0 2rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  text-align: center;
  justify-content: center;
}

.title-icon {
  font-size: 1.8rem;
}

.followers-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  max-width: 800px;
  margin: 0 auto;
}

.follower-item {
  display: flex;
  gap: 1.5rem;
  padding: 1.5rem;
  background: rgba(40, 40, 40, 0.6);
  border-radius: 16px;
  border: 1px solid rgba(123, 31, 162, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.follower-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent, rgba(123, 31, 162, 0.05), transparent);
  transform: translateX(-100%);
  transition: transform 0.5s ease;
}

.follower-item:hover::before {
  transform: translateX(100%);
}

.follower-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(123, 31, 162, 0.3);
  border-color: rgba(123, 31, 162, 0.5);
}

.follower-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.follower-item:hover .follower-avatar {
  transform: scale(1.05);
}

.follower-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.follower-info {
  flex: 1;
  min-width: 0;
}

.follower-name {
  font-size: 1.2rem;
  font-weight: bold;
  color: #e0e0e0;
  margin-bottom: 0.5rem;
  line-height: 1.4;
}

.follower-bio {
  color: #a0a0a0;
  font-size: 0.95rem;
  margin-bottom: 1rem;
  line-height: 1.6;
}

.follower-stats {
  display: flex;
  gap: 1.5rem;
  font-size: 0.85rem;
  color: #888;
}

.follower-actions {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  flex-shrink: 0;
}

.action-btn {
  padding: 0.8rem 1.2rem;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  white-space: nowrap;
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
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(123, 31, 162, 0.4);
}

.action-btn.secondary {
  background: rgba(123, 31, 162, 0.2);
  border: 1px solid rgba(123, 31, 162, 0.5);
  color: #e0e0e0;
}

.action-btn.secondary:hover {
  background: rgba(123, 31, 162, 0.3);
  border-color: #9c27b0;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(123, 31, 162, 0.2);
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #888;
  max-width: 600px;
  margin: 0 auto;
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

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .followers-container {
    padding: 1rem;
  }
  
  .page-title {
    font-size: 1.5rem;
  }
  
  .follower-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .follower-actions {
    flex-direction: row;
    width: 100%;
    justify-content: flex-end;
  }
}
</style>