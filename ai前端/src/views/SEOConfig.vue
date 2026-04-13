<template>
  <div class="seo-container">
    <div class="seo-header">
      <h2>🔍 SEO 优化配置</h2>
      <p class="seo-desc">优化搜索引擎排名，提升网站曝光度</p>
    </div>
    
    <div class="seo-form">
      <div class="form-group">
        <label>网站标题</label>
        <input type="text" v-model="meta.title" placeholder="输入网站标题" />
      </div>
      
      <div class="form-group">
        <label>网站描述</label>
        <textarea v-model="meta.description" placeholder="输入网站描述（150 字以内）"></textarea>
      </div>
      
      <div class="form-group">
        <label>关键词</label>
        <input type="text" v-model="meta.keywords" placeholder="输入关键词，用逗号分隔" />
      </div>
      
      <div class="form-group">
        <label>网站图标</label>
        <input type="text" v-model="meta.icon" placeholder="输入网站图标 URL" />
      </div>
      
      <div class="form-group">
        <label>作者信息</label>
        <input type="text" v-model="meta.author" placeholder="输入作者信息" />
      </div>
      
      <div class="form-group">
        <label>文章目录</label>
        <div class="toggle-group">
          <label class="toggle">
            <input type="checkbox" v-model="settings.toc" />
            <span class="toggle-slider"></span>
            显示文章目录
          </label>
        </div>
      </div>
      
      <div class="form-group">
        <label>代码高亮</label>
        <div class="toggle-group">
          <label class="toggle">
            <input type="checkbox" v-model="settings.highlight" />
            <span class="toggle-slider"></span>
            启用代码高亮
          </label>
        </div>
      </div>
      
      <div class="form-group">
        <label>暗黑模式</label>
        <div class="toggle-group">
          <label class="toggle">
            <input type="checkbox" v-model="settings.darkMode" />
            <span class="toggle-slider"></span>
            支持暗黑模式
          </label>
        </div>
      </div>
    </div>
    
    <div class="seo-preview">
      <h3>预览效果</h3>
      <div class="preview-card">
        <div class="preview-meta">
          <h4>{{ meta.title }}</h4>
          <p>{{ meta.description }}</p>
          <div class="preview-tags">
            <span v-for="keyword in meta.keywords.split(',')" :key="keyword" class="tag">
              {{ keyword.trim() }}
            </span>
          </div>
        </div>
      </div>
    </div>
    
    <button @click="saveConfig" class="save-btn">保存配置</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

// SEO 配置
const meta = ref({
  title: '酷炫博客 - 前端技术分享',
  description: '一个专注于前端技术分享的博客，包含 Vue、React、TypeScript 等内容',
  keywords: 'Vue,React,TypeScript,前端,技术分享',
  icon: '/favicon.ico',
  author: '前端开发者'
})

// 设置
const settings = ref({
  toc: true,
  highlight: true,
  darkMode: true
})

// 保存配置
const saveConfig = () => {
  console.log('保存 SEO 配置:', meta.value, settings.value)
}

onMounted(() => {
  // 加载 SEO 配置
})
</script>

<style scoped>
.seo-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.seo-header {
  text-align: center;
  margin-bottom: 30px;
}

.seo-header h2 {
  font-size: 2rem;
  color: #fff;
  margin-bottom: 10px;
}

.seo-desc {
  color: #999;
  font-size: 1rem;
}

.seo-form {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 15px;
  padding: 30px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 25px;
}

.form-group label {
  display: block;
  color: #e0e0e0;
  margin-bottom: 10px;
  font-size: 1rem;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 15px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  color: #e0e0e0;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #4dd0e1;
  background: rgba(255, 255, 255, 0.08);
}

.form-group textarea {
  min-height: 100px;
  resize: vertical;
}

.toggle-group {
  display: flex;
  align-items: center;
  gap: 15px;
}

.toggle {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 32px;
}

.toggle input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.1);
  transition: .4s;
  border-radius: 32px;
}

.toggle-slider:before {
  position: absolute;
  content: "";
  height: 26px;
  width: 26px;
  left: 2px;
  bottom: 2px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .toggle-slider {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

input:checked + .toggle-slider:before {
  transform: translateX(28px);
}

.seo-preview {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 15px;
  padding: 30px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.seo-preview h3 {
  color: #4dd0e1;
  margin-bottom: 20px;
}

.preview-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  padding: 20px;
}

.preview-meta {
  color: #e0e0e0;
}

.preview-meta h4 {
  font-size: 1.5rem;
  margin-bottom: 10px;
}

.preview-meta p {
  color: #999;
  margin-bottom: 15px;
  line-height: 1.6;
}

.preview-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.tag {
  background: rgba(77, 208, 225, 0.2);
  color: #4dd0e1;
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 0.9rem;
}

.save-btn {
  width: 100%;
  padding: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 1.1rem;
  transition: all 0.3s ease;
  margin-top: 20px;
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}
</style>