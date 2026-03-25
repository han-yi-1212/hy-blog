import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/blog/:id',
    name: 'BlogDetail',
    component: () => import('@/views/BlogDetail.vue')
  },
  {
    path: '/write',
    name: 'Write',
    component: () => import('@/views/Write.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/edit/:id',
    name: 'Edit',
    component: () => import('@/views/Write.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/:id',
    name: 'UserProfile',
    component: () => import('@/views/UserProfile.vue')
  },
  {
    path: '/user/edit',
    name: 'UserEdit',
    component: () => import('@/views/UserEdit.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/ai-chat',
    name: 'AiChat',
    component: () => import('@/views/AiChat.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/Admin.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router
