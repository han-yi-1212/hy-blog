package com.bluesakura.blog.controller;

import com.bluesakura.blog.common.Result;
import com.bluesakura.blog.dto.LoginRequest;
import com.bluesakura.blog.dto.LoginResponse;
import com.bluesakura.blog.dto.RegisterRequest;
import com.bluesakura.blog.entity.User;
import com.bluesakura.blog.security.JwtUtil;
import com.bluesakura.blog.security.UserPrincipal;
import com.bluesakura.blog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/info")
    public Result<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(userService.getCurrentUser(principal.getUserId()));
    }
    
    @GetMapping("/profile/{id}")
    public Result<Map<String, Object>> getUserById(@PathVariable Long id) {
        User user = userService.getCurrentUser(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname() != null ? user.getNickname() : user.getUsername());
        result.put("avatar", user.getAvatar());
        result.put("bio", user.getBio());
        result.put("createTime", user.getCreateTime());
        
        return Result.success(result);
    }
    
    @PostMapping("/update")
    public Result<Map<String, Object>> updateUser(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("请先登录");
        }
        
        token = token.substring(7);
        Long userId = jwtUtil.getUserId(token);
        
        User user = userService.getCurrentUser(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if (params.get("nickname") != null) {
            user.setNickname(params.get("nickname").toString());
        }
        if (params.get("avatar") != null) {
            user.setAvatar(params.get("avatar").toString());
        }
        if (params.get("bio") != null) {
            user.setBio(params.get("bio").toString());
        }
        
        userService.updateUser(user);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("bio", user.getBio());
        
        return Result.success(result);
    }
}
