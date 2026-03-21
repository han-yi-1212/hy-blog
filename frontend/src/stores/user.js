import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  
  const isLoggedIn = computed(() => !!token.value)
  
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }
  
  const updateUserInfo = (info) => {
    userInfo.value = { ...userInfo.value, ...info }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }
  
  const login = async (loginData) => {
    const res = await userApi.login(loginData)
    if (res.code === 200) {
      setToken(res.data.token)
      setUserInfo(res.data.user)
    }
    return res
  }
  
  const register = async (registerData) => {
    return await userApi.register(registerData)
  }
  
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }
  
  const getUserInfo = async () => {
    if (!token.value) return
    try {
      const res = await userApi.getCurrentUser()
      if (res.code === 200) {
        setUserInfo(res.data)
      }
    } catch (error) {
      logout()
    }
  }
  
  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    updateUserInfo,
    login,
    register,
    logout,
    getUserInfo
  }
})
