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
      <div class="edit-card glass-card">
        <h1 class="page-title">编辑资料</h1>
        
        <div class="avatar-section">
          <img :src="form.avatar || defaultAvatar" alt="avatar" class="avatar-preview" />
          <div class="avatar-actions">
            <button class="btn-secondary" @click="triggerUpload">上传头像</button>
            <input 
              ref="fileInput"
              type="file" 
              accept="image/*"
              @change="handleUpload"
              style="display: none"
            />
          </div>
        </div>
        
        <form @submit.prevent="handleSubmit" class="edit-form">
          <div class="form-group">
            <label>昵称</label>
            <input 
              v-model="form.nickname"
              type="text" 
              class="input-glass" 
              placeholder="请输入昵称"
            />
          </div>
          
          <div class="form-group">
            <label>简介</label>
            <textarea 
              v-model="form.bio"
              class="input-glass bio-input" 
              placeholder="介绍一下自己吧~"
              rows="4"
            ></textarea>
          </div>
          
          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="$router.back()">取消</button>
            <button type="submit" class="btn-primary" :disabled="saving">
              {{ saving ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const logoExists = ref(true)
const logoSrc = '/images/logo.jpg'
const saving = ref(false)
const fileInput = ref(null)

const defaultAvatar = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"%3E%3Ccircle cx="50" cy="50" r="50" fill="%23FFB6C1"/%3E%3Ctext x="50" y="55" text-anchor="middle" fill="white" font-size="40"%3E🌸%3C/text%3E%3C/svg%3E'

const form = reactive({
  nickname: '',
  avatar: '',
  bio: ''
})

const triggerUpload = () => {
  fileInput.value?.click()
}

const handleUpload = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  
  const formData = new FormData()
  formData.append('file', file)
  
  try {
    const res = await fetch('/api/upload', {
      method: 'POST',
      body: formData
    })
    const data = await res.json()
    if (data.code === 200) {
      form.avatar = data.data
    } else {
      ElMessage.error('上传失败')
    }
  } catch (error) {
    ElMessage.error('上传失败')
  }
}

const handleSubmit = async () => {
  saving.value = true
  try {
    const res = await userApi.updateProfile({
      nickname: form.nickname,
      avatar: form.avatar,
      bio: form.bio
    })
    
    if (res.code === 200) {
      ElMessage.success('保存成功')
      userStore.updateUserInfo(res.data)
      router.push(`/user/${userStore.userInfo.id}`)
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  form.nickname = userStore.userInfo?.nickname || userStore.userInfo?.username || ''
  form.avatar = userStore.userInfo?.avatar || ''
  form.bio = userStore.userInfo?.bio || ''
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
}

.main-content {
  max-width: 600px;
  margin: 0 auto;
}

.edit-card {
  padding: 40px;
}

.page-title {
  font-size: 24px;
  color: var(--text-color);
  margin-bottom: 30px;
  text-align: center;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
}

.avatar-preview {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid var(--primary-light);
  margin-bottom: 16px;
}

.avatar-actions {
  display: flex;
  gap: 12px;
}

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  
  label {
    font-size: 14px;
    color: var(--text-color);
    font-weight: 500;
  }
}

.bio-input {
  resize: vertical;
  min-height: 100px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 10px;
}

.btn-primary {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  border: none;
  color: white;
  padding: 12px 32px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 14px;
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

.btn-secondary {
  background: var(--glass-bg);
  border: 1px solid var(--border-color);
  color: var(--text-color);
  padding: 12px 32px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  
  &:hover {
    background: var(--primary-light);
    border-color: var(--primary-color);
  }
}
</style>
