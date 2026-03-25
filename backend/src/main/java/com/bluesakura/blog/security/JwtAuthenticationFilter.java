package com.bluesakura.blog.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        
        if (StringUtils.hasText(token)) {
            try {
                if (jwtUtil.validateToken(token)) {
                    Long userId = jwtUtil.getUserId(token);
                    String username = jwtUtil.getUsername(token);
                    String role = jwtUtil.getRole(token);
                    
                    UserPrincipal principal = new UserPrincipal(userId, username, role);
                    
                    // 根据角色设置权限
                    String authority = "ROLE_USER";
                    if ("admin".equals(role)) {
                        authority = "ROLE_ADMIN";
                    }
                    
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority(authority))
                    );
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Token验证失败，但不阻止请求继续（对于permitAll的路径）
                // 记录日志但不做其他处理
                System.out.println("Token验证失败: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
