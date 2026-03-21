package com.bluesakura.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bluesakura.blog.dto.BlogRequest;
import com.bluesakura.blog.dto.BlogVO;

import java.util.List;

public interface BlogService {
    void createBlog(Long userId, BlogRequest request);
    
    void updateBlog(Long userId, Long blogId, BlogRequest request);
    
    void deleteBlog(Long userId, Long blogId);
    
    Page<BlogVO> getBlogList(Integer page, Integer size, Long currentUserId);
    
    BlogVO getBlogDetail(Long blogId, Long currentUserId);
    
    void incrementViewCount(Long blogId);
    
    Page<BlogVO> searchBlogs(String keyword, Integer page, Integer size, Long currentUserId);
    
    List<BlogVO> getRandomBlogs(Integer size, Long currentUserId);
}
