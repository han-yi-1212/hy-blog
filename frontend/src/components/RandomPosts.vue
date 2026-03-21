<template>
  <div class="random-posts">
    <h3 class="widget-title">随机阅读</h3>
    <div class="post-list">
      <div 
        v-for="post in posts" 
        :key="post.id" 
        class="post-item"
        @click="goToDetail(post.id)"
      >
        <div class="post-cover" v-if="post.coverImage">
          <img :src="post.coverImage" :alt="post.title" />
        </div>
        <div class="post-info">
          <h4 class="post-title">{{ post.title }}</h4>
          <div class="post-meta">
            <span class="author">{{ post.username }}</span>
            <span class="likes">❤️ {{ post.likeCount || 0 }}</span>
          </div>
        </div>
      </div>
    </div>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-if="!loading && posts.length === 0" class="empty">暂无内容</div>
    <button @click="refreshRandom" class="refresh-btn">换一批</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { blogApi } from '../api/blog'

const router = useRouter()
const posts = ref([])
const loading = ref(false)

const fetchRandomPosts = async () => {
  loading.value = true
  try {
    const res = await blogApi.getRandom(5)
    posts.value = res.data || []
  } catch (error) {
    console.error('获取随机博客失败:', error)
  } finally {
    loading.value = false
  }
}

const refreshRandom = () => {
  fetchRandomPosts()
}

const goToDetail = (id) => {
  router.push(`/blog/${id}`)
}

onMounted(() => {
  fetchRandomPosts()
})
</script>

<style lang="scss" scoped>
.random-posts {
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

.post-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.post-item {
  display: flex;
  gap: 12px;
  padding: 10px;
  background: var(--bg-color);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background: var(--primary-light);
    transform: translateX(5px);
  }
}

.post-cover {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  overflow: hidden;
  flex-shrink: 0;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.post-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.post-title {
  color: var(--text-color);
  font-size: 14px;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-meta {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: var(--text-secondary);
}

.loading, .empty {
  text-align: center;
  color: var(--text-secondary);
  padding: 20px;
}

.refresh-btn {
  width: 100%;
  margin-top: 15px;
  padding: 10px;
  border: none;
  border-radius: 20px;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: scale(1.02);
    box-shadow: 0 0 15px rgba(255, 143, 177, 0.5);
  }
}
</style>
