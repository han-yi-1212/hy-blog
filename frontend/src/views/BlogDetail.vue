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
        <template v-if="userStore.isLoggedIn">
          <router-link to="/write" class="nav-link">
            <el-icon><Edit /></el-icon>
            写文章
          </router-link>
          <router-link to="/admin" class="nav-link" v-if="userStore.isAdmin">
            <el-icon><Setting /></el-icon>
            管理后台
          </router-link>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.nickname?.charAt(0) || userStore.userInfo?.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
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
          <router-link to="/register" class="nav-link register-btn">注册</router-link>
        </template>
      </nav>
    </header>
    
    <main class="main-content" v-if="blog">
      <div class="content-layout">
        <!-- 左侧主内容区 -->
        <article class="blog-detail glass-card">
          <div class="blog-cover" v-if="blog.coverImage">
            <img :src="blog.coverImage" :alt="blog.title" />
          </div>
          
          <div class="blog-header">
            <h1 class="blog-title">{{ blog.title }}</h1>
            <div class="blog-meta">
              <span class="author" @click="goToAuthorProfile">
                <el-icon><User /></el-icon>
                {{ blog.nickname || blog.username }}
              </span>
              <span class="date">
                <el-icon><Calendar /></el-icon>
                {{ formatDate(blog.createTime) }}
              </span>
              <span class="views">
                <el-icon><View /></el-icon>
                {{ blog.viewCount || 0 }}
              </span>
              <span class="likes" :class="{ liked: blog.liked }">
                <el-icon><Star /></el-icon>
                {{ blog.likeCount }}
              </span>
            </div>
            
            <div class="blog-stats" v-if="blog.wordCount && blog.readTime">
              本文约 {{ blog.wordCount }} 字 · 阅读 {{ blog.readTime }} 分钟
            </div>
            
            <div class="blog-tags" v-if="blog.tags && blog.tags.length > 0">
              <span class="tag-label">标签：</span>
              <span
                v-for="tag in blog.tags"
                :key="tag.id"
                class="tag-item"
                @click="goToTag(tag)"
              >
                {{ tag.name }}
              </span>
            </div>
          </div>
          
          <div class="blog-content" v-html="formatContent(blog.content)"></div>
        
        <div class="blog-actions">
          <button 
            class="like-btn" 
            :class="{ liked: blog.liked }"
            @click="handleLike"
          >
            <el-icon><Star /></el-icon>
            {{ blog.liked ? '已点赞' : '点赞' }}
          </button>
          
          <button class="feedback-btn" @click="openFeedbackDialog">
            <el-icon><ChatLineRound /></el-icon>
            反馈
          </button>
          
          <template v-if="canEdit">
            <button class="edit-btn" @click="$router.push(`/edit/${blog.id}`)">
              <el-icon><Edit /></el-icon>
              编辑
            </button>
            <button class="delete-btn" @click="handleDelete">
              <el-icon><Delete /></el-icon>
              删除
            </button>
          </template>
        </div>
        
        <CommentSection :blogId="blog.id" />
        </article>
        
        <!-- 右侧边栏 -->
        <aside class="sidebar">
          <!-- AI总结卡片 -->
          <div class="sidebar-card glass-card">
            <h3 class="sidebar-title">🤖 AI总结</h3>
            <button 
              class="ai-btn summary-btn" 
              @click="generateSummary" 
              :disabled="summaryLoading"
            >
              {{ summaryLoading ? '生成中...' : '生成总结' }}
            </button>
            <div class="ai-summary-result" v-if="summary">
              <div class="ai-label">AI总结：</div>
              <div class="summary-text">{{ summary }}</div>
            </div>
          </div>
          

        </aside>
      </div>
    </main>
    
    <div v-else-if="loading" class="loading-state">
      <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      <p>加载中...</p>
    </div>
    
    <div v-else class="error-state glass-card">
      <el-icon :size="60"><Warning /></el-icon>
      <p>博客不存在或已被删除</p>
      <router-link to="/" class="btn-primary">返回首页</router-link>
    </div>
    
    <!-- 反馈模态框 -->
    <el-dialog
      v-model="feedbackDialogVisible"
      title="文章反馈"
      width="500px"
    >
      <el-form :model="feedbackForm" label-width="80px">
        <el-form-item label="反馈类型">
          <el-radio-group v-model="feedbackForm.type">
            <el-radio label="1">内容错误</el-radio>
            <el-radio label="2">内容违规</el-radio>
            <el-radio label="3">其他问题</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="详细说明">
          <el-input
            v-model="feedbackForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入详细说明（选填）"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="feedbackDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitFeedback">提交反馈</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { blogApi, feedbackApi } from '@/api/blog'
import { aiApi } from '@/api/ai'
import { ElMessage, ElMessageBox } from 'element-plus'
import CommentSection from '@/components/CommentSection.vue'
import { marked } from 'marked'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const blog = ref(null)
const loading = ref(true)
const logoExists = ref(true)
const logoSrc = '/images/logo.jpg'
const feedbackDialogVisible = ref(false)
const feedbackForm = ref({
  type: 1,
  reason: ''
})

// AI总结相关
const summary = ref('')
const summaryLoading = ref(false)



const canEdit = computed(() => {
  return userStore.isLoggedIn && blog.value && blog.value.userId === userStore.userInfo?.id
})

const fetchBlog = async () => {
  loading.value = true
  try {
    const res = await blogApi.getDetail(route.params.id)
    if (res.code === 200) {
      blog.value = res.data
    }
  } catch (error) {
    console.error('获取博客详情失败:', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const formatContent = (content) => {
  if (!content) return ''
  return marked(content)
}

const goToAuthorProfile = () => {
  if (blog.value?.userId) {
    router.push(`/user/${blog.value.userId}`)
  }
}

const goToTag = (tag) => {
  router.push({
    path: '/',
    query: { tag: tag.name }
  })
}

const handleLike = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    const res = await blogApi.like(blog.value.id)
    if (res.code === 200) {
      blog.value.liked = res.data.isLiked
      blog.value.likeCount = res.data.likeCount
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这篇博客吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await blogApi.delete(blog.value.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      router.push('/')
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
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

const openFeedbackDialog = () => {
  feedbackForm.value = {
    type: 1,
    reason: ''
  }
  feedbackDialogVisible.value = true
}

const submitFeedback = async () => {
  try {
    const res = await feedbackApi.create(blog.value.id, feedbackForm.value.type, feedbackForm.value.reason)
    if (res.code === 200) {
      ElMessage.success('反馈提交成功')
      feedbackDialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error('反馈提交失败')
  }
}

const generateSummary = async () => {
  if (!blog.value?.content) {
    ElMessage.warning('文章内容为空')
    return
  }
  
  summaryLoading.value = true
  try {
    const res = await aiApi.summarize(blog.value.content)
    if (res.code === 200) {
      summary.value = res.data
      ElMessage.success('总结生成成功')
    } else {
      ElMessage.error(res.message || '生成失败')
    }
  } catch (error) {
    ElMessage.error('生成失败，请稍后重试')
  } finally {
    summaryLoading.value = false
  }
}



const addCopyButtons = () => {
  // 给所有代码块添加复制按钮
  document.querySelectorAll('pre').forEach(block => {
    // 检查是否已经有复制按钮
    if (!block.querySelector('.copy-btn')) {
      const btn = document.createElement('button')
      btn.innerText = '复制'
      btn.className = 'copy-btn'

      btn.onclick = () => {
        const code = block.innerText
        navigator.clipboard.writeText(code).then(() => {
          btn.innerText = '已复制'
          setTimeout(() => {
            btn.innerText = '复制'
          }, 2000)
        })
      }

      block.style.position = 'relative'
      block.appendChild(btn)
    }
  })
}

onMounted(() => {
  fetchBlog()
})

watch(() => blog.value, () => {
  // 博客内容更新后，添加复制按钮
  setTimeout(addCopyButtons, 100)
}, { deep: true })
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
  display: flex;
  align-items: center;
  gap: 4px;
  
  &:hover {
    background: var(--primary-light);
  }
  
  &.router-link-active {
    background: var(--primary-light);
  }
  
  &.register-btn {
    background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
    border: none;
    color: white;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(255, 143, 177, 0.4);
    }
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--text-color);
  
  .username {
    font-size: 14px;
  }
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.content-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.blog-detail {
  flex: 1;
  padding: 40px;
  min-width: 0;
}

.sidebar {
  width: 280px;
  flex-shrink: 0;
  position: sticky;
  top: 100px;
}

.sidebar-card {
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 16px;
}

.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-color);
}

.ai-summary-result,
.ai-search-result {
  margin-top: 16px;
  padding: 12px;
  background: rgba(255, 182, 193, 0.15);
  border-radius: 8px;
  max-height: 300px;
  overflow-y: auto;
  
  /* 自定义滚动条样式 */
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-track {
    background: rgba(255, 182, 193, 0.1);
    border-radius: 3px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: rgba(255, 143, 177, 0.5);
    border-radius: 3px;
  }
  
  &::-webkit-scrollbar-thumb:hover {
    background: rgba(255, 143, 177, 0.8);
  }
  
  .ai-label {
    font-weight: 600;
    color: #FF8FB1;
    margin-bottom: 8px;
    font-size: 14px;
  }
  
  .summary-text,
  .search-text {
    color: var(--text-color);
    font-size: 14px;
    line-height: 1.6;
  }
}

@media (max-width: 1024px) {
  .content-layout {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    position: static;
  }
}

.blog-cover {
  width: 100%;
  max-height: 400px;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 32px;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.blog-header {
  margin-bottom: 32px;
}

.blog-title {
  font-size: 32px;
  color: var(--text-color);
  margin-bottom: 16px;
  line-height: 1.4;
}

.blog-meta {
  display: flex;
  gap: 24px;
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 12px;
  
  span {
    display: flex;
    align-items: center;
    gap: 6px;
  }
  
  .author {
    cursor: pointer;
    
    &:hover {
      color: var(--primary-color);
    }
  }
  
  .likes.liked {
    color: #FF8FB1;
  }
}

.blog-stats {
  color: #999;
  font-size: 14px;
  margin-bottom: 10px;
}

.blog-tags {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  
  .tag-label {
    font-size: 14px;
    color: var(--text-secondary);
  }
  
  .tag-item {
    padding: 4px 12px;
    background: rgba(255, 143, 177, 0.15);
    border-radius: 16px;
    font-size: 13px;
    color: var(--text-color);
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba(255, 143, 177, 0.3);
      transform: translateY(-1px);
    }
  }
}

// AI总结功能样式
.ai-summary-section {
  margin: 16px 0 24px;
  
  .summary-btn {
    margin-bottom: 12px;
  }
}

// AI搜索功能样式
.ai-search-section {
  margin: 16px 0 24px;
  
  .search-input-container {
    margin-bottom: 12px;
    width: 100%;
    max-width: 600px;
  }
  
  .ai-search-content {
    display: flex;
    gap: 8px;
    color: var(--text-color);
    line-height: 1.6;
    
    .ai-label {
      font-weight: 600;
      color: #FF8FB1;
    }
  }
}

.ai-btn {
  background: linear-gradient(45deg, #FF8FB1, #FFD1DC);
  border: none;
  border-radius: 20px;
  padding: 8px 20px;
  color: #333;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  
  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(255, 143, 177, 0.4);
  }
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.ai-box {
  background: rgba(255, 182, 193, 0.2);
  padding: 16px;
  border-radius: 12px;
  
  .ai-summary-content {
    display: flex;
    gap: 8px;
    color: var(--text-color);
    line-height: 1.6;
    
    .ai-label {
      font-weight: 600;
      color: #FF8FB1;
    }
  }
}

.blog-content {
  color: var(--text-color);
  font-size: 16px;
  line-height: 1.8;
  margin-bottom: 40px;
  
  :deep(h1) {
    font-size: 2em;
    margin: 0.67em 0;
    font-weight: bold;
  }
  
  :deep(h2) {
    font-size: 1.5em;
    margin: 0.75em 0;
    font-weight: bold;
  }
  
  :deep(h3) {
    font-size: 1.17em;
    margin: 0.83em 0;
    font-weight: bold;
  }
  
  :deep(h4) {
    font-size: 1em;
    margin: 1.12em 0;
    font-weight: bold;
  }
  
  :deep(h5) {
    font-size: 0.83em;
    margin: 1.5em 0;
    font-weight: bold;
  }
  
  :deep(h6) {
    font-size: 0.67em;
    margin: 1.67em 0;
    font-weight: bold;
  }
  
  :deep(p) {
    margin: 1em 0;
  }
  
  :deep(ul), :deep(ol) {
    margin: 1em 0;
    padding-left: 2em;
  }
  
  :deep(li) {
    margin: 0.5em 0;
  }
  
  :deep(blockquote) {
    margin: 1em 0;
    padding: 0.5em 1em;
    border-left: 4px solid var(--primary-color);
    background: var(--bg-color);
    color: var(--text-secondary);
  }
  
  :deep(code) {
    background: var(--bg-color);
    padding: 2px 8px;
    border-radius: 4px;
    font-family: 'Consolas', 'Monaco', monospace;
    font-size: 0.9em;
  }
  
  :deep(pre) {
    background: var(--bg-color);
    padding: 16px;
    border-radius: 8px;
    overflow-x: auto;
    margin: 1em 0;
    position: relative;
    
    code {
      background: none;
      padding: 0;
    }
  }
  
  :deep(.copy-btn) {
    position: absolute;
    top: 5px;
    right: 5px;
    background: #FF8FB1;
    color: white;
    border: none;
    padding: 5px 10px;
    border-radius: 5px;
    cursor: pointer;
    font-size: 12px;
    transition: all 0.3s ease;
    
    &:hover {
      background: #FF5C8A;
      transform: translateY(-2px);
    }
  }
  
  :deep(a) {
    color: var(--primary-color);
    text-decoration: none;
    
    &:hover {
      text-decoration: underline;
    }
  }
  
  :deep(strong) {
    font-weight: bold;
  }
  
  :deep(em) {
    font-style: italic;
  }
  
  :deep(table) {
    width: 100%;
    border-collapse: collapse;
    margin: 1em 0;
    
    th, td {
      border: 1px solid var(--border-color);
      padding: 8px 12px;
      text-align: left;
    }
    
    th {
      background: var(--bg-color);
      font-weight: bold;
    }
    
    tr:nth-child(even) {
      background: var(--bg-color);
    }
  }
  
  :deep(hr) {
    border: none;
    border-top: 1px solid var(--border-color);
    margin: 2em 0;
  }
  
  :deep(img) {
    max-width: 100%;
    height: auto;
    border-radius: 8px;
  }
}

.blog-actions {
  display: flex;
  gap: 16px;
  padding-top: 24px;
  border-top: 1px solid var(--border-color);
}

.like-btn, .edit-btn, .delete-btn, .feedback-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 25px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.like-btn {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  color: white;
  
  &.liked {
    background: linear-gradient(135deg, #FF8FB1, #FF5C8A);
  }
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(255, 143, 177, 0.4);
  }
}

.feedback-btn {
  background: var(--bg-color);
  border: 1px solid var(--border-color);
  color: var(--text-color);
  
  &:hover {
    background: var(--primary-light);
  }
}

.edit-btn {
  background: var(--bg-color);
  border: 1px solid var(--border-color);
  color: var(--text-color);
  
  &:hover {
    background: var(--primary-light);
  }
}

.delete-btn {
  background: rgba(255, 77, 79, 0.1);
  border: 1px solid rgba(255, 77, 79, 0.3);
  color: #ff4d4f;
  
  &:hover {
    background: rgba(255, 77, 79, 0.2);
  }
}

.loading-state, .error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
  color: var(--text-color);
  
  .el-icon {
    margin-bottom: 16px;
  }
  
  p {
    margin-bottom: 24px;
    font-size: 16px;
    color: var(--text-secondary);
  }
  
  a {
    text-decoration: none;
  }
}

.error-state {
  padding: 60px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>