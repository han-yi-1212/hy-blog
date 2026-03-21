import request from './request'

export const commentApi = {
  getList(blogId) {
    return request.get('/comment/list', { params: { blogId } })
  },
  
  add(blogId, content, parentId = null, rootId = null) {
    return request.post('/comment/add', { blogId, content, parentId, rootId })
  },
  
  like(id) {
    return request.post(`/comment/like/${id}`)
  },
  
  unlike(id) {
    return request.delete(`/comment/like/${id}`)
  }
}
