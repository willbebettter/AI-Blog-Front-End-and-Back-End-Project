<template>
  <div class="blog-edit-container">
    <button type="button" class="back-btn" @click="goBack">← 返回</button>
    <div class="form-container">
      <div class="form-header">
        <div class="title-wrapper">
          <h1>✏️ 编辑博客</h1>
          <p>修改博客信息</p>
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

        <!-- 内容 -->
        <div class="form-group">
          <label for="content">内容 <span class="required">*</span></label>
          <div class="content-editor">
            <div 
              class="rich-text-editor" 
              v-html="richTextContent"
              @input="updateRichText"
            ></div>
          </div>
          <p class="content-hint">支持 Markdown 语法，图片请上传到服务器</p>
        </div>

        <!-- 封面图 -->
        <div class="form-group">
          <label for="coverImage">封面图 URL</label>
          <input 
            type="text" 
            id="coverImage" 
            v-model="formData.coverImage" 
            placeholder="请输入图片 URL 或上传图片"
            class="form-input"
          >
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
            {{ isSubmitting ? '保存中...' : '保存修改' }}
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
import { ref, computed, reactive, onMounted } from 'vue'
import { getBlogById, updateBlog } from '@/api/blog'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from '@/utils/toast'

const router = useRouter()
const route = useRoute()
const isSubmitting = ref(false)
const richTextContent = ref('')

const goBack = () => {
  router.back()
}

const blogId = computed(() => route.params.id)

const formData = reactive({
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  tags: '',
  category: '',
  status: 'PUBLISHED'
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

const getRichTextContent = computed(() => {
  return richTextContent.value
})

const updateRichText = (event) => {
  richTextContent.value = event.target.innerHTML
}

const fetchBlog = async () => {
  try {
    const res = await getBlogById(blogId.value)
    if (res.code === 200) {
      const blog = res.data
      formData.title = blog.title
      formData.summary = blog.summary
      formData.content = blog.content
      formData.coverImage = blog.coverImage
      formData.tags = Array.isArray(blog.tags) ? blog.tags.join(',') : (blog.tags || '')
      formData.category = blog.category || ''
      formData.status = blog.status || 'PUBLISHED'
      richTextContent.value = blog.content
      tagList.value = Array.isArray(blog.tags) ? blog.tags : (blog.tags ? blog.tags.split(',').map(t => t.trim()).filter(Boolean) : [])
    }
  } catch (error) {
    console.error('获取博客失败:', error)
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

  isSubmitting.value = true
  
  try {
    const res = await updateBlog(blogId.value, {
      title: formData.title,
      summary: formData.summary,
      content: formData.content,
      coverImage: formData.coverImage,
      tags: formData.tags,
      category: formData.category,
      status: formData.status
    })
    
    if (res.code === 200) {
      showToast('博客更新成功！', 'success')
      router.push('/blog')
    }
  } catch (error) {
    console.error('更新博客失败:', error)
    showToast('更新失败，请稍后重试', 'error')
  } finally {
    isSubmitting.value = false
  }
}

const cancel = () => {
  router.back()
}

onMounted(() => {
  fetchBlog()
})
</script>

<style scoped>
.blog-edit-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f0f23 0%, #1a1a3e 100%);
  padding: 80px 20px 20px;
  position: relative;
  box-sizing: border-box;
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

.form-container {
  max-width: 800px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 20px;
  padding: 2rem;
  border: 1px solid rgba(77, 208, 225, 0.2);
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
}

.required {
  color: #e74c3c;
}

.form-input {
  padding: 12px 15px;
  background: rgba(40, 40, 40, 0.8);
  border: 1px solid #333;
  border-radius: 10px;
  color: #e0e0e0;
  outline: none;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.form-input:focus {
  border-color: #4dd0e1;
  box-shadow: 0 0 10px rgba(77, 208, 225, 0.2);
}

.form-input::placeholder {
  color: #666;
}

.content-editor {
  min-height: 300px;
  padding: 15px;
  border-radius: 10px;
  border: 1px solid #333;
  background: rgba(40, 40, 40, 0.8);
  overflow: auto;
}

.rich-text-editor {
  color: #c0c0c0;
  line-height: 1.6;
  white-space: pre-wrap;
}

.content-hint {
  font-size: 0.8rem;
  color: #777;
  margin-top: 8px;
}

.tag-input-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  padding: 12px 15px;
  background: rgba(40, 40, 40, 0.8);
  border: 1px solid #333;
  border-radius: 10px;
  min-height: 48px;
  transition: all 0.3s ease;
}

.tag-input-container:focus-within {
  border-color: #4dd0e1;
  box-shadow: 0 0 10px rgba(77, 208, 225, 0.2);
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

.btn-primary,
.btn-secondary {
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