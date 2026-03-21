import request from './request'

export const userApi = {
  getUser(id) {
    return request.get(`/user/profile/${id}`)
  },
  
  updateProfile(data) {
    return request.post('/user/update', data)
  },
  
  getCurrentUser() {
    return request.get('/user/info')
  },
  
  login(data) {
    return request.post('/user/login', data)
  },
  
  register(data) {
    return request.post('/user/register', data)
  },
  
  getUserInfo() {
    return request.get('/user/info')
  }
}
