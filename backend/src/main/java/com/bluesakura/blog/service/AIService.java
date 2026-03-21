package com.bluesakura.blog.service;

public interface AIService {
    /**
     * 生成博客文章
     * @param topic 文章主题
     * @return 生成的文章内容
     */
    String generateBlog(String topic);
    
    /**
     * 总结文章内容
     * @param content 文章内容
     * @return 文章总结
     */
    String summarizeContent(String content);
}
