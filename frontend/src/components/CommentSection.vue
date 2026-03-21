<template>
  <div class="comment-section">
    <h3 class="comment-title">评论区</h3>
    
    <div class="comment-input" v-if="userStore.isLoggedIn">
      <textarea 
        v-model="newComment" 
        placeholder="写下你的评论..."
        class="input-glass comment-textarea"
      ></textarea>
      <div class="comment-actions">
        <button 
          class="btn-primary submit-btn" 
          @click="submitComment"
          :disabled="!newComment.trim() || submitting"
        >
          {{ submitting ? '发布中...' : '发布评论' }}
        </button>
      </div>
    </div>
    
    <div class="login-hint" v-else>
      <router-link to="/login">登录</router-link> 后参与评论
    </div>
    
    <div class="comment-list">
      <CommentItem
        v-for="comment in comments"
        :key="comment.id"
        :comment="comment"
        :blog-id="blogId"
        @refresh="fetchComments"
      />
      
      <div v-if="comments.length === 0 && !loading" class="empty-comments">
        暂无评论，快来抢沙发吧~
      </div>
      
      <div v-if="loading" class="loading-comments">
        加载中...
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { commentApi } from '@/api/comment'
import { ElMessage } from 'element-plus'
import CommentItem from './CommentItem.vue'

const props = defineProps({
  blogId: {
    type: [Number, String],
    required: true
  }
})

const userStore = useUserStore()

const comments = ref([])
const newComment = ref('')
const loading = ref(false)
const submitting = ref(false)

const fetchComments = async () => {
  loading.value = true
  try {
    const res = await commentApi.getList(props.blogId)
    if (res.code === 200) {
      comments.value = res.data || []
    }
  } catch (error) {
    console.error('获取评论失败:', error)
  } finally {
    loading.value = false
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) return
  
  submitting.value = true
  try {
    const res = await commentApi.add(props.blogId, newComment.value.trim())
    if (res.code === 200) {
      ElMessage.success('评论成功')
      newComment.value = ''
      fetchComments()
    } else {
      ElMessage.error(res.message || '评论失败')
    }
  } catch (error) {
    ElMessage.error('评论失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

watch(() => props.blogId, () => {
  if (props.blogId) {
    fetchComments()
  }
}, { immediate: true })

onMounted(() => {
  if (props.blogId) {
    fetchComments()
  }
})
</script>

<style lang="scss" scoped>
.comment-section {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px solid var(--border-color);
}

.comment-title {
  color: var(--text-color);
  font-size: 20px;
  margin-bottom: 20px;
}

.comment-input {
  margin-bottom: 24px;
}

.comment-textarea {
  width: 100%;
  min-height: 100px;
  resize: vertical;
  margin-bottom: 12px;
}

.comment-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.submit-btn {
  padding: 10px 24px;
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.empty-comments,
.loading-comments {
  text-align: center;
  padding: 40px;
  color: var(--text-light);
}

.login-hint {
  text-align: center;
  padding: 20px;
  color: var(--text-light);
  
  a {
    color: var(--primary-color);
    text-decoration: none;
    
    &:hover {
      text-decoration: underline;
    }
  }
}
</style>
