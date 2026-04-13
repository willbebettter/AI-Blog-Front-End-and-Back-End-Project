<template>
  <div class="pwa-container">
    <div class="pwa-header">
      <h2>📱 PWA 配置</h2>
      <p class="pwa-desc">渐进式 Web 应用配置，支持离线访问</p>
    </div>
    
    <div class="pwa-settings">
      <div class="setting-card">
        <h3>离线缓存</h3>
        <p class="setting-desc">缓存静态资源，支持离线浏览</p>
        <div class="setting-options">
          <label class="setting-option">
            <input type="checkbox" v-model="settings.cacheStatic" />
            缓存静态资源
          </label>
          <label class="setting-option">
            <input type="checkbox" v-model="settings.cacheAPI" />
            缓存 API 响应
          </label>
          <label class="setting-option">
            <input type="checkbox" v-model="settings.cacheImages" />
            缓存图片
          </label>
        </div>
      </div>
      
      <div class="setting-card">
        <h3>应用安装</h3>
        <p class="setting-desc">允许用户安装应用</p>
        <div class="setting-options">
          <label class="setting-option">
            <input type="checkbox" v-model="settings.allowInstall" />
            允许安装应用
          </label>
          <label class="setting-option">
            <input type="checkbox" v-model="settings.showInstallPrompt" />
            显示安装提示
          </label>
        </div>
      </div>
      
      <div class="setting-card">
        <h3>推送通知</h3>
        <p class="setting-desc">接收新文章推送通知</p>
        <div class="setting-options">
          <label class="setting-option">
            <input type="checkbox" v-model="settings.enableNotifications" />
            启用推送通知
          </label>
          <label class="setting-option">
            <input type="checkbox" v-model="settings.showNotificationBadge" />
            显示通知徽章
          </label>
        </div>
      </div>
      
      <div class="setting-card">
        <h3>主题设置</h3>
        <p class="setting-desc">主题和外观设置</p>
        <div class="setting-options">
          <label class="setting-option">
            <select v-model="settings.theme">
              <option value="auto">自动</option>
              <option value="light">浅色</option>
              <option value="dark">深色</option>
              <option value="sepia">护眼</option>
            </select>
          </label>
          <label class="setting-option">
            <select v-model="settings.fontFamily">
              <option value="system">系统字体</option>
              <option value="sans-serif">无衬线字体</option>
              <option value="serif">衬线字体</option>
              <option value="monospace">等宽字体</option>
            </select>
          </label>
        </div>
      </div>
    </div>
    
    <div class="pwa-info">
      <h3>功能说明</h3>
      <ul>
        <li>离线缓存：即使没有网络连接也能浏览已缓存的页面</li>
        <li>应用安装：可以将网站安装到桌面，像原生应用一样使用</li>
        <li>推送通知：当有新文章发布时，会收到通知提醒</li>
        <li>主题设置：支持自动、浅色、深色、护眼四种主题</li>
      </ul>
    </div>
    
    <button @click="saveConfig" class="save-btn">保存配置</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

// PWA 设置
const settings = ref({
  cacheStatic: true,
  cacheAPI: false,
  cacheImages: true,
  allowInstall: true,
  showInstallPrompt: true,
  enableNotifications: false,
  showNotificationBadge: true,
  theme: 'auto',
  fontFamily: 'system'
})

// 保存配置
const saveConfig = () => {
  console.log('保存 PWA 配置:', settings.value)
}

onMounted(() => {
  // 加载 PWA 配置
})
</script>

<style scoped>
.pwa-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.pwa-header {
  text-align: center;
  margin-bottom: 30px;
}

.pwa-header h2 {
  font-size: 2rem;
  color: #fff;
  margin-bottom: 10px;
}

.pwa-desc {
  color: #999;
  font-size: 1rem;
}

.pwa-settings {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 25px;
  margin-bottom: 30px;
}

.setting-card {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 15px;
  padding: 25px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.setting-card h3 {
  color: #4dd0e1;
  margin-bottom: 15px;
  font-size: 1.2rem;
}

.setting-desc {
  color: #999;
  margin-bottom: 20px;
  line-height: 1.6;
}

.setting-options {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.setting-option {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #e0e0e0;
}

.setting-option input[type="checkbox"],
.setting-option select {
  cursor: pointer;
}

.setting-option select {
  padding: 10px 15px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: #e0e0e0;
  font-size: 0.95rem;
  cursor: pointer;
}

.pwa-info {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 15px;
  padding: 25px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.pwa-info h3 {
  color: #4dd0e1;
  margin-bottom: 15px;
}

.pwa-info ul {
  color: #999;
  line-height: 2;
}

.pwa-info li {
  margin-bottom: 10px;
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