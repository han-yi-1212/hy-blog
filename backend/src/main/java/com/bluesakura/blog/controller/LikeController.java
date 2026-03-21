package com.bluesakura.blog.controller;

import com.bluesakura.blog.common.Result;
import com.bluesakura.blog.dto.LikeResponse;
import com.bluesakura.blog.security.UserPrincipal;
import com.bluesakura.blog.service.LikeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blog")
public class LikeController {
    
    private final LikeService likeService;
    
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }
    
    @PostMapping("/like/{id}")
    public Result<LikeResponse> like(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        try {
            LikeResponse response = likeService.toggleLike(id, principal.getUserId());
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/like/{id}")
    public Result<Void> unlike(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        try {
            likeService.unlike(id, principal.getUserId());
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/like/count/{id}")
    public Result<Long> getLikeCount(@PathVariable Long id) {
        return Result.success(likeService.getLikeCount(id));
    }
}
