<template>
  <div class="appreciation-container">
    <div class="appreciation-header">
      <h2>💰 赞赏功能</h2>
      <p class="appreciation-desc">感谢您的支持，您的赞赏是我创作的动力</p>
    </div>
    
    <div class="appreciation-stats">
      <div class="stat-card">
        <div class="stat-value">¥{{ totalAmount }}</div>
        <div class="stat-label">总赞赏金额</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ totalPeople }} 人</div>
        <div class="stat-label">赞赏人数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ averageAmount }} 元</div>
        <div class="stat-label">平均赞赏</div>
      </div>
    </div>
    
    <div class="appreciation-list">
      <div v-for="record in records" :key="record.id" class="appreciation-card">
        <div class="card-header">
          <div class="user-info">
            <span class="user-avatar">{{ record.userInitial }}</span>
            <span class="user-name">{{ record.userName }}</span>
          </div>
          <div class="amount-info">
            <span class="amount">¥{{ record.amount }}</span>
            <span class="time">{{ record.time }}</span>
          </div>
        </div>
        <div class="card-body">
          <p class="blog-title">{{ record.blogTitle }}</p>
          <p class="blog-desc">{{ record.blogDesc }}</p>
        </div>
        <div class="card-footer">
          <button @click="appreciate(record.id)" class="appreciate-btn">
            赞赏 {{ record.amount }}元
          </button>
          <button @click="viewRecord(record.id)" class="view-btn">查看详情</button>
        </div>
      </div>
    </div>
    
    <div class="appreciation-modal" v-if="showModal">
      <div class="modal-content">
        <h3>选择赞赏金额</h3>
        <div class="amount-options">
          <button v-for="amount in amountOptions" :key="amount" 
                    @click="selectAmount(amount)"
                    :class="{ selected: selectedAmount === amount }"
                    class="amount-btn">
            {{ amount }}元
          </button>
        </div>
        <div class="modal-footer">
          <button @click="closeModal" class="cancel-btn">取消</button>
          <button @click="submitAppreciation" :disabled="!selectedAmount" class="submit-btn">
            确认赞赏
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

// 赞赏数据
const records = ref([
  {
    id: 1,
    userId: 5,
    userName: '前端爱好者',
    userInitial: '前',
    amount: 66,
    time: '2024-01-15 14:30',
    blogTitle: 'Vue 3 性能优化技巧',
    blogDesc: '一篇非常实用的性能优化文章',
    blogId: 10
  },
  {
    id: 2,
    userId: 8,
    userName: 'React 开发者',
    userInitial: '开',
    amount: 88,
    time: '2024-01-14 10:20',
    blogTitle: 'React Hooks 最佳实践',
    blogDesc: 'Hooks 使用指南非常详细',
    blogId: 15
  }
])

const amountOptions = [1, 3, 6, 12, 24, 66, 88, 168]
const selectedAmount = ref(null)
const showModal = ref(false)

// 统计数据
const totalAmount = computed(() => {
  return records.value.reduce((sum, record) => sum + record.amount, 0)
})

const totalPeople = computed(() => {
  return records.value.length
})

const averageAmount = computed(() => {
  if (totalPeople.value === 0) return 0
  return Math.round(totalAmount.value / totalPeople.value)
})

// 选择金额
const selectAmount = (amount) => {
  selectedAmount.value = amount
}

// 提交赞赏
const submitAppreciation = () => {
  // TODO: 调用后端 API
  console.log('提交赞赏:', selectedAmount.value)
  closeModal()
}

// 关闭弹窗
const closeModal = () => {
  showModal.value = false
  selectedAmount.value = null
}

// 查看详情
const viewRecord = (id) => {
  console.log('查看详情:', id)
}

// 赞赏
const appreciate = (id) => {
  showModal.value = true
}

onMounted(() => {
  // 加载赞赏记录
})
</script>

<style scoped>
.appreciation-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.appreciation-header {
  text-align: center;
  margin-bottom: 30px;
}

.appreciation-header h2 {
  font-size: 2rem;
  color: #fff;
  margin-bottom: 10px;
}

.appreciation-desc {
  color: #999;
  font-size: 1rem;
}

.appreciation-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  padding: 30px;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.stat-value {
  font-size: 2.5rem;
  font-weight: bold;
  color: #4dd0e1;
  margin-bottom: 10px;
}

.stat-label {
  color: #999;
  font-size: 0.95rem;
}

.appreciation-list {
  display: grid;
  gap: 25px;
}

.appreciation-card {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 15px;
  padding: 25px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: all 0.3s ease;
}

.appreciation-card:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-3px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
}

.user-name {
  color: #e0e0e0;
  font-size: 1rem;
}

.amount-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.amount {
  font-size: 1.5rem;
  font-weight: bold;
  color: #f5576c;
}

.time {
  color: #666;
  font-size: 0.85rem;
}

.card-body {
  margin-bottom: 20px;
}

.blog-title {
  color: #4dd0e1;
  font-size: 1.1rem;
  margin-bottom: 8px;
}

.blog-desc {
  color: #999;
  font-size: 0.9rem;
  line-height: 1.6;
}

.card-footer {
  display: flex;
  gap: 15px;
}

.appreciate-btn {
  flex: 1;
  padding: 12px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.appreciate-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 87, 108, 0.4);
}

.view-btn {
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.1);
  color: #999;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.view-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.appreciation-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: linear-gradient(135deg, #1a1a3e 0%, #0f0f23 100%);
  border-radius: 20px;
  padding: 40px;
  max-width: 500px;
  width: 90%;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-content h3 {
  color: #fff;
  font-size: 1.5rem;
  margin-bottom: 30px;
  text-align: center;
}

.amount-options {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  margin-bottom: 30px;
}

.amount-btn {
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  color: #999;
  border: 2px solid transparent;
  border-radius: 12px;
  cursor: pointer;
  font-size: 1.2rem;
  transition: all 0.3s ease;
}

.amount-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(77, 208, 225, 0.3);
}

.amount-btn.selected {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: rgba(102, 126, 234, 0.5);
  transform: scale(1.05);
}

.modal-footer {
  display: flex;
  gap: 15px;
}

.cancel-btn {
  flex: 1;
  padding: 15px;
  background: rgba(255, 255, 255, 0.1);
  color: #999;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.submit-btn {
  flex: 1;
  padding: 15px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(79, 172, 254, 0.4);
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>