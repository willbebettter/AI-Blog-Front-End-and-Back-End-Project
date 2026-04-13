<template>
  <div class="user-edit-container">
    <button class="back-btn" @click="goBack">← 返回</button>
    <div class="user-edit-content">
      <h1 class="page-title">
        <span class="title-icon">✏️</span>
        编辑个人资料
      </h1>
      
      <form @submit.prevent="submitForm" class="edit-form">
        <!-- 头像上传 -->
        <div class="form-group">
          <label class="form-label">头像</label>
          <div class="avatar-upload">
            <div class="avatar-preview">
              <img :src="user.avatar || '/images/default-avatar.svg'" :alt="user.nickname" class="avatar-img" />
              <input type="file" @change="handleAvatarUpload" class="avatar-input" accept="image/*" />
              <div class="avatar-overlay">
                <span class="upload-icon">📷</span>
                <span class="upload-text">点击上传</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="form-group">
          <label class="form-label" for="username">账户名</label>
          <input 
            type="text" 
            id="username"
            :value="user.username"
            class="form-input readonly-input"
            readonly
            disabled
          />
        </div>
        
        <div class="form-group">
          <label class="form-label" for="email">邮箱</label>
          <input 
            type="email" 
            id="email"
            :value="user.email"
            class="form-input readonly-input"
            readonly
            disabled
          />
        </div>
        
        <div class="form-group">
          <label class="form-label" for="bio">个人简介</label>
          <textarea 
            id="bio"
            v-model="user.bio"
            class="form-textarea"
            placeholder="请输入个人简介"
            rows="3"
          ></textarea>
        </div>
        
        <!-- 修改密码 -->
        <div class="form-section">
          <h3 class="section-title">修改密码</h3>
          <div class="form-group">
            <label class="form-label" for="currentPassword">当前密码</label>
            <input 
              type="password" 
              id="currentPassword"
              v-model="passwordForm.currentPassword"
              class="form-input"
              placeholder="请输入当前密码"
            />
          </div>
          <div class="form-group">
            <label class="form-label" for="newPassword">新密码</label>
            <input 
              type="password" 
              id="newPassword"
              v-model="passwordForm.newPassword"
              class="form-input"
              placeholder="请输入新密码"
              minlength="6"
            />
          </div>
          <div class="form-group">
            <label class="form-label" for="confirmPassword">确认新密码</label>
            <input 
              type="password" 
              id="confirmPassword"
              v-model="passwordForm.confirmPassword"
              class="form-input"
              placeholder="请确认新密码"
            />
          </div>
        </div>
        
        <div class="form-actions">
          <button type="button" @click="cancelEdit" class="action-btn secondary">取消</button>
          <button type="submit" class="action-btn primary">保存修改</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserInfo, updateUser } from '@/api/user'
import { processAvatar } from '@/utils/image'
import { showToast } from '@/utils/toast'

const router = useRouter()

const goBack = () => {
  router.back()
}

const user = ref({
  id: null,
  username: '',
  nickname: '',
  email: '',
  avatar: '',
  bio: ''
})

const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const loadUserInfo = async () => {
  const savedUser = JSON.parse(localStorage.getItem('user') || '{}')
  if (savedUser.id) {
    try {
      const res = await getUserInfo(savedUser.id)
      if (res.data) {
        user.value = {
          id: res.data.id,
          username: res.data.username || '',
          nickname: res.data.nickname || '',
          email: res.data.email || '',
          avatar: res.data.avatar || '',
          bio: res.data.bio || ''
        }
      }
    } catch (e) {
      user.value = { ...user.value, ...savedUser }
    }
  }
}

const handleAvatarUpload = async (event) => {
  const file = event.target.files[0]
  if (file) {
    try {
      user.value.avatar = await processAvatar(file)
    } catch (e) {
      showToast(e.message || '图片处理失败', 'error')
    }
  }
}

const submitForm = async () => {
  if (!user.value.id) {
    showToast('用户信息异常，请重新登录', 'error')
    return
  }
  
  if (passwordForm.value.currentPassword && passwordForm.value.newPassword) {
    if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
      showToast('新密码和确认密码不一致', 'warning')
      return
    }
    if (passwordForm.value.newPassword.length < 6) {
      showToast('新密码长度不能少于6位', 'warning')
      return
    }
  }
  
  try {
    const res = await updateUser(user.value.id, {
      avatar: user.value.avatar,
      bio: user.value.bio
    })
    
    if (res.data) {
      localStorage.setItem('user', JSON.stringify(res.data))
    }
    
    showToast('个人资料修改成功', 'success')
    router.push('/user-center')
  } catch (e) {
    console.error('保存失败', e)
    showToast('保存失败，请重试', 'error')
  }
}

const cancelEdit = () => {
  router.push('/user-center')
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.user-edit-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 25%, #2a2a4a 50%, #1a1a3a 75%, #0a0a1a 100%);
  padding: 20px;
  position: relative;
  overflow: auto;
  box-sizing: border-box;
}

.user-edit-container::before {
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
  padding: 8px 16px;
  background: rgba(123, 31, 162, 0.3);
  border: 1px solid rgba(123, 31, 162, 0.5);
  border-radius: 25px;
  color: #e0e0e0;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;
}

.back-btn:hover {
  background: rgba(123, 31, 162, 0.5);
  border-color: #9c27b0;
}

.user-edit-content {
  max-width: 600px;
  margin: 0 auto;
  background: rgba(10, 10, 26, 0.85);
  backdrop-filter: blur(15px);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3), 0 0 15px rgba(123, 31, 162, 0.1);
  border: 1px solid rgba(123, 31, 162, 0.4);
  position: relative;
  z-index: 1;
  min-height: calc(100vh - 40px);
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

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-label {
  font-size: 1rem;
  font-weight: 600;
  color: #e0e0e0;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

.form-input {
  padding: 1rem 1.2rem;
  border: 1px solid rgba(123, 31, 162, 0.4);
  border-radius: 12px;
  background: rgba(40, 40, 40, 0.8);
  color: #e0e0e0;
  font-size: 1rem;
  transition: all 0.3s ease;
  outline: none;
}

.form-input:focus {
  border-color: #9c27b0;
  box-shadow: 0 0 15px rgba(123, 31, 162, 0.3);
  background: rgba(40, 40, 40, 1);
}

.readonly-input {
  opacity: 0.6;
  cursor: not-allowed;
  background: rgba(30, 30, 30, 0.8);
  border-color: rgba(100, 100, 100, 0.4);
}

.form-textarea {
  padding: 1rem 1.2rem;
  border: 1px solid rgba(123, 31, 162, 0.4);
  border-radius: 12px;
  background: rgba(40, 40, 40, 0.8);
  color: #e0e0e0;
  font-size: 1rem;
  resize: vertical;
  transition: all 0.3s ease;
  outline: none;
  min-height: 100px;
}

.form-textarea:focus {
  border-color: #9c27b0;
  box-shadow: 0 0 15px rgba(123, 31, 162, 0.3);
  background: rgba(40, 40, 40, 1);
}

.form-section {
  padding: 1.5rem;
  background: rgba(40, 40, 40, 0.6);
  border-radius: 16px;
  border: 1px solid rgba(123, 31, 162, 0.3);
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.section-title {
  font-size: 1.3rem;
  font-weight: bold;
  color: #e0e0e0;
  margin: 0;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

/* 头像上传 */
.avatar-upload {
  display: flex;
  justify-content: center;
  margin: 1rem 0;
}

.avatar-preview {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 4px solid rgba(123, 31, 162, 0.5);
  box-shadow: 0 8px 32px rgba(123, 31, 162, 0.3);
  transition: all 0.3s ease;
}

.avatar-preview:hover {
  transform: scale(1.05);
  box-shadow: 0 12px 40px rgba(123, 31, 162, 0.4);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
  z-index: 2;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 1;
}

.avatar-preview:hover .avatar-overlay {
  opacity: 1;
}

.upload-icon {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
  color: white;
}

.upload-text {
  font-size: 0.8rem;
  color: white;
  font-weight: 600;
}

/* 按钮组 */
.form-actions {
  display: flex;
  gap: 2rem;
  justify-content: center;
  margin-top: 1.5rem;
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
  box-sizing: border-box;
  min-width: 140px;
}

.action-btn.primary {
  background: linear-gradient(135deg, #9c27b0 0%, #03a9f4 100%);
  color: white;
}

.action-btn.primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(123, 31, 162, 0.4);
}

.action-btn.secondary {
  background: linear-gradient(135deg, #9c27b0 0%, #03a9f4 100%);
  color: white;
  border: none;
}

.action-btn.secondary:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(123, 31, 162, 0.4);
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
  .user-edit-content {
    margin: 1rem;
    padding: 1.5rem;
  }
  
  .page-title {
    font-size: 1.5rem;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .action-btn {
    width: 100%;
  }
}
</style>