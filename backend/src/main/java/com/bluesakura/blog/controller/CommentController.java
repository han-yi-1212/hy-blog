package com.bluesakura.blog.controller;

import com.bluesakura.blog.common.Result;
import com.bluesakura.blog.entity.Comment;
import com.bluesakura.blog.service.CommentService;
import com.bluesakura.blog.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/list")
    public Result<List<Comment>> getComments(@RequestParam Long blogId, HttpServletRequest request) {
        Long userId = null;
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                userId = jwtUtil.getUserId(token);
            } catch (Exception e) {
            }
        }
        
        List<Comment> comments = commentService.getCommentsByBlogId(blogId, userId);
        return Result.success(comments);
    }
    
    @PostMapping("/add")
    public Result<Comment> addComment(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("请先登录");
        }
        
        token = token.substring(7);
        Long userId = jwtUtil.getUserId(token);
        
        Long blogId = Long.parseLong(params.get("blogId").toString());
        String content = params.get("content").toString();
        
        Long parentId = null;
        Long rootId = null;
        
        if (params.get("parentId") != null) {
            parentId = Long.parseLong(params.get("parentId").toString());
        }
        if (params.get("rootId") != null) {
            rootId = Long.parseLong(params.get("rootId").toString());
        }
        
        if (content == null || content.trim().isEmpty()) {
            return Result.error("评论内容不能为空");
        }
        
        Comment comment = commentService.addComment(userId, blogId, content.trim(), parentId, rootId);
        return Result.success(comment);
    }
    
    @PostMapping("/like/{id}")
    public Result<Map<String, Object>> likeComment(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("请先登录");
        }
        
        token = token.substring(7);
        Long userId = jwtUtil.getUserId(token);
        
        boolean success = commentService.likeComment(userId, id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", true);
        
        Comment comment = commentService.getById(id);
        if (comment != null) {
            result.put("likeCount", comment.getLikeCount());
        }
        
        return Result.success(result);
    }
    
    @DeleteMapping("/like/{id}")
    public Result<Map<String, Object>> unlikeComment(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("请先登录");
        }
        
        token = token.substring(7);
        Long userId = jwtUtil.getUserId(token);
        
        boolean success = commentService.unlikeComment(userId, id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", false);
        
        Comment comment = commentService.getById(id);
        if (comment != null) {
            result.put("likeCount", comment.getLikeCount());
        }
        
        return Result.success(result);
    }
}
