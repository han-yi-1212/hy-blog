<template>
  <div class="home-page">
    <FixedNav />
    <HeroSection />
    
    <div class="content-section">
      <div class="main-layout">
        <div class="left-content">
          <div class="search-result" v-if="searchKeyword">
            <span>搜索结果: "{{ searchKeyword }}"</span>
            <button @click="clearSearch" class="clear-btn">清除</button>
          </div>
          
          <div class="blog-list">
            <div 
              v-for="blog in blogs" 
              :key="blog.id" 
              class="blog-card glass-card"
              @click="goToBlog(blog.id)"
            >
              <div class="blog-cover" v-if="blog.coverImage">
                <img :src="blog.coverImage" :alt="blog.title" />
              </div>
              <div class="blog-cover placeholder" v-else>
                <span class="placeholder-icon">🌸</span>
              </div>
              <div class="blog-content">
                <h2 class="blog-title">{{ blog.title }}</h2>
                <p class="blog-summary">{{ getSummary(blog.content) }}</p>
                <div class="blog-footer">
                  <div class="blog-meta">
                    <span class="views">
                      <el-icon><View /></el-icon>
                      {{ blog.viewCount || 0 }}
                    </span>
                    <span class="likes" :class="{ liked: blog.liked }" @click.stop="handleLike(blog)">
                      <span class="heart">{{ blog.liked ? '❤️' : '🤍' }}</span>
                      {{ blog.likeCount || 0 }}
                    </span>
                    <span class="comments">
                      <el-icon><ChatDotRound /></el-icon>
                      {{ blog.commentCount || 0 }}
                    </span>
                  </div>
                  <div class="blog-author">
                    <img 
                      :src="blog.userAvatar || defaultAvatar" 
                      class="author-avatar"
                      @click.stop="goToUser(blog.userId)"
                    />
                    <span class="author-name" @click.stop="goToUser(blog.userId)">{{ blog.username }}</span>
                    <span class="date">{{ formatDate(blog.createTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="pagination" v-if="total > pageSize">
            <el-pagination
              v-model:current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              layout="prev, pager, next"
              @current-change="handlePageChange"
              background
            />
          </div>
          
          <div v-if="blogs.length === 0 && !loading" class="empty-state glass-card">
            <el-icon :size="60"><Document /></el-icon>
            <p>{{ searchKeyword ? '未找到相关博客' : '暂无博客文章' }}</p>
            <router-link to="/write" class="btn-primary" v-if="userStore.isLoggedIn && !searchKeyword">
              写第一篇文章
            </router-link>
          </div>
        </div>
        
        <div class="right-sidebar">
          <SearchBox />
          <TagCloud />
          <RandomPosts />
          <Heatmap :blogs="blogs" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { blogApi } from '@/api/blog'
import { tagApi } from '@/api/tag'
import { ElMessage } from 'element-plus'
import HeroSection from '@/components/HeroSection.vue'
import SearchBox from '@/components/SearchBox.vue'
import RandomPosts from '@/components/RandomPosts.vue'
import FixedNav from '@/components/FixedNav.vue'
import Heatmap from '@/components/Heatmap.vue'
import TagCloud from '@/components/TagCloud.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const currentPage = ref(1)
const blogs = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const searchKeyword = ref('')

const defaultAvatar = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"%3E%3Ccircle cx="50" cy="50" r="50" fill="%23FFB6C1"/%3E%3Ctext x="50" y="55" text-anchor="middle" fill="white" font-size="40"%3E🌸%3C/text%3E%3C/svg%3E'

const selectedTag = ref('')

const fetchBlogs = async () => {
  loading.value = true
  try {
    let res
    if (searchKeyword.value) {
      res = await blogApi.search({
        keyword: searchKeyword.value,
        page: pageNum.value,
        size: pageSize.value
      })
    } else if (selectedTag.value) {
      // 通过标签名获取标签ID，然后查询
      const tagRes = await tagApi.list()
      if (tagRes.code === 200) {
        const tag = tagRes.data.find(t => t.name === selectedTag.value)
        if (tag) {
          res = await blogApi.getByTag(tag.id, pageNum.value, pageSize.value)
        } else {
          res = await blogApi.getList({
            page: pageNum.value,
            size: pageSize.value
          })
        }
      }
    } else {
      res = await blogApi.getList({
        page: pageNum.value,
        size: pageSize.value
      })
    }
    if (res.code === 200) {
      blogs.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取博客列表失败:', error)
  } finally {
    loading.value = false
  }
}

const goToBlog = (id) => {
  blogApi.incrementView(id)
  router.push(`/blog/${id}`)
}

const goToUser = (userId) => {
  router.push(`/user/${userId}`)
}

const handleLike = async (blog) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    const res = await blogApi.like(blog.id)
    if (res.code === 200) {
      blog.liked = res.data.isLiked
      blog.likeCount = res.data.likeCount
    }
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

const getSummary = (content) => {
  if (!content) return ''
  const text = content.replace(/<[^>]+>/g, '').replace(/[#*`]/g, '')
  return text.length > 150 ? text.substring(0, 150) + '...' : text
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchBlogs()
}

const clearSearch = () => {
  searchKeyword.value = ''
  selectedTag.value = ''
  pageNum.value = 1
  router.push('/')
}

watch(() => route.query.search, (newSearch) => {
  if (newSearch) {
    searchKeyword.value = newSearch
    pageNum.value = 1
    fetchBlogs()
  } else {
    searchKeyword.value = ''
    fetchBlogs()
  }
}, { immediate: true })

watch(() => route.query.tag, (newTag) => {
  if (newTag) {
    selectedTag.value = newTag
    pageNum.value = 1
    fetchBlogs()
  } else {
    selectedTag.value = ''
    fetchBlogs()
  }
}, { immediate: true })

onMounted(() => {
  if (!route.query.search && !route.query.tag) {
    fetchBlogs()
  }
})
</script>

<style lang="scss" scoped>
.home-page {
  background: var(--bg-color);
}

.content-section {
  padding: 40px 20px 20px;
}

.main-layout {
  display: flex;
  gap: 30px;
  max-width: 1400px;
  margin: 0 auto;
}

.left-content {
  flex: 3;
  min-width: 0;
}

.right-sidebar {
  flex: 1;
  min-width: 280px;
  max-width: 320px;
}

.search-result {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px 20px;
  background: var(--card-color);
  border-radius: 15px;
  margin-bottom: 20px;
  color: var(--text-color);
  font-size: 16px;
  border: 1px solid var(--border-color);
  
  .clear-btn {
    padding: 5px 15px;
    background: var(--primary-light);
    border: none;
    border-radius: 15px;
    color: var(--primary-dark);
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      background: var(--primary-color);
      color: white;
    }
  }
}

.blog-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.blog-card {
  display: flex;
  gap: 20px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 30px rgba(255, 143, 177, 0.2);
  }
}

.blog-cover {
  width: 200px;
  height: 140px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }
  
  &.placeholder {
    background: linear-gradient(135deg, var(--primary-light), var(--primary-color));
    display: flex;
    align-items: center;
    justify-content: center;
    
    .placeholder-icon {
      font-size: 48px;
    }
  }
}

.blog-card:hover .blog-cover img {
  transform: scale(1.05);
}

.blog-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.blog-title {
  font-size: 20px;
  color: var(--text-color);
  margin-bottom: 10px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.blog-summary {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  flex: 1;
}

.blog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.blog-meta {
  display: flex;
  gap: 16px;
  color: var(--text-secondary);
  font-size: 13px;
  
  span {
    display: flex;
    align-items: center;
    gap: 4px;
  }
  
  .views, .comments {
    .el-icon {
      font-size: 14px;
    }
  }
  
  .likes {
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      transform: scale(1.1);
    }
    
    &.liked {
      color: #ff6b6b;
    }
    
    .heart {
      font-size: 14px;
    }
  }
}

.blog-author {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .author-avatar {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    object-fit: cover;
    cursor: pointer;
    transition: transform 0.3s ease;
    
    &:hover {
      transform: scale(1.1);
    }
  }
  
  .author-name {
    font-size: 13px;
    color: var(--text-color);
    cursor: pointer;
    
    &:hover {
      color: var(--primary-color);
    }
  }
  
  .date {
    font-size: 12px;
    color: var(--text-secondary);
  }
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  
  :deep(.el-pagination) {
    .btn-prev, .btn-next, .el-pager li {
      background: var(--card-color);
      color: var(--text-color);
      border: 1px solid var(--border-color);
      
      &:hover, &.is-active {
        background: var(--primary-color);
        color: white;
        border-color: var(--primary-color);
      }
    }
  }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-color);
  
  .el-icon {
    color: var(--primary-color);
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

@media (max-width: 1024px) {
  .main-layout {
    flex-direction: column;
  }
  
  .right-sidebar {
    max-width: 100%;
    min-width: 100%;
  }
  
  .blog-card {
    flex-direction: column;
  }
  
  .blog-cover {
    width: 100%;
    height: 180px;
  }
}
</style>