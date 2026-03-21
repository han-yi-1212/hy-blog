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
    
    <main class="main-content">
      <div class="write-card glass-card">
        <h1 class="page-title">{{ isEdit ? '编辑文章' : '写文章' }}</h1>
        
        <!-- AI生成功能 -->
        <div class="ai-section">
          <h3 class="ai-section-title">🤖 AI辅助写作</h3>
          <div class="ai-form">
            <input 
              v-model="aiTopic" 
              placeholder="输入文章主题，如：Spring Boot 入门" 
              class="input-glass ai-input"
            />
            <button @click="generateContent" class="ai-btn" :disabled="aiLoading">
              {{ aiLoading ? '生成中...' : 'AI生成' }}
            </button>
          </div>
        </div>
        
        <form @submit.prevent="handleSubmit" class="write-form">
          <div class="form-group">
            <label>文章标题</label>
            <input 
              v-model="form.title"
              type="text" 
              class="input-glass" 
              placeholder="请输入文章标题"
              required
            />
          </div>
          
          <div class="form-group">
            <label>封面图片</label>
            <div class="upload-section">
              <div class="upload-box" @click="triggerUpload" :class="{ 'has-image': form.coverImage }">
                <div v-if="form.coverImage" class="preview-container">
                  <img :src="form.coverImage" alt="封面预览" class="preview-image" />
                  <div class="preview-overlay">
                    <span @click.stop="removeImage" class="remove-btn">✕</span>
                  </div>
                </div>
                <div v-else class="upload-placeholder">
                  <span class="upload-icon">📷</span>
                  <p>点击上传封面图片</p>
                  <p class="upload-hint">支持 JPG、PNG 格式，最大 10MB</p>
                </div>
              </div>
              <input 
                ref="fileInput"
                type="file" 
                accept="image/*"
                @change="handleUpload"
                style="display: none"
              />
              <div class="url-input-section">
                <span class="or-divider">或</span>
                <input 
                  v-model="form.coverImage"
                  type="text" 
                  class="input-glass url-input" 
                  placeholder="直接输入图片URL"
                />
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label>文章内容</label>
            <textarea 
              v-model="form.content"
              class="input-glass content-input" 
              placeholder="请输入文章内容，支持Markdown格式"
              required
            ></textarea>
          </div>
          
          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="$router.back()">
              取消
            </button>
            <button type="submit" class="btn-primary" :disabled="loading">
              {{ loading ? '保存中...' : (isEdit ? '更新' : '发布') }}
            </button>
          </div>
        </form>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { blogApi } from '@/api/blog'
import { uploadApi } from '@/api/upload'
import { aiApi } from '@/api/ai'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const logoExists = ref(true)
const logoSrc = '/images/logo.png'
const fileInput = ref(null)
const uploading = ref(false)

const form = reactive({
  title: '',
  content: '',
  coverImage: ''
})

const loading = ref(false)

// AI生成相关
const aiTopic = ref('')
const aiLoading = ref(false)

const fetchBlog = async () => {
  if (!isEdit.value) return
  
  try {
    const res = await blogApi.getDetail(route.params.id)
    if (res.code === 200) {
      form.title = res.data.title
      form.content = res.data.content
      form.coverImage = res.data.coverImage || ''
    } else {
      ElMessage.error('获取文章失败')
      router.push('/')
    }
  } catch (error) {
    ElMessage.error('获取文章失败')
    router.push('/')
  }
}

const triggerUpload = () => {
  fileInput.value?.click()
}

const handleUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过10MB')
    return
  }
  
  uploading.value = true
  try {
    const res = await uploadApi.upload(file)
    if (res.code === 200) {
      form.coverImage = res.data
      ElMessage.success('上传成功')
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error) {
    ElMessage.error('上传失败，请稍后重试')
  } finally {
    uploading.value = false
    e.target.value = ''
  }
}

const removeImage = () => {
  form.coverImage = ''
}

const generateContent = async () => {
  if (!aiTopic.value.trim()) {
    ElMessage.warning('请输入文章主题')
    return
  }
  
  aiLoading.value = true
  try {
    const res = await aiApi.generate(aiTopic.value)
    if (res.code === 200) {
      form.content = res.data
      ElMessage.success('文章生成成功')
    } else {
      ElMessage.error(res.message || '生成失败')
    }
  } catch (error) {
    ElMessage.error('生成失败，请稍后重试')
  } finally {
    aiLoading.value = false
  }
}

const handleSubmit = async () => {
  if (!form.title.trim()) {
    ElMessage.warning('请输入文章标题')
    return
  }
  
  if (!form.content.trim()) {
    ElMessage.warning('请输入文章内容')
    return
  }
  
  loading.value = true
  try {
    let res
    if (isEdit.value) {
      res = await blogApi.update(route.params.id, form)
    } else {
      res = await blogApi.create(form)
    }
    
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '发布成功')
      router.push('/')
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchBlog()
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

.write-card {
  padding: 40px;
}

.page-title {
  color: var(--text-color);
  font-size: 28px;
  margin-bottom: 32px;
  text-align: center;
}

// AI生成功能样式
.ai-section {
  background: rgba(255, 182, 193, 0.2);
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 32px;
  
  .ai-section-title {
    color: var(--text-color);
    font-size: 18px;
    margin-bottom: 16px;
    font-weight: 600;
  }
  
  .ai-form {
    display: flex;
    gap: 12px;
    
    .ai-input {
      flex: 1;
      padding: 12px 16px;
      border: 1px solid var(--border-color);
      border-radius: 24px;
      background: rgba(255, 255, 255, 0.8);
      backdrop-filter: blur(10px);
      color: var(--text-color);
      
      &::placeholder {
        color: var(--text-secondary);
      }
    }
  }
}

.ai-btn {
  background: linear-gradient(45deg, #FF8FB1, #FFD1DC);
  border: none;
  border-radius: 24px;
  padding: 12px 24px;
  color: #333;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(255, 143, 177, 0.4);
  }
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.write-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  
  label {
    color: var(--text-color);
    font-size: 14px;
    font-weight: 500;
  }
}

.upload-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.upload-box {
  border: 2px dashed var(--primary-color);
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: var(--bg-color);
  
  &:hover {
    border-color: var(--primary-dark);
    background: var(--primary-light);
  }
  
  &.has-image {
    padding: 0;
    border-style: solid;
  }
}

.preview-container {
  position: relative;
  border-radius: 10px;
  overflow: hidden;
}

.preview-image {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
  display: block;
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  
  .preview-container:hover & {
    opacity: 1;
  }
}

.remove-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  color: #ff4d4f;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  
  &:hover {
    background: #ff4d4f;
    color: white;
  }
}

.upload-placeholder {
  .upload-icon {
    font-size: 48px;
    display: block;
    margin-bottom: 12px;
  }
  
  p {
    color: var(--text-color);
    margin: 4px 0;
  }
  
  .upload-hint {
    font-size: 12px;
    color: var(--text-secondary);
  }
}

.url-input-section {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .or-divider {
    color: var(--text-secondary);
    font-size: 14px;
  }
  
  .url-input {
    flex: 1;
  }
}

.content-input {
  min-height: 400px;
  resize: vertical;
  line-height: 1.6;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 8px;
}
</style>