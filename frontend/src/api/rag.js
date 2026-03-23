import request from './request'

export const ragApi = {
  query(question) {
    return request.post('/ai/rag', { question })
  },
  
  getStats() {
    return request.get('/ai/rag/stats')
  },
  
  syncBlogs() {
    return request.post('/ai/rag/sync')
  }
}
