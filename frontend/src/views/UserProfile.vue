<template>
  <div class="page-container">
    <header class="header glass-card">
      <div class="logo" @click="$router.push('/')">
        <img :src="logoSrc" class="logo-img" alt="Logo" v-if="logoExists" @error="logoExists = false" />
        <span class="logo-icon" v-else>🌸</span>
        <span class="logo-text">hyのblog</span>
      </div>
      
      <nav class="nav">
        <router-link to="/" class="nav-link">首页</router-link>
      </nav>
    </header>
    
    <main class="main-content" v-if="user">
      <div class="profile-card glass-card">
        <div class="profile-header">
          <div class="avatar-wrapper" @click="goToEdit" v-if="isCurrentUser">
            <img :src="user.avatar || defaultAvatar" alt="avatar" class="avatar" />
            <div class="avatar-overlay">
              <span>编辑</span>
            </div>
          </div>
          <img :src="user.avatar || defaultAvatar" alt="avatar" class="avatar" v-else />
          
          <h1 class="nickname">{{ user.nickname || user.username }}</h1>
          <p class="bio" v-if="user.bio">{{ user.bio }}</p>
          <p class="bio placeholder" v-else>这个人很懒，什么都没写~</p>
          
          <button class="edit-btn" v-if="isCurrentUser" @click="goToEdit">
            编辑资料
          </button>
        </div>
        
        <div class="profile-stats">
          <div class="stat-item">
            <span class="stat-value">{{ blogCount }}</span>
            <span class="stat-label">文章</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ activityCount }}</span>
            <span class="stat-label">动态</span>
          </div>
        </div>
      </div>
      
      <div class="profile-tabs glass-card">
        <div class="tab-header">
          <button 
            :class="['tab-btn', { active: activeTab === 'blogs' }]" 
            @click="activeTab = 'blogs'"
          >
            文章
          </button>
          <button 
            :class="['tab-btn', { active: activeTab === 'activity' }]" 
            @click="activeTab = 'activity'"
          >
            动态
          </button>
        </div>
        
        <div class="tab-content">
          <div v-if="activeTab === 'blogs'" class="blog-list">
            <div 
              v-for="blog in blogs" 
              :key="blog.id" 
              class="blog-item"
              @click="$router.push(`/blog/${blog.id}`)"
            >
              <div class="blog-cover" v-if="blog.coverImage">
                <img :src="blog.coverImage" :alt="blog.title" />
              </div>
              <div class="blog-info">
                <h3 class="blog-title">{{ blog.title }}</h3>
                <p class="blog-summary">{{ getSummary(blog.content) }}</p>
                <div class="blog-meta">
                  <span>{{ formatDate(blog.createTime) }}</span>
                  <span>❤️ {{ blog.likeCount || 0 }}</span>
                </div>
              </div>
            </div>
            
            <div v-if="blogs.length === 0" class="empty-state">
              暂无文章
            </div>
          </div>
          
          <div v-if="activeTab === 'activity'" class="activity-list">
            <div 
              v-for="activity in activities" 
              :key="activity.id" 
              class="activity-item"
              @click="$router.push(`/blog/${activity.blogId}`)"
            >
              <div class="activity-icon">📝</div>
              <div class="activity-content">
                <p class="activity-text">
                  <span class="activity-action">发布了文章</span>
                  <span class="activity-title">《{{ activity.title }}》</span>
                </p>
                <div class="activity-meta">
                  <span class="activity-time">{{ formatDateTime(activity.createTime) }}</span>
                </div>
              </div>
            </div>
            
            <div v-if="activities.length === 0" class="empty-state">
              暂无动态
            </div>
          </div>
        </div>
      </div>
    </main>
    
    <div v-else-if="loading" class="loading-state">
      加载中...
    </div>
    
    <div v-else class="error-state glass-card">
      用户不存在
      <router-link to="/" class="btn-primary">返回首页</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'
import { blogApi } from '@/api/blog'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const user = ref(null)
const blogs = ref([])
const activities = ref([])
const loading = ref(true)
const activeTab = ref('blogs')
const blogCount = ref(0)
const activityCount = ref(0)

const logoExists = ref(true)
const logoSrc = '/images/logo.jpg'
const defaultAvatar = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"%3E%3Ccircle cx="50" cy="50" r="50" fill="%23FFB6C1"/%3E%3Ctext x="50" y="55" text-anchor="middle" fill="white" font-size="40"%3E🌸%3C/text%3E%3C/svg%3E'

const isCurrentUser = computed(() => {
  return userStore.userInfo?.id === Number(route.params.id)
})

const fetchUser = async () => {
  loading.value = true
  try {
    const res = await userApi.getUser(route.params.id)
    if (res.code === 200) {
      user.value = res.data
      fetchUserBlogs()
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchUserBlogs = async () => {
  try {
    const res = await blogApi.getList({ page: 1, size: 100 })
    if (res.code === 200) {
      const allBlogs = res.data.records || []
      blogs.value = allBlogs.filter(b => b.userId === Number(route.params.id))
      blogCount.value = blogs.value.length
      
      // 生成动态列表（按时间倒序排列的博客发布记录）
      activities.value = blogs.value
        .map(blog => ({
          id: blog.id,
          blogId: blog.id,
          title: blog.title,
          createTime: blog.createTime
        }))
        .sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      activityCount.value = activities.value.length
    }
  } catch (error) {
    console.error('获取博客列表失败:', error)
  }
}

const getSummary = (content) => {
  if (!content) return ''
  const text = content.replace(/<[^>]+>/g, '').replace(/[#*`]/g, '')
  return text.length > 100 ? text.substring(0, 100) + '...' : text
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const formatDateTime = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const goToEdit = () => {
  router.push('/user/edit')
}

watch(() => route.params.id, () => {
  if (route.params.id) {
    fetchUser()
  }
}, { immediate: true })
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 32px;
  margin-bottom: 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  
  .logo-img {
    height: 36px;
    width: auto;
  }
  
  .logo-icon {
    font-size: 28px;
  }
  
  .logo-text {
    font-size: 24px;
    font-weight: bold;
    background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
}

.nav {
  display: flex;
  align-items: center;
  gap: 24px;
}

.nav-link {
  color: var(--text-color);
  text-decoration: none;
  font-size: 16px;
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s ease;
  
  &:hover {
    background: var(--primary-light);
  }
  
  &.router-link-active {
    background: var(--primary-light);
  }
}

.main-content {
  max-width: 900px;
  margin: 0 auto;
}

.profile-card {
  padding: 40px;
  text-align: center;
  margin-bottom: 24px;
}

.profile-header {
  margin-bottom: 30px;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  cursor: pointer;
  
  .avatar-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    border-radius: 50%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s ease;
    
    span {
      color: white;
      font-size: 14px;
    }
  }
  
  &:hover .avatar-overlay {
    opacity: 1;
  }
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid var(--primary-light);
}

.nickname {
  font-size: 28px;
  color: var(--text-color);
  margin: 20px 0 10px;
}

.bio {
  color: var(--text-secondary);
  font-size: 16px;
  
  &.placeholder {
    font-style: italic;
  }
}

.edit-btn {
  margin-top: 20px;
  padding: 10px 30px;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  border: none;
  border-radius: 25px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(255, 143, 177, 0.4);
  }
}

.profile-stats {
  display: flex;
  justify-content: center;
  gap: 60px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: var(--primary-color);
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.profile-tabs {
  padding: 0;
  overflow: hidden;
}

.tab-header {
  display: flex;
  border-bottom: 1px solid var(--border-color);
}

.tab-btn {
  flex: 1;
  padding: 16px;
  background: none;
  border: none;
  font-size: 16px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    color: var(--primary-color);
  }
  
  &.active {
    color: var(--primary-color);
    border-bottom: 2px solid var(--primary-color);
  }
}

.tab-content {
  padding: 20px;
}

.blog-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background: var(--bg-color);
  }
}

.blog-cover {
  width: 150px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.blog-info {
  flex: 1;
}

.blog-title {
  font-size: 18px;
  color: var(--text-color);
  margin-bottom: 8px;
}

.blog-summary {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 10px;
  line-height: 1.5;
}

.blog-meta {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: var(--text-secondary);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  background: var(--bg-color);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background: var(--primary-light);
    transform: translateX(5px);
  }
}

.activity-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-color);
  border-radius: 50%;
  font-size: 20px;
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
}

.activity-text {
  margin-bottom: 8px;
  line-height: 1.5;
}

.activity-action {
  color: var(--text-secondary);
}

.activity-title {
  color: var(--primary-color);
  font-weight: 500;
}

.activity-meta {
  font-size: 12px;
  color: var(--text-secondary);
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: var(--text-secondary);
}

.loading-state, .error-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-color);
  
  a {
    display: inline-block;
    margin-top: 20px;
  }
}
</style>
