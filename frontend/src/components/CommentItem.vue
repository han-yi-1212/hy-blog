<template>
  <div class="comment-item" :class="{ 'is-reply': isReply }">
    <div class="comment-header">
      <img 
        :src="comment.userAvatar || defaultAvatar" 
        alt="avatar" 
        class="avatar"
        @click="goToProfile"
      />
      <div class="comment-info">
        <span class="username" @click="goToProfile">{{ comment.username }}</span>
        <span class="time">{{ formatDate(comment.createTime) }}</span>
      </div>
    </div>
    
    <div class="comment-content">{{ comment.content }}</div>
    
    <div class="comment-actions">
      <span 
        class="like-btn" 
        :class="{ liked: comment.liked }"
        @click="handleLike"
      >
        <span class="heart">{{ comment.liked ? '❤️' : '🤍' }}</span>
        {{ comment.likeCount || 0 }}
      </span>
      <span class="reply-btn" @click="showReplyInput = !showReplyInput">
        回复
      </span>
    </div>
    
    <div v-if="showReplyInput" class="reply-input">
      <textarea 
        v-model="replyContent" 
        placeholder="写下你的回复..."
        class="input-glass"
      ></textarea>
      <div class="reply-actions">
        <button class="btn-cancel" @click="showReplyInput = false">取消</button>
        <button 
          class="btn-submit" 
          @click="submitReply"
          :disabled="!replyContent.trim() || submitting"
        >
          {{ submitting ? '发送中...' : '发送' }}
        </button>
      </div>
    </div>
    
    <div v-if="comment.children && comment.children.length > 0" class="children">
      <CommentItem
        v-for="child in comment.children"
        :key="child.id"
        :comment="child"
        :blog-id="blogId"
        :is-reply="true"
        @refresh="$emit('refresh')"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { commentApi } from '@/api/comment'
import { ElMessage } from 'element-plus'

const props = defineProps({
  comment: {
    type: Object,
    required: true
  },
  blogId: {
    type: [Number, String],
    required: true
  },
  isReply: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['refresh'])

const router = useRouter()
const userStore = useUserStore()

const showReplyInput = ref(false)
const replyContent = ref('')
const submitting = ref(false)

const defaultAvatar = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"%3E%3Ccircle cx="50" cy="50" r="50" fill="%23FFB6C1"/%3E%3Ctext x="50" y="55" text-anchor="middle" fill="white" font-size="40"%3E🌸%3C/text%3E%3C/svg%3E'

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const now = new Date()
  const diff = now - d
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  
  return d.toLocaleDateString('zh-CN')
}

const goToProfile = () => {
  router.push(`/user/${props.comment.userId}`)
}

const handleLike = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  
  try {
    let res
    if (props.comment.liked) {
      res = await commentApi.unlike(props.comment.id)
    } else {
      res = await commentApi.like(props.comment.id)
    }
    
    if (res.code === 200) {
      props.comment.liked = res.data.isLiked
      props.comment.likeCount = res.data.likeCount
    }
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const submitReply = async () => {
  if (!replyContent.value.trim()) return
  
  submitting.value = true
  try {
    const parentId = props.comment.id
    const rootId = props.comment.rootId || props.comment.id
    
    const res = await commentApi.add(
      props.blogId,
      replyContent.value.trim(),
      parentId,
      rootId
    )
    
    if (res.code === 200) {
      ElMessage.success('回复成功')
      replyContent.value = ''
      showReplyInput.value = false
      emit('refresh')
    } else {
      ElMessage.error(res.message || '回复失败')
    }
  } catch (error) {
    ElMessage.error('回复失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid var(--border-color);
  
  &:last-child {
    border-bottom: none;
  }
  
  &.is-reply {
    padding: 12px;
    margin-top: 12px;
    background: var(--bg-color);
    border-radius: 12px;
    border-left: 3px solid var(--primary-light);
  }
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.3s ease;
  
  &:hover {
    transform: scale(1.1);
  }
}

.comment-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-color);
  cursor: pointer;
  
  &:hover {
    color: var(--primary-color);
  }
}

.time {
  font-size: 12px;
  color: var(--text-secondary);
}

.comment-content {
  color: var(--text-color);
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 10px;
  padding-left: 48px;
}

.comment-actions {
  display: flex;
  gap: 20px;
  padding-left: 48px;
}

.like-btn, .reply-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    color: var(--primary-color);
  }
  
  &.liked {
    color: #ff6b6b;
  }
  
  .heart {
    font-size: 14px;
  }
}

.reply-input {
  margin-top: 12px;
  margin-left: 48px;
  
  textarea {
    width: 100%;
    min-height: 80px;
    resize: vertical;
    margin-bottom: 10px;
  }
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-cancel, .btn-submit {
  padding: 6px 16px;
  border-radius: 15px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel {
  background: var(--bg-color);
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  
  &:hover {
    background: var(--primary-light);
  }
}

.btn-submit {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  border: none;
  color: white;
  
  &:hover:not(:disabled) {
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(255, 143, 177, 0.4);
  }
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.children {
  margin-left: 48px;
  margin-top: 12px;
}
</style>
