<template>
  <div v-if="visible" class="modal-overlay" @click="handleOverlayClick">
    <div class="modal-container" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">{{ title }}</h3>
        <button class="close-btn" @click="close">&times;</button>
      </div>
      <div class="modal-content">
        {{ message }}
      </div>
      <div class="modal-footer">
        <button v-if="showCancel" class="modal-btn secondary" @click="cancel">取消</button>
        <button class="modal-btn primary" @click="confirm">确认</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '提示'
  },
  message: {
    type: String,
    default: ''
  },
  showCancel: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'confirm', 'cancel'])

const close = () => {
  emit('close')
}

const confirm = () => {
  emit('confirm')
  emit('close')
}

const cancel = () => {
  emit('cancel')
  emit('close')
}

const handleOverlayClick = () => {
  emit('close')
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  animation: fadeIn 0.3s ease;
}

.modal-container {
  background: rgba(10, 10, 26, 0.95);
  border-radius: 15px;
  border: 1px solid rgba(123, 31, 162, 0.4);
  backdrop-filter: blur(15px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3), 0 0 15px rgba(123, 31, 162, 0.1);
  width: 90%;
  max-width: 400px;
  animation: slideIn 0.3s ease;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 20px 15px;
  border-bottom: 1px solid rgba(123, 31, 162, 0.3);
  background: rgba(10, 10, 26, 0.95);
}

.modal-title {
  color: #e0e0e0;
  margin: 0;
  font-size: 1.1rem;
  font-weight: bold;
}

.close-btn {
  background: none;
  border: none;
  color: #a0a0a0;
  font-size: 1.5rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.close-btn:hover {
  color: #e0e0e0;
  transform: rotate(90deg);
}

.modal-content {
  padding: 25px 20px;
  color: #e0e0e0;
  font-size: 0.95rem;
  line-height: 1.5;
}

.modal-footer {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  padding: 15px 20px 20px;
  border-top: 1px solid rgba(123, 31, 162, 0.3);
  background: rgba(10, 10, 26, 0.95);
}

.modal-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
  font-size: 0.9rem;
}

.modal-btn.primary {
  background: linear-gradient(135deg, #9c27b0, #03a9f4);
  color: white;
}

.modal-btn.primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(123, 31, 162, 0.4);
}

.modal-btn.secondary {
  background: rgba(40, 40, 40, 0.9);
  color: #e0e0e0;
  border: 1px solid rgba(123, 31, 162, 0.4);
}

.modal-btn.secondary:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.4);
  border-color: rgba(123, 31, 162, 0.6);
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideIn {
  from {
    transform: translateY(-50px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>