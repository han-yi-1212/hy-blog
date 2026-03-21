package com.bluesakura.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bluesakura.blog.entity.Comment;
import com.bluesakura.blog.mapper.CommentMapper;
import com.bluesakura.blog.mapper.UserMapper;
import com.bluesakura.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private static final String COMMENT_LIKE_KEY = "comment:like:";
    
    @Override
    public List<Comment> getCommentsByBlogId(Long blogId, Long userId) {
        List<Comment> allComments = commentMapper.selectByBlogId(blogId);
        
        for (Comment comment : allComments) {
            if (userId != null) {
                comment.setLiked(isCommentLiked(userId, comment.getId()));
            } else {
                comment.setLiked(false);
            }
        }
        
        return buildCommentTree(allComments);
    }
    
    private List<Comment> buildCommentTree(List<Comment> comments) {
        Map<Long, List<Comment>> parentMap = comments.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Comment::getParentId));
        
        for (Comment comment : comments) {
            List<Comment> children = parentMap.get(comment.getId());
            if (children != null) {
                comment.setChildren(children);
            }
        }
        
        return comments.stream()
                .filter(c -> c.getParentId() == null)
                .collect(Collectors.toList());
    }
    
    @Override
    public Comment addComment(Long userId, Long blogId, String content, Long parentId, Long rootId) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setBlogId(blogId);
        comment.setContent(content);
        comment.setLikeCount(0);
        comment.setParentId(parentId);
        comment.setRootId(rootId);
        
        save(comment);
        
        List<Comment> comments = commentMapper.selectByBlogId(blogId);
        Comment savedComment = comments.stream()
                .filter(c -> c.getId().equals(comment.getId()))
                .findFirst()
                .orElse(comment);
        
        savedComment.setLiked(false);
        
        return savedComment;
    }
    
    @Override
    public boolean likeComment(Long userId, Long commentId) {
        String key = COMMENT_LIKE_KEY + commentId;
        
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userId.toString());
        if (Boolean.TRUE.equals(isMember)) {
            return false;
        }
        
        redisTemplate.opsForSet().add(key, userId.toString());
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
        
        Comment comment = getById(commentId);
        if (comment != null) {
            comment.setLikeCount(comment.getLikeCount() + 1);
            updateById(comment);
        }
        
        return true;
    }
    
    @Override
    public boolean unlikeComment(Long userId, Long commentId) {
        String key = COMMENT_LIKE_KEY + commentId;
        
        Long removed = redisTemplate.opsForSet().remove(key, userId.toString());
        
        if (removed != null && removed > 0) {
            Comment comment = getById(commentId);
            if (comment != null && comment.getLikeCount() > 0) {
                comment.setLikeCount(comment.getLikeCount() - 1);
                updateById(comment);
            }
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean isCommentLiked(Long userId, Long commentId) {
        String key = COMMENT_LIKE_KEY + commentId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId.toString()));
    }
}
