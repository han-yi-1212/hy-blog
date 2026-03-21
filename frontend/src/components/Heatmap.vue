<template>
  <div class="heatmap-container">
    <h3 class="heatmap-title">文章热度地图</h3>
    <div class="heatmap-grid" v-if="blogs.length > 0">
      <div 
        v-for="blog in blogs" 
        :key="blog.id"
        class="heatmap-item"
        :class="getHeatLevelClass(blog)"
        @click="goToBlog(blog.id)"
      >
        <div class="heatmap-content">
          <h4 class="heatmap-item-title">{{ blog.title }}</h4>
          <div class="heatmap-item-meta">
            <span>{{ blog.viewCount || 0 }} 次浏览</span>
            <span>{{ blog.likeCount }} 次点赞</span>
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

const getHeatLevelClass = (blog) => {
  const viewCount = blog.viewCount || 0
  const likeCount = blog.likeCount || 0
  const score = viewCount + likeCount * 2
  
  if (score >= 100) return 'heat-level-4'
  if (score >= 50) return 'heat-level-3'
  if (score >= 20) return 'heat-level-2'
  return 'heat-level-1'
}

const goToBlog = (blogId) => {
  blogApi.incrementView(blogId)
  router.push(`/blog/${blogId}`)
}
</script>

<style lang="scss" scoped>
.heatmap-container {
  margin: 20px 0;
  padding: 20px;
  background: var(--bg-color);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.heatmap-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 16px;
}

.heatmap-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.heatmap-item {
  padding: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
  
  .heatmap-content {
    .heatmap-item-title {
      font-size: 14px;
      font-weight: 500;
      color: var(--text-color);
      margin-bottom: 8px;
      line-height: 1.4;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
    
    .heatmap-item-meta {
      display: flex;
      gap: 12px;
      font-size: 12px;
      color: var(--text-secondary);
    }
  }
}

/* 热度等级样式 */
.heat-level-1 {
  background: rgba(108, 166, 255, 0.1);
  border-color: rgba(108, 166, 255, 0.3);
}

.heat-level-2 {
  background: rgba(108, 166, 255, 0.2);
  border-color: rgba(108, 166, 255, 0.4);
}

.heat-level-3 {
  background: rgba(108, 166, 255, 0.3);
  border-color: rgba(108, 166, 255, 0.5);
}

.heat-level-4 {
  background: rgba(108, 166, 255, 0.4);
  border-color: rgba(108, 166, 255, 0.6);
}

.no-data {
  text-align: center;
  padding: 40px 0;
  color: var(--text-secondary);
}

@media (max-width: 768px) {
  .heatmap-grid {
    grid-template-columns: 1fr;
  }
  
  .heatmap-container {
    padding: 16px;
  }
}
</style>