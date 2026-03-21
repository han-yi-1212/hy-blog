<template>
  <div class="page-container register-page">
    <div class="register-card glass-card">
      <div class="register-header">
        <img :src="logoSrc" class="logo-img" alt="Logo" v-if="logoExists" @error="logoExists = false" />
        <span class="logo-icon" v-else>🌸</span>
        <h1>加入我们</h1>
        <p>创建您的 hyのblog 账号</p>
      </div>
      
      <form @submit.prevent="handleRegister" class="register-form">
        <div class="form-group">
          <label>用户名</label>
          <input 
            v-model="form.username"
            type="text" 
            class="input-glass" 
            placeholder="请输入用户名（3-20个字符）"
            required
          />
        </div>
        
        <div class="form-group">
          <label>密码</label>
          <input 
            v-model="form.password"
            type="password" 
            class="input-glass" 
            placeholder="请输入密码（6-20个字符）"
            required
          />
        </div>
        
        <div class="form-group">
          <label>确认密码</label>
          <input 
            v-model="form.confirmPassword"
            type="password" 
            class="input-glass" 
            placeholder="请再次输入密码"
            required
          />
        </div>
        
        <button type="submit" class="btn-primary register-btn" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      
      <div class="register-footer">
        <span>已有账号？</span>
        <router-link to="/login">立即登录</router-link>
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
  password: '',
  confirmPassword: ''
})

const loading = ref(false)
const logoExists = ref(true)
const logoSrc = '/images/logo.png'

const handleRegister = async () => {
  if (!form.username || !form.password || !form.confirmPassword) {
    ElMessage.warning('请填写所有字段')
    return
  }
  
  if (form.username.length < 3 || form.username.length > 20) {
    ElMessage.warning('用户名长度必须在3-20个字符之间')
    return
  }
  
  if (form.password.length < 6 || form.password.length > 20) {
    ElMessage.warning('密码长度必须在6-20个字符之间')
    return
  }
  
  if (form.password !== form.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  
  loading.value = true
  try {
    const res = await userStore.register({
      username: form.username,
      password: form.password
    })
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch (error) {
    ElMessage.error('注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

</script>

<style lang="scss" scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 420px;
  padding: 40px;
}

.register-header {
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

.register-form {
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

.register-btn {
  width: 100%;
  margin-top: 8px;
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.register-footer {
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
