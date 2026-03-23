package com.bluesakura.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bluesakura.blog.dto.LoginRequest;
import com.bluesakura.blog.dto.LoginResponse;
import com.bluesakura.blog.dto.RegisterRequest;
import com.bluesakura.blog.entity.Blog;
import com.bluesakura.blog.entity.User;
import com.bluesakura.blog.mapper.BlogMapper;
import com.bluesakura.blog.mapper.UserMapper;
import com.bluesakura.blog.security.JwtUtil;
import com.bluesakura.blog.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final BlogMapper blogMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public UserServiceImpl(UserMapper userMapper, BlogMapper blogMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.blogMapper = blogMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    public void register(RegisterRequest request) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getUsername());
        User existUser = userMapper.selectOne(queryWrapper);
        
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        
        userMapper.insert(user);
    }
    
    @Override
    public LoginResponse login(LoginRequest request) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(queryWrapper);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        userInfo.setAvatar(user.getAvatar());
        response.setUser(userInfo);
        
        return response;
    }
    
    @Override
    public User getCurrentUser(Long userId) {
        return userMapper.selectById(userId);
    }
    
    @Override
    public void updateUser(User user) {
        // 获取旧用户信息
        User oldUser = userMapper.selectById(user.getId());
        if (oldUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 更新用户表
        userMapper.updateById(user);
        
        // 如果用户名发生变化，同步更新博客表中的用户名
        if (user.getUsername() != null && !user.getUsername().equals(oldUser.getUsername())) {
            LambdaUpdateWrapper<Blog> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Blog::getUserId, user.getId())
                        .set(Blog::getUsername, user.getUsername());
            blogMapper.update(null, updateWrapper);
        }
    }
}
