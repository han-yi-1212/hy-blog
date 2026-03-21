<template>
  <div class="header-container">
    <!-- Logo 单独放在左上角 -->
    <router-link to="/" class="site-logo">
      <img src="/images/logo_transparent.png" alt="hyのblog" />
    </router-link>
    
    <!-- 导航栏保持在右侧 -->
    <nav class="fixed-nav" :class="{ 'scrolled': isScrolled }">
      <div class="nav-content">
        <div class="nav-links">
          <router-link to="/" class="nav-link">首页</router-link>
          <template v-if="userStore.isLoggedIn">
            <router-link to="/write" class="nav-link">写文章</router-link>
            <el-dropdown @command="handleCommand" trigger="click">
              <span class="user-info">
                <el-avatar :size="28" :src="userStore.userInfo?.avatar">
                  {{ userStore.userInfo?.nickname?.charAt(0) || userStore.userInfo?.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="nav-link">登录</router-link>
            <router-link to="/register" class="nav-link register">注册</router-link>
          </template>
          <ThemeToggle />
        </div>
      </div>
    </nav>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import ThemeToggle from './ThemeToggle.vue'

const router = useRouter()
const userStore = useUserStore()

const isScrolled = ref(false)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 100
}

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/')
  } else if (command === 'profile') {
    router.push(`/user/${userStore.userInfo.id}`)
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  handleScroll()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style lang="scss">
/* 全局样式变量 */
:root {
  --text-color: #333;
  --bg-color: #fff;
}

[data-theme="dark"] {
  --text-color: #e0e0e0;
  --bg-color: #1a1a1a;
}
</style>

<style lang="scss" scoped>
.header-container {
  position: relative;
  z-index: 1000;
}

.site-logo {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 1001;
  display: flex;
  align-items: center;
  text-decoration: none;
  
  img {
    width: auto;
    height: 80px;
    object-fit: contain;
  }
}

.fixed-nav {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1000;
  transition: all 0.3s ease;
  
  .nav-content {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 10px 20px;
    border-radius: 30px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
  }
  
  &.scrolled {
    .nav-content {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    }
  }
  
  .nav-links {
    display: flex;
    align-items: center;
    gap: 15px;
  }
  
  .nav-link {
    color: var(--text-color, #333);
    text-decoration: none;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.3s ease;
    padding: 8px 16px;
    border-radius: 20px;
    
    &:hover {
      background: rgba(108, 166, 255, 0.1);
      color: #6CA6FF;
    }
  }
  
  .register {
    background: #6CA6FF;
    color: white;
    
    &:hover {
      background: #4A89DC;
      color: white;
    }
  }
  
  .user-info {
    cursor: pointer;
  }
}

/* 暗色模式支持 - 使用全局选择器 */
[data-theme="dark"] .fixed-nav {
  .nav-content {
    background: rgba(30, 30, 30, 0.9);
    border: 1px solid rgba(255, 255, 255, 0.1);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  }
  
  &.scrolled {
    .nav-content {
      background: rgba(30, 30, 30, 0.95);
    }
  }
  
  .nav-link {
    color: var(--text-color, #e0e0e0);
    
    &:hover {
      background: rgba(108, 166, 255, 0.2);
      color: #8CB8FF;
    }
  }
  
  .register {
    background: #4A89DC;
    color: white;
    
    &:hover {
      background: #357ABD;
    }
  }
}

@media (max-width: 768px) {
  .site-logo {
    top: 10px;
    left: 10px;
    
    img {
      height: 60px;
    }
  }
  
  .fixed-nav {
    top: 10px;
    right: 10px;
    
    .nav-content {
      padding: 8px 16px;
      gap: 10px;
    }
    
    .nav-link {
      font-size: 12px;
      padding: 6px 12px;
    }
  }
}
</style>