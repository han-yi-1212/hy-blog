<template>
  <div class="tag-cloud">
    <h3 class="tag-cloud-title">🏷️ 标签云</h3>
    <div class="tag-list">
      <span
        v-for="tag in tags"
        :key="tag.name"
        class="tag-item"
        :style="getTagStyle(tag.count)"
        @click="handleTagClick(tag)"
      >
        {{ tag.name }}
        <span class="tag-count">({{ tag.count }})</span>
      </span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { tagApi } from '@/api/tag'
import { ElMessage } from 'element-plus'

const router = useRouter()
const tags = ref([])

const fetchTags = async () => {
  try {
    const res = await tagApi.getTagCloud()
    if (res.code === 200) {
      tags.value = res.data
    }
  } catch (error) {
    console.error('获取标签云失败:', error)
  }
}

const getTagStyle = (count) => {
  // 根据文章数量计算字体大小（12px - 24px）
  const minSize = 12
  const maxSize = 24
  const maxCount = Math.max(...tags.value.map(t => t.count), 1)
  const size = minSize + (count / maxCount) * (maxSize - minSize)
  
  // 根据数量计算透明度（0.6 - 1）
  const opacity = 0.6 + (count / maxCount) * 0.4
  
  return {
    fontSize: `${size}px`,
    opacity: opacity
  }
}

const handleTagClick = (tag) => {
  // 跳转到标签筛选页面
  router.push({
    path: '/',
    query: { tag: tag.name }
  })
}

onMounted(() => {
  fetchTags()
})
</script>

<style lang="scss" scoped>
.tag-cloud {
  padding: 20px;
  
  .tag-cloud-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-color);
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--border-color);
  }
  
  .tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .tag-item {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    padding: 6px 12px;
    background: rgba(255, 143, 177, 0.15);
    border-radius: 20px;
    color: var(--text-color);
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba(255, 143, 177, 0.3);
      transform: translateY(-2px);
    }
    
    .tag-count {
      font-size: 12px;
      color: var(--text-secondary);
    }
  }
}
</style>
