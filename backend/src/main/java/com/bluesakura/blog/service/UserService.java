package com.bluesakura.blog.service;

import com.bluesakura.blog.dto.LoginRequest;
import com.bluesakura.blog.dto.LoginResponse;
import com.bluesakura.blog.dto.RegisterRequest;
import com.bluesakura.blog.entity.User;

public interface UserService {
    void register(RegisterRequest request);
    
    LoginResponse login(LoginRequest request);
    
    User getCurrentUser(Long userId);
    
    void updateUser(User user);
}
