package com.bluesakura.blog.service;

import java.util.Map;

public interface RagService {
    Map<String, Object> query(String question);
    Map<String, Object> getStats();
    void addBlogToKnowledge(Long blogId, String title, String content);
    void syncAllBlogs();
}
