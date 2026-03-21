<template>
  <div class="theme-toggle" @click="toggleTheme" :title="isDark ? '切换到亮色模式' : '切换到暗色模式'">
    <span class="theme-icon" :class="{ 'rotate': isAnimating }">
      {{ isDark ? '🌙' : '☀️' }}
    </span>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const isDark = ref(false)
const isAnimating = ref(false)

onMounted(() => {
  const savedTheme = localStorage.getItem('theme')
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
  
  if (savedTheme === 'dark' || (!savedTheme && prefersDark)) {
    document.documentElement.setAttribute('data-theme', 'dark')
    isDark.value = true
  }
})

const toggleTheme = () => {
  isAnimating.value = true
  setTimeout(() => {
    isAnimating.value = false
  }, 300)
  
  isDark.value = !isDark.value
  const theme = isDark.value ? 'dark' : 'light'
  document.documentElement.setAttribute('data-theme', theme)
  localStorage.setItem('theme', theme)
}
</script>

<style lang="scss" scoped>
.theme-toggle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--glass-bg);
  border: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background: var(--primary-light);
    border-color: var(--primary-color);
    transform: scale(1.1);
  }
}

.theme-icon {
  font-size: 20px;
  transition: transform 0.3s ease;
  
  &.rotate {
    transform: rotate(180deg);
  }
}
</style>
