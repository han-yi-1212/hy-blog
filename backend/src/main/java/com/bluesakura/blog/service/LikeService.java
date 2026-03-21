package com.bluesakura.blog.service;

import com.bluesakura.blog.dto.LikeResponse;

public interface LikeService {
    LikeResponse toggleLike(Long blogId, Long userId);
    
    void unlike(Long blogId, Long userId);
    
    boolean hasLiked(Long blogId, Long userId);
    
    Long getLikeCount(Long blogId);
}
