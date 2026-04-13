<template>
  <div class="user-center" :class="{ 'animate-fade-in': true }">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <div class="header-content">
          <div class="title-wrapper">
            <h1 class="page-title">
              <span class="title-icon">👤</span>
              个人中心
            </h1>
          </div>
        </div>
      </div>
      
      <!-- 用户信息卡片 -->
      <div class="user-card card-anim">
        <div class="user-header">
          <div class="user-avatar-wrapper">
            <div class="avatar-frame">
              <img :src="user.avatar || '/images/default-avatar.svg'" :alt="user.nickname" class="avatar-img" />
              <div class="avatar-glow"></div>
            </div>
          </div>
          <div class="user-info">
            <h2 class="user-name">{{ user.username || '用户' }}</h2>
            <p class="user-id">ID: {{ user.id }}</p>
            <p class="user-bio">{{ user.bio || '用户暂未设置描述信息' }}</p>
            <p class="user-email">{{ user.email }}</p>
          </div>
          <div class="user-active-days">
            <div class="active-days-icon">🔥</div>
            <div class="active-days-info">
              <div class="active-days-value">{{ activeDays }}</div>
              <div class="active-days-label">累计活跃天数</div>
            </div>
          </div>
        </div>
        
        <div class="user-stats">
          <div class="stat-item stat-articles">
            <div class="stat-icon">📝</div>
            <span class="stat-value">{{ user.articleCount || 0 }}</span>
            <span class="stat-label">文章数</span>
          </div>
          <div class="stat-item stat-likes">
            <div class="stat-icon">❤</div>
            <span class="stat-value">{{ user.likeCount || 0 }}</span>
            <span class="stat-label">点赞数</span>
          </div>
          <div class="stat-item stat-collects">
            <div class="stat-icon">⭐</div>
            <span class="stat-value">{{ user.collectCount || 0 }}</span>
            <span class="stat-label">收藏数</span>
          </div>
          <div class="stat-item stat-followers" @click="viewFollowers" title="查看粉丝">
            <div class="stat-icon">👥</div>
            <span class="stat-value">{{ user.followersCount || 0 }}</span>
            <span class="stat-label">粉丝</span>
            <div class="stat-tooltip">查看详情</div>
          </div>
          <div class="stat-item stat-following" @click="viewFollowing" title="查看关注">
            <div class="stat-icon">🔗</div>
            <span class="stat-value">{{ user.followingCount || 0 }}</span>
            <span class="stat-label">关注</span>
            <div class="stat-tooltip">查看详情</div>
          </div>
        </div>
        
        <div class="user-actions" v-if="isOwnProfile">
          <button @click="editProfile" class="action-btn primary">编辑资料</button>
          <button @click="manageBlogs" class="action-btn primary">管理博客</button>
        </div>
      </div>
      
      <!-- 已关注模块 -->
      <div class="section-card card-anim">
        <div class="section-header">
          <div class="section-title-wrapper">
            <h2 class="section-title">
              <span class="title-icon">👥</span>
              已关注博主
            </h2>
            <span class="count-badge">{{ following.length }} 位</span>
          </div>
          <div class="actions">
          <button @click="viewAllFollowing" class="action-btn small primary">全部</button>
        </div>
        </div>
        
        <div v-if="following.length > 0" class="following-list">
          <div v-for="author in following" :key="author.id" class="author-item">
            <div class="author-avatar">
              <img :src="author.avatar || '/images/default-avatar.svg'" :alt="author.nickname" />
            </div>
            <div class="author-info">
              <h3 class="author-name">{{ author.username }}</h3>
              <p class="author-stats">
                <span :title="author.articleCount + ' 篇文章'">📝 {{ author.articleCount || 0 }}</span>
                <span :title="author.likeCount + ' 个点赞'">❤ {{ author.likeCount || 0 }}</span>
              </p>
            </div>
            <div class="author-actions">
              <button @click="viewAuthorProfile(author)" class="message-btn">查看主页</button>
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
      
      <!-- 点赞博客模块 -->
      <div class="section-card card-anim">
        <div class="section-header">
          <div class="section-title-wrapper">
            <h2 class="section-title">
              <span class="title-icon">❤</span>
              点赞的博客
            </h2>
            <span class="count-badge">{{ likedBlogs.length }} 篇</span>
          </div>
          <div class="actions">
          <button @click="viewAllLikedBlogs" class="action-btn small primary">全部</button>
        </div>
        </div>
        
        <div v-if="likedBlogs.length > 0" class="blog-list">
          <router-link v-for="blog in likedBlogs" :key="blog.id" :to="`/blog/${blog.id}`" class="blog-item">
            <div class="blog-cover">
              <img :src="blog.coverImage || '/images/default-cover.svg'" :alt="blog.title" />
              <div class="cover-overlay">
                <span class="cover-badge">❤</span>
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
            <button @click.stop="unlike(blog.id)" class="unlike-btn">取消点赞</button>
          </router-link>
        </div>
        
        <div v-else class="empty-state">
          <div class="empty-icon">❤</div>
          <p>暂无点赞的博客</p>
        </div>
      </div>
      
      <!-- 收藏博客模块 -->
      <div class="section-card card-anim">
        <div class="section-header">
          <div class="section-title-wrapper">
            <h2 class="section-title">
              <span class="title-icon">⭐</span>
              收藏的博客
            </h2>
            <span class="count-badge">{{ collectedBlogs.length }} 篇</span>
          </div>
          <div class="actions">
          <button @click="viewAllCollectedBlogs" class="action-btn small primary">全部</button>
        </div>
        </div>
        
        <div v-if="collectedBlogs.length > 0" class="blog-list">
          <router-link v-for="blog in collectedBlogs" :key="blog.id" :to="`/blog/${blog.id}`" class="blog-item">
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
          </router-link>
        </div>
        
        <div v-else class="empty-state">
          <div class="empty-icon">⭐</div>
          <p>暂无收藏的博客</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getUserInfo, getLikedBlogs, getCollectedBlogs, getFollowing, unfollowUser } from '@/api/user'
import { toggleLike, toggleCollect } from '@/api/blog'

const router = useRouter()
const route = useRoute()

const user = ref({})
const following = ref([])
const likedBlogs = ref([])
const collectedBlogs = ref([])

const isOwnProfile = computed(() => {
  const storedUser = JSON.parse(localStorage.getItem('user') || '{}')
  const targetUserId = route.query.userId
  return !targetUserId || Number(targetUserId) === storedUser.id
})

const activeDays = computed(() => {
  const registerDateStr = user.value.createTime || new Date().toISOString()
  const registerDate = new Date(registerDateStr)
  const today = new Date()
  const diffTime = Math.abs(today - registerDate)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return diffDays
})

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

const fetchUserInfo = async () => {
  const targetUserId = route.query.userId
  const storedUser = JSON.parse(localStorage.getItem('user') || '{}')
  
  if (targetUserId && Number(targetUserId) !== storedUser.id) {
    try {
      const res = await getUserInfo(Number(targetUserId))
      if (res.data) {
        user.value = res.data
      }
    } catch (e) {
      console.error('获取用户信息失败', e)
    }
    return
  }
  
  if (!storedUser.id) {
    router.push('/login')
    return
  }
  try {
    const res = await getUserInfo(storedUser.id)
    if (res.data) {
      user.value = res.data
      localStorage.setItem('user', JSON.stringify(res.data))
    }
  } catch (e) {
    console.error('获取用户信息失败', e)
    user.value = storedUser
  }
}

const fetchFollowing = async () => {
  const storedUser = JSON.parse(localStorage.getItem('user') || '{}')
  if (!storedUser.id) return
  try {
    const res = await getFollowing(storedUser.id)
    following.value = (res.data || []).slice(0, 2)
  } catch (e) {
    console.error('获取关注列表失败', e)
  }
}

const fetchLikedBlogs = async () => {
  const storedUser = JSON.parse(localStorage.getItem('user') || '{}')
  if (!storedUser.id) return
  try {
    const res = await getLikedBlogs(storedUser.id)
    likedBlogs.value = (res.data || []).slice(0, 2)
  } catch (e) {
    console.error('获取点赞博客失败', e)
  }
}

const fetchCollectedBlogs = async () => {
  const storedUser = JSON.parse(localStorage.getItem('user') || '{}')
  if (!storedUser.id) return
  try {
    const res = await getCollectedBlogs(storedUser.id)
    collectedBlogs.value = (res.data || []).slice(0, 2)
  } catch (e) {
    console.error('获取收藏博客失败', e)
  }
}

const editProfile = () => {
  router.push('/user-edit')
}

const logout = () => {
  localStorage.removeItem('user')
  router.push('/')
}

const unlike = async (blogId) => {
  const storedUser = JSON.parse(localStorage.getItem('user') || '{}')
  if (!storedUser.id) return
  try {
    await toggleLike(blogId, storedUser.id)
    likedBlogs.value = likedBlogs.value.filter(blog => blog.id !== blogId)
  } catch (error) {
    console.error('取消点赞失败:', error)
  }
}

const uncollect = async (blogId) => {
  const storedUser = JSON.parse(localStorage.getItem('user') || '{}')
  if (!storedUser.id) return
  try {
    await toggleCollect(blogId, storedUser.id, 'remove')
    collectedBlogs.value = collectedBlogs.value.filter(blog => blog.id !== blogId)
  } catch (error) {
    console.error('取消收藏失败:', error)
  }
}

const unfollow = async (authorId) => {
  const storedUser = JSON.parse(localStorage.getItem('user') || '{}')
  if (!storedUser.id) return
  try {
    await unfollowUser(storedUser.id, authorId)
    following.value = following.value.filter(author => author.id !== authorId)
  } catch (error) {
    console.error('取消关注失败:', error)
  }
}

const manageBlogs = () => {
  router.push('/blog-management')
}

const viewAllFollowing = () => {
  router.push('/all-following')
}

const viewAllLikedBlogs = () => {
  router.push('/liked-blogs')
}

const viewAllCollectedBlogs = () => {
  router.push('/collected-blogs')
}

const viewFollowers = () => {
  router.push('/followers')
}

const viewFollowing = () => {
  router.push('/following')
}

const viewAuthorProfile = (author) => {
  router.push(`/author/${author.id}`)
}

onMounted(async () => {
  await Promise.all([
    fetchUserInfo(),
    fetchFollowing(),
    fetchLikedBlogs(),
    fetchCollectedBlogs()
  ])
})
</script>

<style scoped>
/* 简化动画，提高性能 */
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

.user-center {
  min-height: 100%;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 25%, #2a2a4a 50%, #1a1a3a 75%, #0a0a1a 100%);
  padding: 2rem 0;
  position: relative;
  color: #fff;
  overflow: hidden;
}

.user-center::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 30%, rgba(123, 31, 162, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(25, 118, 210, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(0, 188, 212, 0.1) 0%, transparent 50%),
    linear-gradient(45deg, transparent 49%, rgba(123, 31, 162, 0.1) 50%, transparent 51%),
    linear-gradient(-45deg, transparent 49%, rgba(25, 118, 210, 0.1) 50%, transparent 51%);
  z-index: 0;
  pointer-events: none;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
  position: relative;
  z-index: 1;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 3rem;
  animation: fadeIn 0.3s ease-out;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 2rem;
}

.title-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.page-title {
  font-size: 2.5rem;
  font-weight: bold;
  color: white;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  animation: fadeIn 0.4s ease-out;
}

.title-icon {
  font-size: 2rem;
}

.page-description {
  color: rgba(255, 255, 255, 0.9);
  font-size: 1.1rem;
  margin: 0;
  animation: fadeIn 0.5s ease-out;
}

.page-actions {
  animation: fadeIn 0.6s ease-out;
}

/* 用户卡片 */
.user-card {
  background: rgba(10, 10, 26, 0.85);
  backdrop-filter: blur(15px);
  border-radius: 20px;
  padding: 2.5rem;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(123, 31, 162, 0.3);
  margin-bottom: 2rem;
  animation: fadeIn 0.5s ease-out 0.1s both;
  border: 1px solid rgba(123, 31, 162, 0.4);
  position: relative;
  overflow: hidden;
}

.user-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, #9c27b0, #03a9f4, transparent);
  z-index: 1;
}

.card-anim {
  animation: fadeIn 0.5s ease-out both;
}

.card-anim:nth-child(1) { animation-delay: 0.1s; }
.card-anim:nth-child(2) { animation-delay: 0.2s; }
.card-anim:nth-child(3) { animation-delay: 0.3s; }
.card-anim:nth-child(4) { animation-delay: 0.4s; }

.user-header {
  display: flex;
  align-items: center;
  gap: 2.5rem;
  margin-bottom: 2rem;
}

.user-avatar-wrapper {
  flex-shrink: 0;
}

.avatar-frame {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  padding: 5px;
  background: linear-gradient(135deg, #9c27b0 0%, #03a9f4 100%);
  box-shadow: 
    0 8px 32px rgba(123, 31, 162, 0.4),
    inset 0 0 0 2px rgba(255, 255, 255, 0.2);
  position: relative;
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid rgba(10, 10, 26, 0.8);
  padding: 3px;
  background: rgba(10, 10, 26, 0.8);
}

.avatar-glow {
  position: absolute;
  top: -5px;
  left: -5px;
  right: -5px;
  bottom: -5px;
  border-radius: 50%;
  background: linear-gradient(45deg, #9c27b0, #03a9f4, #9c27b0, #03a9f4);
  animation: rotate 8s linear infinite;
  z-index: -1;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
  background: linear-gradient(135deg, #9c27b0 0%, #03a9f4 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 20px rgba(123, 31, 162, 0.5);
}

.user-id {
  color: #a0a0a0;
  font-size: 0.95rem;
  margin-bottom: 0.3rem;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

.user-bio {
  color: #aaa;
  font-size: 0.9rem;
  margin-bottom: 0.3rem;
  max-width: 300px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-email {
  color: #888;
  font-size: 0.9rem;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

/* 活跃天数 */
.user-active-days {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  background: linear-gradient(135deg, rgba(255, 193, 7, 0.1), rgba(255, 152, 0, 0.1));
  border: 1px solid rgba(255, 193, 7, 0.3);
  border-radius: 16px;
  box-shadow: 0 4px 15px rgba(255, 193, 7, 0.2);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.user-active-days:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(255, 193, 7, 0.3);
  border-color: rgba(255, 193, 7, 0.5);
}

.active-days-icon {
  font-size: 2rem;
  animation: pulse 2s ease-in-out infinite;
}

.active-days-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
}

.active-days-value {
  font-size: 1.8rem;
  font-weight: bold;
  color: #ffc107;
  text-shadow: 0 0 10px rgba(255, 193, 7, 0.5);
}

.active-days-label {
  font-size: 0.85rem;
  color: #ffb300;
  text-shadow: 0 0 5px rgba(255, 193, 7, 0.3);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 统计卡片 */
.user-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
  padding: 2rem;
  background: rgba(40, 40, 40, 0.6);
  border-radius: 16px;
  border: 1px solid rgba(123, 31, 162, 0.3);
}

.stat-item {
  text-align: center;
  padding: 1.5rem 1rem;
  background: rgba(10, 10, 26, 0.8);
  border-radius: 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(123, 31, 162, 0.3);
}

.stat-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #9c27b0, #03a9f4);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.stat-item:hover::before {
  transform: scaleX(1);
}

.stat-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(123, 31, 162, 0.3);
  border-color: rgba(123, 31, 162, 0.5);
}

.stat-icon {
  display: block;
  font-size: 2rem;
  margin-bottom: 0.5rem;
  animation: pulse 2s ease-in-out infinite;
}

.stat-value {
  display: block;
  font-size: 2.2rem;
  font-weight: bold;
  background: linear-gradient(135deg, #9c27b0 0%, #03a9f4 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  display: block;
  font-size: 0.85rem;
  color: #888;
  margin-top: 0.5rem;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

.stat-tooltip {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) scale(0);
  background: rgba(123, 31, 162, 0.9);
  color: white;
  padding: 10px 20px;
  border-radius: 25px;
  font-weight: bold;
  font-size: 0.9rem;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  white-space: nowrap;
  z-index: 2;
  box-shadow: 0 4px 15px rgba(123, 31, 162, 0.4);
}

.stat-item:hover .stat-tooltip {
  opacity: 1;
  visibility: visible;
  transform: translate(-50%, -50%) scale(1);
}

.stat-item:hover .stat-icon, .stat-item:hover .stat-value, .stat-item:hover .stat-label {
  opacity: 0.7;
  transition: opacity 0.3s ease;
}

.stat-item:hover {
  cursor: pointer;
}

/* 用户操作按钮 */
.user-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1rem;
}

/* 通用按钮样式 */
.action-btn {
  padding: 0.8rem 1.8rem;
  border: none;
  border-radius: 30px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.95rem;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.action-btn.primary {
  background: linear-gradient(135deg, #9c27b0 0%, #03a9f4 100%);
  color: white;
}

.action-btn.primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(123, 31, 162, 0.4);
}

.action-btn.danger {
  background: linear-gradient(135deg, #ff4757 0%, #ff6b7a 100%);
  color: white;
}

.action-btn.danger:hover {
  background: linear-gradient(135deg, #ff3344 0%, #ff5261 100%);
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(255, 71, 87, 0.4);
}

.action-btn.small {
  padding: 0.5rem 1.2rem;
  font-size: 0.85rem;
  box-shadow: none;
}

.action-btn.small:hover {
  transform: scale(1.05);
}

/* 通用卡片样式 */
.section-card {
  background: rgba(10, 10, 26, 0.85);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 1.5rem;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(123, 31, 162, 0.3);
  margin-bottom: 2rem;
  border: 1px solid rgba(123, 31, 162, 0.4);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.section-title-wrapper {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex: 1;
}

.section-title {
  font-size: 1.6rem;
  font-weight: bold;
  color: #e0e0e0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.title-icon {
  font-size: 1.5rem;
}

.count-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0.4rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
  box-shadow: 0 2px 10px rgba(102, 126, 234, 0.3);
}

.actions {
  display: flex;
  gap: 0.5rem;
}

/* 关注列表 */
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
  background: rgba(40, 40, 40, 0.8);
  border-radius: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid rgba(123, 31, 162, 0.3);
}

.author-item:hover {
  transform: translateX(5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  border-color: rgba(123, 31, 162, 0.5);
  background: rgba(50, 50, 50, 0.9);
}

.author-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  border: 3px solid white;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
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
  color: #999;
  font-size: 0.85rem;
  margin-bottom: 0.3rem;
}

.author-stats {
  display: flex;
  gap: 1rem;
  font-size: 0.85rem;
  color: #666;
}

.author-stats span {
  background: rgba(102, 126, 234, 0.1);
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
  color: #667eea;
}

.author-actions {
  display: flex;
  gap: 0.8rem;
  margin-left: auto;
}

.message-btn {
  padding: 0.5rem 1.2rem;
  border: none;
  background: linear-gradient(135deg, #4dd0e1 0%, #00bcd4 100%);
  color: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  font-size: 0.9rem;
  box-shadow: 0 4px 15px rgba(77, 208, 225, 0.2);
  min-width: 90px;
}

.message-btn:hover {
  background: linear-gradient(135deg, #26c6da 0%, #00acc1 100%);
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(77, 208, 225, 0.3);
}

.unfollow-btn {
  padding: 0.5rem 1.2rem;
  border: none;
  background: linear-gradient(135deg, #ff4757 0%, #ff6b7a 100%);
  color: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  font-size: 0.9rem;
  box-shadow: 0 4px 15px rgba(255, 71, 87, 0.2);
}

.unfollow-btn:hover {
  background: linear-gradient(135deg, #ff3344 0%, #ff5261 100%);
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(255, 71, 87, 0.3);
}

/* 博客列表 */
.blog-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.blog-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.2rem;
  background: rgba(40, 40, 40, 0.8);
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
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  border-color: rgba(123, 31, 162, 0.5);
  background: rgba(50, 50, 50, 0.9);
}

.blog-cover {
  width: 120px;
  height: 80px;
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

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.8), rgba(118, 75, 162, 0.8));
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
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

.blog-summary {
  color: #a0a0a0;
  font-size: 0.9rem;
  margin-bottom: 0.8rem;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
}

.blog-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.85rem;
  color: #888;
  margin-bottom: 0.5rem;
  text-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
}

.blog-tags {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.tag {
  background: rgba(123, 31, 162, 0.2);
  color: #9c27b0;
  padding: 0.3rem 0.8rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
  text-shadow: 0 0 5px rgba(123, 31, 162, 0.3);
  border: 1px solid rgba(123, 31, 162, 0.3);
  transition: all 0.3s ease;
}

.tag:hover {
  background: rgba(123, 31, 162, 0.3);
  color: #ba68c8;
  border-color: rgba(123, 31, 162, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(123, 31, 162, 0.3);
}

.unlike-btn,
.uncollect-btn {
  margin-left: auto;
  padding: 0.5rem 1rem;
  border: none;
  background: linear-gradient(135deg, #ff4757 0%, #ff6b7a 100%);
  color: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  font-size: 0.85rem;
  box-shadow: 0 4px 15px rgba(255, 71, 87, 0.2);
}

.unlike-btn:hover,
.uncollect-btn:hover {
  background: linear-gradient(135deg, #ff3344 0%, #ff5261 100%);
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(255, 71, 87, 0.3);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 3rem 1rem;
  color: #999;
}

.empty-icon {
  font-size: 4rem;
  display: block;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.empty-state p {
  font-size: 1.1rem;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-center {
    padding: 1rem 0;
  }

  .container {
    padding: 0 1rem;
  }

  .page-title {
    font-size: 2rem;
  }

  .user-header {
    flex-direction: column;
    text-align: center;
    align-items: center;
  }

  .user-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .blog-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .blog-cover {
    width: 100%;
    height: 180px;
  }

  .blog-info {
    width: 100%;
  }

  .blog-meta {
    flex-direction: column;
    gap: 0.5rem;
  }

  .actions {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .user-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .stat-item {
    padding: 1rem 0.5rem;
  }

  .stat-value {
    font-size: 1.8rem;
  }
}
</style>