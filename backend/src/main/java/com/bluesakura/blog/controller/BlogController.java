package com.bluesakura.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bluesakura.blog.common.Result;
import com.bluesakura.blog.dto.BlogRequest;
import com.bluesakura.blog.dto.BlogVO;
import com.bluesakura.blog.security.UserPrincipal;
import com.bluesakura.blog.service.BlogService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog")
public class BlogController {
    
    private final BlogService blogService;
    
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }
    
    @PostMapping("/add")
    public Result<Void> createBlog(@Valid @RequestBody BlogRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        try {
            blogService.createBlog(principal.getUserId(), request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<Void> updateBlog(@PathVariable Long id, @Valid @RequestBody BlogRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        try {
            blogService.updateBlog(principal.getUserId(), id, request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteBlog(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        try {
            blogService.deleteBlog(principal.getUserId(), id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/list")
    public Result<Page<BlogVO>> getBlogList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long currentUserId = principal != null ? principal.getUserId() : null;
        return Result.success(blogService.getBlogList(page, size, currentUserId));
    }
    
    @GetMapping("/{id}")
    public Result<BlogVO> getBlogDetail(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        try {
            Long currentUserId = principal != null ? principal.getUserId() : null;
            return Result.success(blogService.getBlogDetail(id, currentUserId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/view/{id}")
    public Result<Void> incrementViewCount(@PathVariable Long id) {
        blogService.incrementViewCount(id);
        return Result.success();
    }
    
    @GetMapping("/search")
    public Result<Page<BlogVO>> searchBlogs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long currentUserId = principal != null ? principal.getUserId() : null;
        return Result.success(blogService.searchBlogs(keyword, page, size, currentUserId));
    }
    
    @GetMapping("/random")
    public Result<List<BlogVO>> getRandomBlogs(
            @RequestParam(defaultValue = "5") Integer size,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long currentUserId = principal != null ? principal.getUserId() : null;
        return Result.success(blogService.getRandomBlogs(size, currentUserId));
    }
    
    @GetMapping("/byTag/{tagId}")
    public Result<Page<BlogVO>> getBlogsByTag(
            @PathVariable Long tagId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long currentUserId = principal != null ? principal.getUserId() : null;
        return Result.success(blogService.getBlogsByTag(tagId, page, size, currentUserId));
    }
    
    @GetMapping("/drafts")
    public Result<Page<BlogVO>> getDraftList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return Result.success(blogService.getDraftList(principal.getUserId(), page, size));
    }
}
