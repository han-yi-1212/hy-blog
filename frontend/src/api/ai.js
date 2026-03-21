import request from './request'

export const aiApi = {
  // AI生成博客
  generate(topic) {
    return request.post('/ai/generate', { topic })
  },
  
  // AI总结内容
  summarize(content) {
    return request.post('/ai/summary', { content })
  }
}
