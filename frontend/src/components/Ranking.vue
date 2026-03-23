<template>
  <div class="ranking-container">
    <h3 class="ranking-title">📈 文章排行榜</h3>
    <div class="ranking-list" v-if="sortedBlogs.length > 0">
      <div 
        v-for="(blog, index) in sortedBlogs" 
        :key="blog.id"
        class="ranking-item"
        @click="goToBlog(blog.id)"
      >
        <div class="ranking-number" :class="getRankClass(index)">{{ index + 1 }}</div>
        <div class="ranking-content">
          <h4 class="ranking-item-title">{{ blog.title }}</h4>
          <div class="ranking-item-meta">
            <span class="views">👁 {{ blog.viewCount || 0 }}</span>
            <span class="likes">❤️ {{ blog.likeCount || 0 }}</span>
            <span class="score">热度: {{ calculateScore(blog) }}</span>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="no-data">
      <p>暂无数据</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { blogApi } from '@/api/blog'

const props = defineProps({
  blogs: {
    type: Array,
    default: () => []
  }
})

const router = useRouter()

const calculateScore = (blog) => {
  const viewCount = blog.viewCount || 0
  const likeCount = blog.likeCount || 0
  return viewCount + likeCount * 2
}

const sortedBlogs = computed(() => {
  return [...props.blogs]
    .sort((a, b) => calculateScore(b) - calculateScore(a))
    .slice(0, 10)
})

const getRankClass = (index) => {
  if (index === 0) return 'rank-1'
  if (index === 1) return 'rank-2'
  if (index === 2) return 'rank-3'
  return ''
}

const goToBlog = (blogId) => {
  blogApi.incrementView(blogId)
  router.push(`/blog/${blogId}`)
}
</script>

<style lang="scss" scoped>
.ranking-container {
  margin: 20px 0;
  padding: 20px;
  background: var(--bg-color);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.ranking-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 16px;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: var(--card-color);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid var(--border-color);
  
  &:hover {
    transform: translateX(5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    border-color: var(--primary-light);
  }
  
  .ranking-number {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    font-weight: 600;
    font-size: 14px;
    background: var(--bg-color);
    color: var(--text-secondary);
    flex-shrink: 0;
    
    &.rank-1 {
      background: linear-gradient(135deg, #FFD700, #FFA500);
      color: white;
    }
    
    &.rank-2 {
      background: linear-gradient(135deg, #C0C0C0, #A0A0A0);
      color: white;
    }
    
    &.rank-3 {
      background: linear-gradient(135deg, #CD7F32, #B87333);
      color: white;
    }
  }
  
  .ranking-content {
    flex: 1;
    min-width: 0;
    
    .ranking-item-title {
      font-size: 14px;
      font-weight: 500;
      color: var(--text-color);
      margin-bottom: 6px;
      line-height: 1.4;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .ranking-item-meta {
      display: flex;
      gap: 12px;
      font-size: 12px;
      color: var(--text-secondary);
      
      .views, .likes {
        display: flex;
        align-items: center;
        gap: 4px;
      }
      
      .score {
        color: var(--primary-color);
        font-weight: 500;
      }
    }
  }
}

.no-data {
  text-align: center;
  padding: 40px 0;
  color: var(--text-secondary);
}

@media (max-width: 768px) {
  .ranking-container {
    padding: 16px;
  }
  
  .ranking-item {
    padding: 10px 12px;
    
    .ranking-number {
      width: 28px;
      height: 28px;
      font-size: 12px;
    }
  }
}
</style>
