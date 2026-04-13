<template>
  <div class="blog-create-container">
    <div class="form-container">
      <div class="form-header">
        <div class="title-wrapper">
          <h1>✏️ 创建新博客</h1>
          <p>填写下方信息，分享你的精彩故事</p>
        </div>
      </div>

      <form @submit.prevent="submitBlog" class="blog-form">
        <!-- 标题 -->
        <div class="form-group">
          <label for="title">标题 <span class="required">*</span></label>
          <input 
            type="text" 
            id="title" 
            v-model="formData.title" 
            maxlength="100"
            placeholder="请输入博客标题"
            class="form-input"
          >
        </div>

        <!-- 摘要 -->
        <div class="form-group">
          <label for="summary">摘要</label>
          <textarea 
            id="summary" 
            v-model="formData.summary" 
            maxlength="200"
            placeholder="简短介绍一下这篇博客..."
            class="form-input"
            rows="3"
          ></textarea>
        </div>

        <!-- 分类 -->
        <div class="form-group">
          <label for="category">分类 <span class="required">*</span></label>
          <select 
            id="category" 
            v-model="formData.category" 
            class="form-input"
          >
            <option value="">请选择分类</option>
            <option value="tech">技术</option>
            <option value="life">生活</option>
            <option value="code">编程</option>
            <option value="design">设计</option>
            <option value="share">分享</option>
            <option value="other">其他</option>
          </select>
        </div>

        <!-- 内容 -->
        <div class="form-group">
          <label for="content">内容 <span class="required">*</span></label>
          <textarea
            id="content"
            v-model="formData.content"
            placeholder="开始编写你的博客内容...\n\n支持 Markdown 语法"
            class="form-input content-textarea"
            rows="15"
          ></textarea>
          <div class="content-counter">
            {{ contentCount }} 字
          </div>
          <p class="content-hint">支持 Markdown 语法，图片请上传到服务器</p>
        </div>

        <!-- 封面图 -->
        <div class="form-group">
          <label for="coverImage">封面图</label>
          <div class="image-upload">
            <input 
              type="file" 
              id="coverImage" 
              ref="fileInput"
              @change="handleImageUpload"
              class="file-input"
              accept="image/*"
            >
            <label for="coverImage" class="upload-label">
              <span v-if="!formData.coverImage" class="upload-icon">📷</span>
              <div v-else class="upload-preview-container">
                <img :src="formData.coverImage" class="upload-preview" alt="封面图">
                <button type="button" @click.stop="removeCoverImage" class="remove-image-btn">×</button>
              </div>
              <span class="upload-text">{{ formData.coverImage ? '更换图片' : '点击上传封面图' }}</span>
            </label>
            <p class="upload-hint">支持 JPG、PNG、WebP 格式，建议尺寸 1200x630px</p>
          </div>
        </div>

        <!-- 标签 -->
        <div class="form-group">
          <label>标签</label>
          <div class="tag-input-container">
            <div v-for="(tag, index) in tagList" :key="index" class="tag-item">
              <span class="tag-text">{{ tag }}</span>
              <button type="button" @click="removeTag(index)" class="tag-remove">×</button>
            </div>
            <div v-if="tagList.length < 5" class="tag-input-wrapper">
              <input 
                type="text"
                v-model="newTag"
                @keyup.enter.prevent="addTag"
                placeholder="输入标签后回车添加"
                class="tag-input"
                maxlength="8"
              >
              <button type="button" @click="addTag" class="tag-add-btn">添加</button>
            </div>
          </div>
          <p class="tag-hint">最多添加5个标签，每个标签不超过8个字符</p>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <button type="submit" class="btn-primary" :disabled="isSubmitting">
            {{ isSubmitting ? '提交中...' : '发表博客' }}
          </button>
          <button type="button" @click="cancel" class="btn-secondary">
            取消
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { createBlog } from '@/api/blog'
import { useRouter } from 'vue-router'
import { processCoverImage } from '@/utils/image'
import { showToast } from '@/utils/toast'

const router = useRouter()
const isSubmitting = ref(false)
const fileInput = ref(null)

const formData = reactive({
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  category: '',
  tags: ''
})

const tagList = ref([])
const newTag = ref('')

const addTag = () => {
  const tag = newTag.value.trim()
  if (!tag) return
  if (tag.length > 8) {
    showToast('标签不能超过8个字符', 'warning')
    return
  }
  if (tagList.value.length >= 5) {
    showToast('最多添加5个标签', 'warning')
    return
  }
  if (tagList.value.includes(tag)) {
    showToast('标签已存在', 'warning')
    return
  }
  tagList.value.push(tag)
  newTag.value = ''
  formData.tags = tagList.value.join(',')
}

const removeTag = (index) => {
  tagList.value.splice(index, 1)
  formData.tags = tagList.value.join(',')
}

const contentCount = computed(() => {
  return formData.content.length
})

const handleImageUpload = async (event) => {
  const file = event.target.files[0]
  if (file) {
    try {
      formData.coverImage = await processCoverImage(file)
    } catch (e) {
      showToast(e.message || '图片处理失败', 'error')
    }
  }
}

const removeCoverImage = () => {
  formData.coverImage = ''
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

const submitBlog = async () => {
  if (!formData.title || !formData.title.trim()) {
    showToast('标题不能为空', 'warning')
    return
  }
  if (formData.title.length > 100) {
    showToast('标题不能超过100个字符', 'warning')
    return
  }
  if (!formData.content || !formData.content.trim()) {
    showToast('内容不能为空', 'warning')
    return
  }
  if (!formData.category) {
    showToast('请选择分类', 'warning')
    return
  }

  isSubmitting.value = true
  
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const res = await createBlog({
      title: formData.title,
      summary: formData.summary,
      content: formData.content,
      coverImage: formData.coverImage,
      category: formData.category,
      tags: formData.tags,
      authorId: user.id,
      status: 'PUBLISHED'
    })
    
    if (res.code === 200) {
      showToast('博客创建成功！', 'success')
      router.push('/blog')
    }
  } catch (error) {
    console.error('创建博客失败:', error)
    showToast(error.response?.data?.message || '创建失败，请稍后重试', 'error')
  } finally {
    isSubmitting.value = false
  }
}

const cancel = () => {
  router.back()
}
</script>

<style scoped>
.blog-create-container {
  height: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 25%, #2a2a4a 50%, #1a1a3a 75%, #0a0a1a 100%);
  padding: 80px 20px 20px;
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
}

.blog-create-container::before {
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

.form-container {
  max-width: 800px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 20px;
  padding: 2rem;
  border: 1px solid rgba(77, 208, 225, 0.2);
  position: relative;
  z-index: 1;
  backdrop-filter: blur(15px);
  box-sizing: border-box;
  margin-bottom: 2rem;
}

.form-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, #9c27b0, #03a9f4, transparent);
  z-index: 2;
}

.form-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.title-wrapper {
  text-align: center;
  flex: 1;
}

.form-header h1 {
  font-size: 2rem;
  color: #4dd0e1;
  margin-bottom: 10px;
}

.form-header p {
  color: #a0a0a0;
  font-size: 1rem;
}

.blog-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: relative;
  z-index: 2;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

label {
  color: #c0c0c0;
  font-size: 0.9rem;
  font-weight: 600;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

.required {
  color: #e74c3c;
}

.form-input {
  padding: 12px 15px;
  background: rgba(40, 40, 40, 0.9);
  border: 1px solid #333;
  border-radius: 10px;
  color: #e0e0e0;
  outline: none;
  font-size: 1rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.form-input:focus {
  border-color: #9c27b0;
  box-shadow: 0 0 20px rgba(123, 31, 162, 0.4);
  background: rgba(40, 40, 40, 1);
}

.form-input::placeholder {
  color: #666;
}

.content-hint {
  font-size: 0.8rem;
  color: #777;
  margin-top: 8px;
}

.content-textarea {
  min-height: 400px;
  resize: vertical;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  width: 100%;
  box-sizing: border-box;
  background: rgba(40, 40, 40, 0.9);
  border: 1px solid #333;
  border-radius: 10px;
  color: #e0e0e0;
  outline: none;
  font-size: 1rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
  padding: 15px;
}

.content-textarea:focus {
  border-color: #9c27b0;
  box-shadow: 0 0 20px rgba(123, 31, 162, 0.4);
  background: rgba(40, 40, 40, 1);
}

.content-textarea::placeholder {
  color: #666;
}

.content-counter {
  align-self: flex-end;
  font-size: 0.8rem;
  color: #666;
  background: rgba(0, 0, 0, 0.5);
  padding: 4px 8px;
  border-radius: 4px;
  margin-top: 5px;
  transition: all 0.3s ease;
}

.content-counter:hover {
  background: rgba(123, 31, 162, 0.2);
  color: #9c27b0;
}

.image-upload {
  position: relative;
}

.file-input {
  display: none;
}

.upload-label {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px;
  border: 2px dashed #333;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(40, 40, 40, 0.8);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.upload-label:hover {
  border-color: #9c27b0;
  background: rgba(40, 40, 40, 0.9);
  box-shadow: 0 0 20px rgba(123, 31, 162, 0.2);
}

.upload-icon {
  font-size: 3rem;
  margin-bottom: 10px;
  filter: drop-shadow(0 0 10px rgba(123, 31, 162, 0.5));
}

.upload-preview-container {
  position: relative;
  margin-bottom: 10px;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.upload-preview {
  max-width: 100%;
  max-height: 200px;
  object-fit: cover;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.upload-preview:hover {
  transform: scale(1.02);
}

.remove-image-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: rgba(231, 76, 60, 0.9);
  color: white;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  z-index: 2;
}

.remove-image-btn:hover {
  background: rgba(231, 76, 60, 1);
  transform: scale(1.1);
  box-shadow: 0 4px 15px rgba(231, 76, 60, 0.3);
}

.upload-text {
  color: #c0c0c0;
  font-size: 1rem;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

.upload-hint {
  font-size: 0.8rem;
  color: #777;
  margin-top: 10px;
  text-align: center;
}

.tag-input-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  padding: 12px 15px;
  background: rgba(40, 40, 40, 0.9);
  border: 1px solid #333;
  border-radius: 10px;
  min-height: 48px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.tag-input-container:focus-within {
  border-color: #9c27b0;
  box-shadow: 0 0 20px rgba(123, 31, 162, 0.4);
}

.tag-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 5px 12px;
  background: linear-gradient(90deg, #9c27b0, #e040fb);
  border-radius: 15px;
  color: white;
  font-size: 0.85rem;
  font-weight: 600;
}

.tag-text {
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tag-remove {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.8);
  font-size: 1rem;
  cursor: pointer;
  padding: 0 2px;
  line-height: 1;
  transition: color 0.2s ease;
}

.tag-remove:hover {
  color: #ff6b6b;
}

.tag-input-wrapper {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  min-width: 150px;
}

.tag-input {
  flex: 1;
  background: none;
  border: none;
  color: #e0e0e0;
  font-size: 0.9rem;
  outline: none;
  padding: 4px 0;
  min-width: 100px;
}

.tag-input::placeholder {
  color: #666;
}

.tag-add-btn {
  padding: 4px 12px;
  background: rgba(77, 208, 225, 0.2);
  border: 1px solid rgba(77, 208, 225, 0.4);
  border-radius: 12px;
  color: #4dd0e1;
  font-size: 0.8rem;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.tag-add-btn:hover {
  background: rgba(77, 208, 225, 0.3);
}

.tag-hint {
  font-size: 0.8rem;
  color: #777;
  margin-top: 6px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 30px;
  position: relative;
  z-index: 2;
}

.btn-primary, .btn-secondary {
  padding: 12px 30px;
  border: none;
  border-radius: 25px;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background: linear-gradient(90deg, #4dd0e1, #00bcd4);
  color: #0f0f23;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(77, 208, 225, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: transparent;
  border: 1px solid #4dd0e1;
  color: #4dd0e1;
}

.btn-secondary:hover {
  background: rgba(77, 208, 225, 0.2);
}
</style>
