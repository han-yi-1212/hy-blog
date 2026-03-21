<template>
  <div class="search-box">
    <h3 class="widget-title">搜索</h3>
    <div class="search-input-wrapper">
      <input 
        v-model="keyword" 
        type="text" 
        placeholder="搜索博客..." 
        @keyup.enter="handleSearch"
        class="search-input"
      />
      <button @click="handleSearch" class="search-btn">
        <span class="search-icon">🔍</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const keyword = ref('')

const handleSearch = () => {
  if (keyword.value.trim()) {
    router.push({ 
      path: '/', 
      query: { search: keyword.value.trim() } 
    })
  }
}
</script>

<style lang="scss" scoped>
.search-box {
  background: var(--card-color);
  border-radius: 20px;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
}

.widget-title {
  color: var(--text-color);
  font-size: 16px;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--primary-light);
}

.search-input-wrapper {
  display: flex;
  gap: 10px;
}

.search-input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid var(--border-color);
  border-radius: 20px;
  background: var(--input-bg);
  color: var(--text-color);
  font-size: 14px;
  outline: none;
  transition: all 0.3s ease;
  
  &:focus {
    border-color: var(--primary-color);
    box-shadow: 0 0 10px rgba(255, 143, 177, 0.3);
  }
  
  &::placeholder {
    color: var(--input-placeholder);
  }
}

.search-btn {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  
  &:hover {
    transform: scale(1.1);
    box-shadow: 0 0 15px rgba(255, 143, 177, 0.5);
  }
}

.search-icon {
  font-size: 16px;
}
</style>
