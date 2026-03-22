import request from './request'

export const tagApi = {
  // 获取标签列表
  list() {
    return request.get('/tag/list')
  },
  
  // 创建标签
  create(name) {
    return request.post('/tag', { name })
  },
  
  // 删除标签
  delete(id) {
    return request.delete(`/tag/${id}`)
  },
  
  // 获取博客的标签
  getBlogTags(blogId) {
    return request.get(`/tag/blog/${blogId}`)
  },
  
  // 为博客添加标签
  addTagsToBlog(blogId, tagIds) {
    return request.post(`/tag/blog/${blogId}`, tagIds)
  },
  
  // 获取标签云
  getTagCloud() {
    return request.get('/tag/cloud')
  }
}
