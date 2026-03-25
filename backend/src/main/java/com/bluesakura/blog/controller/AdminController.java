package com.bluesakura.blog.controller;

import com.bluesakura.blog.entity.User;
import com.bluesakura.blog.mapper.UserMapper;
import com.bluesakura.blog.security.UserPrincipal;
import com.bluesakura.blog.service.BlogService;
import com.bluesakura.blog.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final UserService userService;
    private final UserMapper userMapper;
    private final BlogService blogService;
    
    public AdminController(UserService userService, UserMapper userMapper, BlogService blogService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.blogService = blogService;
    }
    
    // 获取所有用户
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userMapper.selectList(null);
        return ResponseEntity.ok(users);
    }
    
    // 切换用户角色
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body("用户不存在");
        }
        user.setRole(role);
        userMapper.updateById(user);
        return ResponseEntity.ok("角色更新成功");
    }
    
    // 删除用户
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        // 禁止删除user1（ID为1的用户）
        if (id == 1) {
            return ResponseEntity.badRequest().body("无法删除主管理员");
        }
        userMapper.deleteById(id);
        return ResponseEntity.ok("用户删除成功");
    }
    
    // 管理员信息
    @GetMapping("/info")
    public ResponseEntity<?> getAdminInfo(@AuthenticationPrincipal UserPrincipal principal) {
        User user = userService.getCurrentUser(principal.getUserId());
        return ResponseEntity.ok(user);
    }
}