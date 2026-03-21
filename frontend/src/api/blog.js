import request from './request'

export const blogApi = {
  getList(params) {
    return request.get('/blog/list', { params })
  },
  
  getDetail(id) {
    return request.get(`/blog/${id}`)
  },
  
  create(data) {
    return request.post('/blog/add', data)
  },
  
  update(id, data) {
    return request.put(`/blog/${id}`, data)
  },
  
  delete(id) {
    return request.delete(`/blog/${id}`)
  },
  
  like(id) {
    return request.post(`/blog/like/${id}`)
  },
  
  unlike(id) {
    return request.delete(`/blog/like/${id}`)
  },
  
  getLikeCount(id) {
    return request.get(`/blog/like/count/${id}`)
  },
  
  incrementView(id) {
    return request.post(`/blog/view/${id}`)
  },
  
  search(params) {
    return request.get('/blog/search', { params })
  },
  
  getRandom(size = 5) {
    return request.get('/blog/random', { params: { size } })
  }
}

export const feedbackApi = {
  create(blogId, type, reason) {
    return request.post('/api/feedback', { blogId, type, reason })
  }
}