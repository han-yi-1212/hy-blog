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
        <router-link to="/write" class="nav-link" v-if="userStore.isLoggedIn">
          <el-icon><Edit /></el-icon>
          写文章
        </router-link>
        <router-link to="/ai-chat" class="nav-link active">
          <el-icon><ChatDotRound /></el-icon>
          AI问答
        </router-link>
      </nav>
    </header>
    
    <main class="main-content">
      <div class="chat-container glass-card">
        <div class="chat-header">
          <h1>🤖 AI知识问答</h1>
          <p class="chat-desc">基于博客知识库的智能问答系统，可以回答关于博客内容的问题</p>
          <div class="knowledge-stats">
            <span class="stat-item" v-if="stats.documentCount > 0">
              <el-icon><Document /></el-icon>
              知识库: {{ stats.documentCount }} 篇文章
            </span>
            <span class="stat-item" v-else>
              <el-icon><Document /></el-icon>
              知识库: 暂无文章
            </span>
            <button class="sync-btn" @click="syncKnowledge" :disabled="syncing">
              {{ syncing ? '同步中...' : '同步知识库' }}
            </button>
          </div>
        </div>
        
        <div class="chat-messages" ref="messagesRef">
          <div v-if="messages.length === 0" class="empty-state">
            <div class="empty-icon">💬</div>
            <p>开始提问吧！</p>
            <p class="empty-hint">例如：Spring Boot 怎么整合 MySQL？</p>
          </div>
          
          <div v-for="(msg, index) in messages" :key="index" class="message" :class="msg.role">
            <div class="message-avatar">
              {{ msg.role === 'user' ? '👤' : '🤖' }}
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(msg.content)"></div>
              <div v-if="msg.sources && msg.sources.length > 0" class="message-sources">
                <div class="sources-title">📚 参考来源：</div>
                <div v-for="source in msg.sources" :key="source.id" class="source-item" @click="goToBlog(source.id)">
                  {{ source.title }}
                  <span class="source-score">相关度: {{ (1 - source.score * 0.01).toFixed(2) }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <div v-if="loading" class="message assistant">
            <div class="message-avatar">🤖</div>
            <div class="message-content">
              <div class="loading-dots">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="chat-input">
          <el-input
            v-model="question"
            placeholder="输入你的问题..."
            @keyup.enter="sendQuestion"
            :disabled="loading"
            size="large"
          >
            <template #append>
              <el-button @click="sendQuestion" :loading="loading" type="primary">
                发送
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ragApi } from '@/api/rag'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'

const router = useRouter()
const userStore = useUserStore()

const logoExists = ref(true)
const logoSrc = '/images/logo_transparent.png'
const question = ref('')
const messages = ref([])
const loading = ref(false)
const messagesRef = ref(null)
const stats = ref({ documentCount: 0 })
const syncing = ref(false)

const fetchStats = async () => {
  try {
    const res = await ragApi.getStats()
    // 后端返回的是Result对象，数据在res.data中
    const data = res.data || res
    if (data.document_count !== undefined) {
      stats.value.documentCount = data.document_count
    }
  } catch (e) {
    console.error('获取知识库统计失败', e)
  }
}

const syncKnowledge = async () => {
  syncing.value = true
  try {
    await ragApi.syncBlogs()
    ElMessage.success('知识库同步成功')
    fetchStats()
  } catch (e) {
    ElMessage.error('同步失败')
  } finally {
    syncing.value = false
  }
}

const sendQuestion = async () => {
  if (!question.value.trim()) return
  if (loading.value) return
  
  const q = question.value.trim()
  question.value = ''
  
  messages.value.push({
    role: 'user',
    content: q
  })
  
  scrollToBottom()
  
  loading.value = true
  try {
    const res = await ragApi.query(q)
    // 后端返回的是Result对象，数据在res.data中
    const data = res.data || res
    
    messages.value.push({
      role: 'assistant',
      content: data.answer || '抱歉，我无法回答这个问题。',
      sources: data.sources || []
    })
  } catch (e) {
    messages.value.push({
      role: 'assistant',
      content: '抱歉，AI服务暂时不可用，请稍后再试。'
    })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

const formatMessage = (content) => {
  if (!content) return ''
  return marked(content)
}

const goToBlog = (id) => {
  router.push(`/blog/${id}`)
}

onMounted(() => {
  fetchStats()
})
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
  gap: 16px;
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
  
  &:hover, &.active {
    background: var(--primary-light);
    color: var(--primary-color);
  }
}

.main-content {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 20px;
}

.chat-container {
  padding: 24px;
  min-height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
}

.chat-header {
  text-align: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border-color);
  
  h1 {
    font-size: 24px;
    color: var(--text-color);
    margin-bottom: 8px;
  }
  
  .chat-desc {
    color: var(--text-secondary);
    font-size: 14px;
  }
  
  .knowledge-stats {
    margin-top: 16px;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 16px;
    
    .stat-item {
      display: flex;
      align-items: center;
      gap: 4px;
      color: var(--text-secondary);
      font-size: 14px;
    }
    
    .sync-btn {
      padding: 6px 12px;
      border: 1px solid var(--primary-color);
      background: transparent;
      color: var(--primary-color);
      border-radius: 16px;
      font-size: 12px;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover:not(:disabled) {
        background: var(--primary-light);
      }
      
      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
  
  .empty-state {
    text-align: center;
    padding: 60px 20px;
    color: var(--text-secondary);
    
    .empty-icon {
      font-size: 48px;
      margin-bottom: 16px;
    }
    
    p {
      margin: 8px 0;
    }
    
    .empty-hint {
      font-size: 14px;
      color: var(--text-secondary);
    }
  }
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  
  &.user {
    flex-direction: row-reverse;
    
    .message-content {
      background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
      color: white;
      border-radius: 16px 16px 4px 16px;
    }
  }
  
  &.assistant {
    .message-content {
      background: var(--bg-color);
      border-radius: 16px 16px 16px 4px;
    }
  }
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--bg-color);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  
  .message-text {
    line-height: 1.6;
    word-break: break-word;
    
    :deep(p) {
      margin: 0.5em 0;
    }
    
    :deep(code) {
      background: rgba(0,0,0,0.1);
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 0.9em;
    }
    
    :deep(pre) {
      background: rgba(0,0,0,0.1);
      padding: 12px;
      border-radius: 8px;
      overflow-x: auto;
      margin: 8px 0;
    }
  }
  
  .message-sources {
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px solid var(--border-color);
    
    .sources-title {
      font-size: 12px;
      color: var(--text-secondary);
      margin-bottom: 8px;
    }
    
    .source-item {
      font-size: 13px;
      color: var(--primary-color);
      cursor: pointer;
      padding: 4px 8px;
      border-radius: 4px;
      margin-bottom: 4px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      &:hover {
        background: var(--primary-light);
      }
      
      .source-score {
        font-size: 11px;
        color: var(--text-secondary);
      }
    }
  }
}

.loading-dots {
  display: flex;
  gap: 4px;
  
  span {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: var(--primary-color);
    animation: bounce 1.4s infinite ease-in-out both;
    
    &:nth-child(1) { animation-delay: -0.32s; }
    &:nth-child(2) { animation-delay: -0.16s; }
  }
}

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.chat-input {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}
</style>
