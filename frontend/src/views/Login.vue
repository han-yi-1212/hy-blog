<template>
  <div class="page-container login-page">
    <div class="login-card glass-card">
      <div class="login-header">
        <img :src="logoSrc" class="logo-img" alt="Logo" v-if="logoExists" @error="logoExists = false" />
        <span class="logo-icon" v-else>🌸</span>
        <h1>欢迎回来</h1>
        <p>登录您的 hyのblog 账号</p>
      </div>
      
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label>用户名</label>
          <input 
            v-model="form.username"
            type="text" 
            class="input-glass" 
            placeholder="请输入用户名"
            required
          />
        </div>
        
        <div class="form-group">
          <label>密码</label>
          <input 
            v-model="form.password"
            type="password" 
            class="input-glass" 
            placeholder="请输入密码"
            required
          />
        </div>
        
        <button type="submit" class="btn-primary login-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      
      <div class="login-footer">
        <span>还没有账号？</span>
        <router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const form = reactive({
  username: '',
  password: ''
})

const loading = ref(false)
const logoExists = ref(true)
const logoSrc = '/images/logo.png'

const handleLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  
  loading.value = true
  try {
    const res = await userStore.login(form)
    if (res.code === 200) {
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error) {
    ElMessage.error('登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

</script>

<style lang="scss" scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
  
  .logo-img {
    height: 60px;
    width: auto;
    margin-bottom: 16px;
  }
  
  .logo-icon {
    font-size: 48px;
    display: block;
    margin-bottom: 16px;
  }
  
  h1 {
    color: var(--text-color);
    font-size: 28px;
    margin-bottom: 8px;
  }
  
  p {
    color: var(--text-secondary);
    font-size: 14px;
  }
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
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

.login-btn {
  width: 100%;
  margin-top: 8px;
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.login-footer {
  text-align: center;
  margin-top: 24px;
  color: var(--text-secondary);
  font-size: 14px;
  
  a {
    color: var(--primary-color);
    text-decoration: none;
    margin-left: 4px;
    font-weight: 500;
    
    &:hover {
      text-decoration: underline;
    }
  }
}
</style>
