package com.bluesakura.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bluesakura.blog.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {
    
    List<Comment> getCommentsByBlogId(Long blogId, Long userId);
    
    Comment addComment(Long userId, Long blogId, String content, Long parentId, Long rootId);
    
    boolean likeComment(Long userId, Long commentId);
    
    boolean unlikeComment(Long userId, Long commentId);
    
    boolean isCommentLiked(Long userId, Long commentId);
}
