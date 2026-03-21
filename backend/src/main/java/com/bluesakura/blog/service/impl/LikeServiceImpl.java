package com.bluesakura.blog.service.impl;

import com.bluesakura.blog.dto.LikeResponse;
import com.bluesakura.blog.entity.Blog;
import com.bluesakura.blog.mapper.BlogMapper;
import com.bluesakura.blog.service.LikeService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {
    
    private final StringRedisTemplate redisTemplate;
    private final BlogMapper blogMapper;
    
    private static final String LIKE_KEY_PREFIX = "blog:like:";
    
    public LikeServiceImpl(StringRedisTemplate redisTemplate, BlogMapper blogMapper) {
        this.redisTemplate = redisTemplate;
        this.blogMapper = blogMapper;
    }
    
    @Override
    public LikeResponse toggleLike(Long blogId, Long userId) {
        String key = LIKE_KEY_PREFIX + blogId;
        String userIdStr = String.valueOf(userId);
        
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userIdStr);
        boolean isLiked;
        
        if (Boolean.TRUE.equals(isMember)) {
            redisTemplate.opsForSet().remove(key, userIdStr);
            decrementLikeCount(blogId);
            isLiked = false;
        } else {
            redisTemplate.opsForSet().add(key, userIdStr);
            incrementLikeCount(blogId);
            isLiked = true;
        }
        
        Long likeCount = getLikeCount(blogId);
        return new LikeResponse(likeCount, isLiked);
    }
    
    @Override
    public void unlike(Long blogId, Long userId) {
        String key = LIKE_KEY_PREFIX + blogId;
        String userIdStr = String.valueOf(userId);
        
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userIdStr);
        
        if (Boolean.TRUE.equals(isMember)) {
            redisTemplate.opsForSet().remove(key, userIdStr);
            decrementLikeCount(blogId);
        }
    }
    
    @Override
    public boolean hasLiked(Long blogId, Long userId) {
        String key = LIKE_KEY_PREFIX + blogId;
        String userIdStr = String.valueOf(userId);
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userIdStr);
        return Boolean.TRUE.equals(isMember);
    }
    
    @Override
    public Long getLikeCount(Long blogId) {
        String key = LIKE_KEY_PREFIX + blogId;
        Long count = redisTemplate.opsForSet().size(key);
        return count != null ? count : 0L;
    }
    
    private void incrementLikeCount(Long blogId) {
        Blog blog = blogMapper.selectById(blogId);
        if (blog != null) {
            blog.setLikeCount(blog.getLikeCount() + 1);
            blogMapper.updateById(blog);
        }
    }
    
    private void decrementLikeCount(Long blogId) {
        Blog blog = blogMapper.selectById(blogId);
        if (blog != null && blog.getLikeCount() > 0) {
            blog.setLikeCount(blog.getLikeCount() - 1);
            blogMapper.updateById(blog);
        }
    }
}
