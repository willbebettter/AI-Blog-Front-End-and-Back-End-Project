// 全局 toast 工具函数
let toastTimer = null

/**
 * 显示提示框
 * @param {string} message - 提示消息
 * @param {string} type - 提示类型: success, error, warning, info
 * @param {number} duration - 显示时长（毫秒）
 */
export const showToast = (message, type = 'info', duration = 3000) => {
  if (toastTimer) clearTimeout(toastTimer)
  
  // 创建 toast 元素
  let toastElement = document.getElementById('global-toast')
  if (!toastElement) {
    toastElement = document.createElement('div')
    toastElement.id = 'global-toast'
    document.body.appendChild(toastElement)
  }
  
  // 设置样式
  toastElement.className = `toast ${type}`
  toastElement.innerHTML = `
    <span class="toast-icon">${type === 'success' ? '✓' : type === 'error' ? '✕' : type === 'warning' ? '⚠' : 'ℹ'}</span>
    <span class="toast-message">${message}</span>
  `
  
  // 显示 toast
  toastElement.style.display = 'flex'
  
  // 自动隐藏
  toastTimer = setTimeout(() => {
    toastElement.style.display = 'none'
  }, duration)
}

// 导出默认对象
export default {
  showToast
}