<template>
  <div class="blog-detail-container" ref="containerRef">
    <button class="back-btn" @click="goBack">← 返回</button>

    <div v-if="blog" class="blog-content fade-in">
      <div class="blog-header">
        <h1 class="blog-title">{{ blog.title }}</h1>
        
        <div class="author-info">
          <div class="author-avatar">
            <img :src="blog.author?.avatar || '/images/default-avatar.svg'" alt="作者头像" />
          </div>
          <div class="author-details">
            <span class="author-name">{{ blog.author?.username || '作者' }}</span>
            <span class="author-bio">{{ blog.author?.bio || '暂无简介' }}</span>
          </div>
          <button 
            v-if="blog.authorId && !isOwnBlog" 
            class="profile-btn"
            @click="viewAuthorProfile"
          >
            查看主页
          </button>
          <template v-if="!isOwnBlog">
            <button class="follow-btn" v-if="!isFollowing" @click="toggleFollow">+ 关注</button>
            <button class="follow-btn following" v-else @click="toggleFollow">已关注</button>
          </template>
        </div>
        
        <div class="blog-meta-info">
          <div class="meta-left">
            <span class="meta-item views">
              <span class="meta-icon">👁️</span>
              <span class="meta-text">{{ blog.views || 0 }} 次浏览</span>
            </span>
            <span class="meta-item likes" @click="toggleLike" :class="{ liked: isLiked }">
              <span class="meta-icon">{{ isLiked ? '❤️' : '🤍' }}</span>
              <span class="meta-text">{{ blog.likes || 0 }} 个点赞</span>
            </span>
            <span class="meta-item collections" @click="toggleCollection" :class="{ collected: isCollected }">
              <span class="meta-icon">{{ isCollected ? '⭐' : '☆' }}</span>
              <span class="meta-text">{{ blog.collects || 0 }} 个收藏</span>
            </span>
            <span class="meta-item comments">
              <span class="meta-icon">💬</span>
              <span class="meta-text">{{ commentCount }} 条评论</span>
            </span>
          </div>
          <div class="meta-right">
            <span class="meta-item time">
              <span class="meta-icon">🕐</span>
              <span class="meta-text">{{ formatTime(blog.createTime) }}</span>
            </span>
          </div>
        </div>
        
        <div class="blog-tags" v-if="blog.tags && blog.tags.length > 0">
          <span 
            v-for="tag in blog.tags" 
            :key="tag"
            class="blog-tag-item"
            :style="{ background: getTagColor(tag) }"
            @click="goToTag(tag)"
          >
            {{ tag }}
          </span>
        </div>
        
        <div class="blog-actions">
          <button v-if="canEdit" @click="editBlog" class="btn-secondary">
            <span class="btn-icon">✏️</span> 编辑
          </button>
          <button v-if="canEdit" @click="deleteBlog" class="btn-danger">
            <span class="btn-icon">🗑️</span> 删除
          </button>
          <button @click="shareBlog" class="btn-secondary">
            <span class="btn-icon">📤</span> 分享
          </button>
        </div>
      </div>
      
      <div class="blog-body">
        <div class="blog-content-html" v-html="blog.content"></div>
      </div>
      
      <div class="blog-footer">
        <div class="blog-footer-stats">
          <div class="stat-item">
            <span class="stat-icon">👁️</span>
            <span class="stat-value">{{ blog.views || 0 }}</span>
            <span class="stat-label">浏览</span>
          </div>
          <div class="stat-item">
            <span class="stat-icon">{{ isLiked ? '❤️' : '🤍' }}</span>
            <span class="stat-value">{{ blog.likes || 0 }}</span>
            <span class="stat-label">点赞</span>
          </div>
          <div class="stat-item">
            <span class="stat-icon">⭐</span>
            <span class="stat-value">{{ blog.collects || 0 }}</span>
            <span class="stat-label">收藏</span>
          </div>
          <div class="stat-item">
            <span class="stat-icon">💬</span>
            <span class="stat-value">{{ commentCount }}</span>
            <span class="stat-label">评论</span>
          </div>
        </div>
      </div>
      
      <div class="comments-section">
        <h3 class="comments-title">
          <span class="title-icon">💬</span>
          评论区 ({{ commentCount }})
        </h3>
        
        <div class="comment-form">
          <textarea 
            v-model="newComment" 
            placeholder="写下你的评论..."
            class="comment-input"
            rows="3"
            @keyup.enter.ctrl="submitComment"
          ></textarea>
          <div class="comment-form-actions">
            <span class="comment-tip">按 Ctrl+Enter 快速发表</span>
            <button @click="submitComment" class="btn-primary">
              <span class="btn-icon">🚀</span> 发表评论
            </button>
          </div>
        </div>
        
        <div class="comments-list" ref="commentsListRef">
          <div 
            v-for="(comment, index) in displayedComments" 
            :key="comment.id" 
            class="comment-card fade-in"
            :style="{ animationDelay: `${index * 0.05}s` }"
          >
            <div 
              class="comment-avatar"
              @click="viewCommentAuthor(comment.userId)"
              :title="`查看 ${getUserNickname(comment)} 的主页`"
            >
              <img :src="getUserAvatar(comment)" alt="用户头像" />
            </div>
            <div class="comment-content-wrapper">
              <div class="comment-header">
                <span class="comment-user">{{ getUserNickname(comment) }}</span>
                <span v-if="comment.isAuthor" class="author-badge">博主</span>
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
            </div>
          </div>
          
          <div v-if="isLoadingMore" class="loading-more">
            <div class="loading-spinner-small"></div>
            <span>加载更多评论...</span>
          </div>
          
          <div v-if="hasMoreComments && !isLoadingMore" class="load-more-hint">
            向下滚动加载更多评论
          </div>
          
          <div v-if="displayedComments.length === 0 && !isLoadingComments" class="no-comments">
            <div class="no-comments-icon">💭</div>
            <p>暂无评论，快来抢沙发吧！</p>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="not-found fade-in">
      <div class="not-found-icon">😢</div>
      <h2>博客不存在或已被删除</h2>
      <p>该博客可能已被作者删除，或者链接地址有误</p>
      <router-link to="/" class="btn-primary">返回首页</router-link>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getBlogById, toggleLike as apiToggleLike, toggleCollect as apiToggleCollect, deleteBlog as apiDeleteBlog, getComments, addComment, isBlogLiked, isBlogCollected } from '@/api/blog'
import { followUser, unfollowUser, isFollowing as checkIsFollowing } from '@/api/user'
import { showToast } from '@/utils/toast'

const router = useRouter()
const route = useRoute()
const blogId = computed(() => route.params.id)

const blog = ref(null)
const allComments = ref([])
const newComment = ref('')
const isLiked = ref(false)
const isCollected = ref(false)
const isFollowing = ref(false)
const containerRef = ref(null)

const commentPageSize = 5
const currentCommentPage = ref(1)
const isLoadingMore = ref(false)
const isLoadingComments = ref(false)
const commentCount = ref(0)

const displayedComments = computed(() => {
  const end = currentCommentPage.value * commentPageSize
  return allComments.value.slice(0, end)
})

const hasMoreComments = computed(() => {
  return currentCommentPage.value * commentPageSize < allComments.value.length
})

const parseTags = (tags) => {
  if (!tags) return []
  if (Array.isArray(tags)) return tags
  return tags.split(',').map(t => t.trim()).filter(Boolean)
}

const canEdit = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.id && user.id === blog.value?.authorId
})

const isOwnBlog = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.id && user.id === blog.value?.authorId
})

const getUserNickname = (comment) => comment.nickname || `用户${comment.userId}`
const getUserAvatar = (comment) => comment.avatar || '/images/default-avatar.svg'

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

const shareBlog = () => {
  if (!blog.value) return
  const shareUrl = window.location.href
  if (navigator.share) {
    navigator.share({ title: blog.value.title, url: shareUrl }).catch(() => {})
  } else {
    navigator.clipboard.writeText(shareUrl).then(() => {
      showToast('链接已复制到剪贴板！', 'success')
    }).catch(() => {})
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff / (1000 * 60 * 60)) % 24)
  if (days > 365) return `${Math.floor(days / 365)}年前`
  if (days > 30) return `${Math.floor(days / 30)}个月前`
  if (days > 0) return `${days}天前`
  if (hours > 0) return `${hours}小时前`
  return `${Math.floor(diff / (1000 * 60))}分钟前`
}

const fetchBlog = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const res = await getBlogById(blogId.value, user.id || null)
    if (res.data) {
      blog.value = { ...res.data, tags: parseTags(res.data.tags) }
      if (user.id) {
        fetchLikeStatus()
        fetchCollectStatus()
        fetchFollowStatus()
      }
      fetchCommentsData()
    }
  } catch (error) {
    console.error('获取博客详情失败:', error)
  }
}

const fetchLikeStatus = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const res = await isBlogLiked(blogId.value, user.id)
    if (res.data !== undefined) isLiked.value = res.data
  } catch (e) {
    console.error('获取点赞状态失败', e)
  }
}

const fetchCollectStatus = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const res = await isBlogCollected(blogId.value, user.id)
    if (res.data !== undefined) isCollected.value = res.data
  } catch (e) {
    console.error('获取收藏状态失败', e)
  }
}

const fetchFollowStatus = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!user.id || !blog.value?.authorId) return
    const res = await checkIsFollowing(user.id, blog.value.authorId)
    if (res.data !== undefined) isFollowing.value = res.data
  } catch (e) {
    console.error('获取关注状态失败', e)
  }
}

const fetchCommentsData = async () => {
  isLoadingComments.value = true
  try {
    const res = await getComments(blogId.value)
    allComments.value = res.data || []
    commentCount.value = allComments.value.length
  } catch (error) {
    console.error('获取评论失败:', error)
  } finally {
    isLoadingComments.value = false
  }
}

const loadMoreComments = () => {
  if (isLoadingMore.value || !hasMoreComments.value) return
  isLoadingMore.value = true
  const delay = 300 + Math.random() * 500
  setTimeout(() => {
    currentCommentPage.value++
    isLoadingMore.value = false
  }, delay)
}

const toggleLike = async () => {
  if (!blog.value) return
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (!user.id) {
    showToast('请先登录', 'warning')
    return
  }
  try {
    const res = await apiToggleLike(blogId.value, user.id)
    isLiked.value = res.data.liked
    blog.value.likes = res.data.likes
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

const toggleCollection = async () => {
  if (!blog.value) return
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (!user.id) {
    showToast('请先登录', 'warning')
    return
  }
  try {
    const action = isCollected.value ? 'remove' : 'add'
    const res = await apiToggleCollect(blogId.value, user.id, action)
    isCollected.value = res.data.collected
    blog.value.collects = res.data.collects
  } catch (error) {
    console.error('收藏失败:', error)
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    showToast('评论内容不能为空', 'warning')
    return
  }
  if (newComment.value.trim().length > 2000) {
    showToast('评论内容不能超过2000个字符', 'warning')
    return
  }
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!user.id) {
      showToast('请先登录', 'warning')
      return
    }
    await addComment({
      blogId: Number(blogId.value),
      userId: user.id,
      content: newComment.value.trim()
    })
    await fetchCommentsData()
    newComment.value = ''
    showToast('评论发表成功', 'success')
  } catch (error) {
    console.error('提交评论失败:', error)
  }
}

const editBlog = () => router.push(`/blog/edit/${blogId.value}`)

const deleteBlog = async () => {
  try {
    await apiDeleteBlog(blogId.value)
    showToast('博客已删除', 'success')
    router.push('/')
  } catch (error) {
    console.error('删除博客失败:', error)
    showToast('删除失败，请重试', 'error')
  }
}

const toggleFollow = async () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (!user.id) {
    showToast('请先登录', 'warning')
    return
  }
  if (!blog.value?.authorId) return
  try {
    if (isFollowing.value) {
      await unfollowUser(user.id, blog.value.authorId)
      isFollowing.value = false
      showToast('已取消关注', 'info')
    } else {
      await followUser(user.id, blog.value.authorId)
      isFollowing.value = true
      showToast('关注成功', 'success')
    }
  } catch (error) {
    console.error('关注操作失败:', error)
    showToast('操作失败，请重试', 'error')
  }
}

const goBack = () => router.back()

const viewAuthorProfile = () => {
  if (route.query.from === 'author' && route.query.authorId && String(blog.value?.authorId) === route.query.authorId) {
    router.back()
  } else {
    router.push(`/author/${blog.value.authorId}`)
  }
}

const viewCommentAuthor = (userId) => {
  if (route.query.from === 'author' && route.query.authorId && String(userId) === route.query.authorId) {
    router.back()
  } else {
    router.push(`/author/${userId}`)
  }
}

const goToTag = (tag) => {
  router.push({ path: '/blog', query: { tag } })
}

const handleScroll = () => {
  if (!hasMoreComments.value || isLoadingMore.value) return
  const scrollBottom = window.innerHeight + window.scrollY
  const docHeight = document.documentElement.scrollHeight
  if (docHeight - scrollBottom < 300) {
    loadMoreComments()
  }
}

onMounted(() => {
  fetchBlog()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.blog-detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 25%, #2a2a4a 50%, #1a1a3a 75%, #0a0a1a 100%);
  min-height: 100vh;
}

.blog-detail-container::before {
  content: '';
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: 
    radial-gradient(circle at 20% 30%, rgba(156, 39, 176, 0.2) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(0, 188, 212, 0.2) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

.back-btn {
  position: fixed;
  top: 20px; left: 20px;
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

.blog-content {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 20px;
  padding: 40px;
  border: 1px solid rgba(77, 208, 225, 0.2);
  box-shadow: 0 10px 60px rgba(0, 0, 0, 0.3);
  margin-top: 20px;
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 1;
}

.blog-header { margin-bottom: 30px; }

.blog-title {
  font-size: 2.5rem;
  background: linear-gradient(90deg, #4dd0e1, #00bcd4);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 25px;
  line-height: 1.3;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 30px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 15px;
  border: 1px solid rgba(77, 208, 225, 0.1);
  transition: all 0.3s ease;
}

.author-info:hover {
  background: rgba(255, 255, 255, 0.04);
  transform: translateY(-2px);
}

.author-avatar {
  width: 55px; height: 55px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid rgba(77, 208, 225, 0.3);
  flex-shrink: 0;
}

.author-avatar img { width: 100%; height: 100%; object-fit: cover; }

.author-details { flex: 1; min-width: 0; }

.author-name {
  display: block;
  font-size: 1.15rem;
  font-weight: 700;
  color: #4dd0e1;
  margin-bottom: 4px;
}

.author-bio {
  display: block;
  font-size: 0.85rem;
  color: #888;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.profile-btn {
  padding: 10px 22px;
  border: 2px solid #9c27b0;
  border-radius: 25px;
  background: transparent;
  color: #ce93d8;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  white-space: nowrap;
}

.profile-btn:hover {
  background: rgba(156, 39, 176, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(156, 39, 176, 0.3);
  color: #e1bee7;
}

.follow-btn {
  padding: 10px 22px;
  border: 2px solid #4dd0e1;
  border-radius: 25px;
  background: transparent;
  color: #4dd0e1;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.follow-btn:hover {
  background: rgba(77, 208, 225, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(77, 208, 225, 0.3);
}

.follow-btn.following {
  background: rgba(77, 208, 225, 0.2);
  color: #4dd0e1;
}

.blog-meta-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  flex-wrap: wrap;
  gap: 12px;
}

.meta-left, .meta-right { display: flex; gap: 12px; flex-wrap: wrap; }

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 7px 14px;
  border-radius: 20px;
  font-size: 0.85rem;
  transition: all 0.3s ease;
  cursor: default;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.meta-item.likes { cursor: pointer; background: rgba(255, 107, 107, 0.1); border-color: rgba(255, 107, 107, 0.2); }
.meta-item.likes .meta-text { color: #ff6b6b; }
.meta-item.likes.liked { background: rgba(255, 71, 87, 0.2); border-color: rgba(255, 71, 87, 0.3); }
.meta-item.likes.liked .meta-text { color: #ff4757; font-weight: 600; }
.meta-item.collections { cursor: pointer; background: rgba(255, 165, 2, 0.1); border-color: rgba(255, 165, 2, 0.2); }
.meta-item.collections .meta-text { color: #ffa502; }
.meta-item.collections.collected { background: rgba(255, 127, 80, 0.2); border-color: rgba(255, 127, 80, 0.3); }
.meta-item.comments { background: rgba(77, 208, 225, 0.1); border-color: rgba(77, 208, 225, 0.2); }
.meta-item.comments .meta-text { color: #4dd0e1; }
.meta-icon { font-size: 1rem; }
.meta-text { color: #888; }

.blog-tags { display: flex; gap: 10px; margin-bottom: 25px; flex-wrap: wrap; }

.blog-tag-item {
  padding: 7px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  color: white;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
  cursor: pointer;
}

.blog-tag-item:hover { transform: translateY(-2px); }

.blog-actions { display: flex; gap: 15px; margin-bottom: 30px; flex-wrap: wrap; }

.btn-secondary, .btn-danger, .btn-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 25px;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  text-decoration: none;
}

.btn-icon { font-size: 1rem; }

.btn-secondary {
  background: rgba(77, 208, 225, 0.2);
  color: #4dd0e1;
  border: 1px solid rgba(77, 208, 225, 0.3);
}

.btn-secondary:hover { background: rgba(77, 208, 225, 0.3); transform: translateY(-2px); }

.btn-danger {
  background: rgba(231, 76, 60, 0.2);
  color: #e74c3c;
  border: 1px solid rgba(231, 76, 60, 0.3);
}

.btn-danger:hover { background: rgba(231, 76, 60, 0.3); transform: translateY(-2px); }

.btn-primary {
  background: linear-gradient(90deg, #4dd0e1, #00bcd4);
  color: #0f0f23;
  font-weight: 700;
}

.btn-primary:hover { transform: translateY(-2px); box-shadow: 0 6px 16px rgba(77, 208, 225, 0.4); }

.blog-body {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #d0d0d0;
  margin-bottom: 40px;
  padding: 30px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.blog-content-html { white-space: pre-wrap; }

.blog-footer {
  margin-bottom: 40px;
  padding: 25px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 15px;
  border: 1px solid rgba(77, 208, 225, 0.1);
}

.blog-footer-stats {
  display: flex;
  justify-content: space-around;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  min-width: 100px;
}

.stat-item:hover { background: rgba(255, 255, 255, 0.05); transform: translateY(-2px); }
.stat-icon { font-size: 1.4rem; }
.stat-value { font-size: 1.2rem; font-weight: 700; color: #4dd0e1; }
.stat-label { font-size: 0.85rem; color: #888; }

.comments-section {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 40px;
}

.comments-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.5rem;
  background: linear-gradient(90deg, #4dd0e1, #00bcd4);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 30px;
}

.title-icon { font-size: 1.2rem; }

.comment-form {
  margin-bottom: 40px;
  padding: 25px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 15px;
  border: 1px solid rgba(77, 208, 225, 0.1);
}

.comment-input {
  width: 100%;
  padding: 18px;
  background: rgba(40, 40, 40, 0.8);
  border: 2px solid #333;
  border-radius: 12px;
  color: #e0e0e0;
  outline: none;
  resize: vertical;
  min-height: 100px;
  font-size: 1rem;
  transition: all 0.3s ease;
  margin-bottom: 15px;
  box-sizing: border-box;
}

.comment-input:focus { border-color: #4dd0e1; box-shadow: 0 0 15px rgba(77, 208, 225, 0.3); }

.comment-form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.comment-tip { font-size: 0.85rem; color: #888; }

.comments-list { display: flex; flex-direction: column; gap: 16px; }

.comment-card {
  display: flex;
  gap: 15px;
  background: rgba(255, 255, 255, 0.03);
  padding: 18px;
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.comment-card:hover { background: rgba(255, 255, 255, 0.05); transform: translateY(-2px); }

.comment-avatar {
  width: 45px; height: 45px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(77, 208, 225, 0.3);
  flex-shrink: 0;
  transition: all 0.3s ease;
  cursor: pointer;
}

.comment-avatar:hover {
  border-color: #9c27b0;
  transform: scale(1.1);
  box-shadow: 0 0 12px rgba(156, 39, 176, 0.4);
}

.comment-avatar img { width: 100%; height: 100%; object-fit: cover; }

.comment-content-wrapper { flex: 1; min-width: 0; }

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 0.9rem;
}

.comment-user { color: #4dd0e1; font-weight: 600; }

.author-badge {
  display: inline-block;
  padding: 2px 8px;
  background: linear-gradient(90deg, #9c27b0, #e040fb);
  color: white;
  font-size: 0.7rem;
  font-weight: 700;
  border-radius: 10px;
  margin-left: 6px;
  vertical-align: middle;
}

.comment-time { color: #777; font-size: 0.8rem; }
.comment-content { color: #c0c0c0; line-height: 1.6; word-break: break-word; }

.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 20px;
  color: #888;
  font-size: 0.9rem;
}

.loading-spinner-small {
  width: 20px; height: 20px;
  border: 3px solid rgba(156, 39, 176, 0.3);
  border-top: 3px solid #00bcd4;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.load-more-hint {
  text-align: center;
  padding: 15px;
  color: #666;
  font-size: 0.85rem;
}

.no-comments {
  text-align: center;
  padding: 50px 20px;
  color: #777;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 15px;
  border: 2px dashed rgba(77, 208, 225, 0.2);
}

.no-comments-icon { font-size: 3rem; margin-bottom: 15px; }
.no-comments p { font-size: 1.1rem; margin: 0; }

.not-found {
  text-align: center;
  padding: 120px 20px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 20px;
  border: 2px dashed rgba(77, 208, 225, 0.3);
  margin-top: 40px;
}

.not-found-icon { font-size: 4rem; margin-bottom: 20px; }
.not-found h2 { font-size: 2rem; color: #e74c3c; margin-bottom: 15px; }
.not-found p { font-size: 1.1rem; color: #888; margin-bottom: 30px; }

.fade-in { animation: fadeIn 0.6s ease-out both; }

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(15px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 768px) {
  .blog-detail-container { padding: 15px; }
  .blog-content { padding: 25px; }
  .blog-title { font-size: 1.8rem; }
  .author-info { flex-wrap: wrap; gap: 12px; }
  .blog-meta-info { flex-direction: column; align-items: flex-start; }
  .meta-left, .meta-right { width: 100%; }
  .blog-body { padding: 20px; font-size: 1rem; }
  .blog-footer-stats { flex-direction: column; align-items: stretch; }
  .stat-item { flex-direction: row; justify-content: space-between; }
}
</style>
