<template>
  <div class="search-container">
    <div class="search-box-wrapper">
      <div class="search-box">
        <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
        </svg>
        <input 
          type="text" 
          v-model="searchKeyword" 
          @keyup.enter="handleSearch"
          placeholder="搜索博客、文章..."
          class="search-input"
          :class="{ focused: isFocused }"
        />
        <button @click="handleSearch" class="search-btn">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8"></circle>
            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          </svg>
        </button>
      </div>
      
      <!-- 搜索建议下拉 -->
      <div v-if="showSuggestions" class="search-suggestions">
        <div 
          v-for="suggestion in suggestions" 
          :key="suggestion"
          @click="selectSuggestion(suggestion)"
          class="suggestion-item"
          :class="{ active: searchKeyword === suggestion }"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8"></circle>
            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          </svg>
          <span>{{ suggestion }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue'

export default {
  name: 'SearchBox',
  props: {
    modelValue: {
      type: String,
      default: ''
    }
  },
  emits: ['update:modelValue', 'search'],
  setup(props, { emit }) {
    const searchKeyword = ref(props.modelValue)
    const isFocused = ref(false)
    const showSuggestions = ref(false)
    const suggestions = ref([])

    // 监听输入变化
    watch(searchKeyword, (value) => {
      if (value.trim()) {
        // 这里可以添加搜索建议逻辑
        showSuggestions.value = true
        suggestions.value = [value, `搜索"${value}"`, `查看"${value}" 相关内容`]
      } else {
        showSuggestions.value = false
      }
    })

    // 处理搜索
    const handleSearch = () => {
      emit('search', searchKeyword.value)
      emit('update:modelValue', '')
      showSuggestions.value = false
    }

    // 选择建议
    const selectSuggestion = (suggestion) => {
      searchKeyword.value = suggestion
      emit('update:modelValue', suggestion)
      showSuggestions.value = false
    }

    return {
      searchKeyword,
      isFocused,
      showSuggestions,
      suggestions,
      handleSearch,
      selectSuggestion
    }
  }
}
</script>

<style scoped>
.search-container {
  position: relative;
  width: 100%;
}

.search-box-wrapper {
  position: relative;
  width: 100%;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  background: rgba(255, 255, 255, 0.95);
  padding: 0.8rem 1.2rem;
  border-radius: 50px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 2px solid transparent;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.search-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.search-box.focused {
  border-color: #667eea;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.2);
  transform: translateY(-2px);
}

.search-box.focused::before {
  transform: scaleX(1);
}

.search-icon {
  color: #667eea;
  flex-shrink: 0;
  transition: transform 0.3s ease;
}

.search-box.focused .search-icon {
  transform: rotate(180deg);
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 1rem;
  color: #333;
  padding: 0;
  transition: all 0.3s ease;
}

.search-input::placeholder {
  color: #999;
  transition: color 0.3s ease;
}

.search-box.focused .search-input::placeholder {
  color: #ccc;
}

.search-btn {
  padding: 0.6rem 1rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 30px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.search-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.search-btn:active {
  transform: scale(0.95);
}

.search-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  margin-top: 0.5rem;
  overflow: hidden;
  z-index: 100;
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  padding: 0.8rem 1.2rem;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 1px solid #f0f0f0;
}

.suggestion-item:last-child {
  border-bottom: none;
}

.suggestion-item:hover,
.suggestion-item.active {
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.1) 0%, transparent 100%);
  padding-left: 1.5rem;
}

.suggestion-item svg {
  color: #667eea;
  flex-shrink: 0;
}

.suggestion-item span {
  color: #333;
  font-size: 0.95rem;
}
</style>
