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
        <template v-if="userStore.isLoggedIn">
          <router-link to="/write" class="nav-link">
            <el-icon><Edit /></el-icon>
            写文章
          </router-link>
          <router-link to="/admin" class="nav-link" v-if="userStore.isAdmin">
            <el-icon><Setting /></el-icon>
            管理后台
          </router-link>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.nickname?.charAt(0) || userStore.userInfo?.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-link">登录</router-link>
          <router-link to="/register" class="nav-link register-btn">注册</router-link>
        </template>
      </nav>
    </header>
    
    <main class="main-content">
      <div class="admin-container glass-card">
        <h1 class="admin-title">管理后台</h1>
        
        <div class="admin-nav">
          <button 
            v-for="tab in tabs" 
            :key="tab.key"
            :class="['admin-nav-btn', { active: activeTab === tab.key }]"
            @click="activeTab = tab.key"
          >
            {{ tab.label }}
          </button>
        </div>
        
        <div class="admin-content">
          <!-- 用户管理 -->
          <div v-if="activeTab === 'users'" class="admin-section">
            <h2>用户管理</h2>
            <el-table :data="users" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="username" label="用户名" />
              <el-table-column prop="nickname" label="昵称" />
              <el-table-column prop="role" label="角色">
                <template #default="scope">
                  <el-select v-model="scope.row.role" @change="updateUserRole(scope.row.id, scope.row.role)">
                    <el-option label="普通用户" value="user" />
                    <el-option label="管理员" value="admin" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <el-button 
                    type="danger" 
                    size="small" 
                    @click="deleteUser(scope.row.id)"
                    :disabled="scope.row.id === userStore.userInfo?.id"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <!-- 系统信息 -->
          <div v-if="activeTab === 'system'" class="admin-section">
            <h2>系统信息</h2>
            <div class="system-info">
              <div class="info-item">
                <span class="info-label">当前管理员：</span>
                <span>{{ userStore.userInfo?.username }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">角色：</span>
                <span>{{ userStore.userInfo?.role === 'admin' ? '管理员' : '普通用户' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const logoExists = ref(true)
const logoSrc = '/images/logo.jpg'
const activeTab = ref('users')
const users = ref([])

const tabs = [
  { key: 'users', label: '用户管理' },
  { key: 'system', label: '系统信息' }
]

const fetchUsers = async () => {
  try {
    const res = await fetch('/api/admin/users', {
      headers: {
        'Authorization': `Bearer ${userStore.token}`
      }
    })
    const data = await res.json()
    if (res.ok) {
      users.value = data
    } else {
      ElMessage.error(data.message || '获取用户列表失败')
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  }
}

const updateUserRole = async (userId, role) => {
  try {
    const res = await fetch(`/api/admin/users/${userId}/role?role=${role}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${userStore.token}`
      }
    })
    if (res.ok) {
      ElMessage.success('角色更新成功')
      fetchUsers()
    } else {
      try {
        const data = await res.json()
        ElMessage.error(data.message || '角色更新失败')
      } catch {
        ElMessage.error('角色更新失败')
      }
    }
  } catch (error) {
    ElMessage.error('角色更新失败')
  }
}

const deleteUser = async (userId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await fetch(`/api/admin/users/${userId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${userStore.token}`
      }
    })
    if (res.ok) {
      ElMessage.success('用户删除成功')
      fetchUsers()
    } else {
      try {
        const data = await res.json()
        ElMessage.error(data.message || '用户删除失败')
      } catch {
        ElMessage.error('用户删除失败')
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('用户删除失败')
    }
  }
}

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/')
  } else if (command === 'profile') {
    router.push(`/user/${userStore.userInfo.id}`)
  }
}

onMounted(() => {
  if (!userStore.isLoggedIn || !userStore.isAdmin) {
    ElMessage.warning('无权限访问管理后台')
    router.push('/')
    return
  }
  fetchUsers()
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
  display: flex;
  align-items: center;
  gap: 4px;
  
  &:hover {
    background: var(--primary-light);
  }
  
  &.router-link-active {
    background: var(--primary-light);
  }
  
  &.register-btn {
    background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
    border: none;
    color: white;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(255, 143, 177, 0.4);
    }
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--text-color);
  
  .username {
    font-size: 14px;
  }
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.admin-container {
  padding: 40px;
  border-radius: 16px;
}

.admin-title {
  font-size: 28px;
  color: var(--text-color);
  margin-bottom: 32px;
  text-align: center;
}

.admin-nav {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 16px;
}

.admin-nav-btn {
  padding: 12px 24px;
  border: none;
  background: var(--bg-color);
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s ease;
  color: var(--text-color);
  
  &:hover {
    background: var(--primary-light);
  }
  
  &.active {
    background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
    color: white;
  }
}

.admin-content {
  min-height: 400px;
}

.admin-section {
  h2 {
    font-size: 20px;
    color: var(--text-color);
    margin-bottom: 20px;
  }
}

.system-info {
  background: var(--bg-color);
  padding: 24px;
  border-radius: 12px;
  
  .info-item {
    display: flex;
    margin-bottom: 16px;
    
    .info-label {
      width: 120px;
      font-weight: 600;
      color: var(--text-secondary);
    }
    
    span:last-child {
      color: var(--text-color);
    }
  }
}
</style>