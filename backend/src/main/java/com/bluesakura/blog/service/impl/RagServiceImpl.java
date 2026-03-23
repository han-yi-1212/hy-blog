package com.bluesakura.blog.service.impl;

import com.bluesakura.blog.entity.Blog;
import com.bluesakura.blog.mapper.BlogMapper;
import com.bluesakura.blog.service.RagService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class RagServiceImpl implements RagService {

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Map<String, Object> query(String question) {
        try {
            String url = aiServiceUrl + "/api/ai/rag";
            
            Map<String, String> body = new HashMap<>();
            body.put("question", question);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }
            
            return Map.of(
                "answer", "AI服务暂时不可用",
                "sources", Collections.emptyList(),
                "has_context", false
            );
        } catch (Exception e) {
            return Map.of(
                "answer", "AI服务调用失败: " + e.getMessage(),
                "sources", Collections.emptyList(),
                "has_context", false
            );
        }
    }

    @Override
    public Map<String, Object> getStats() {
        try {
            String url = aiServiceUrl + "/api/ai/rag/stats";
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }
            
            return Map.of("document_count", 0, "documents", Collections.emptyList());
        } catch (Exception e) {
            return Map.of("document_count", 0, "documents", Collections.emptyList(), "error", e.getMessage());
        }
    }

    @Override
    public void addBlogToKnowledge(Long blogId, String title, String content) {
        try {
            String url = aiServiceUrl + "/api/ai/rag/add";
            
            Map<String, Object> body = new HashMap<>();
            body.put("id", blogId);
            body.put("title", title);
            body.put("content", content);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            System.err.println("添加博客到知识库失败: " + e.getMessage());
        }
    }

    @Override
    public void syncAllBlogs() {
        // 只同步已发布的博客，排除草稿
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "published").or().isNull("status");
        List<Blog> blogs = blogMapper.selectList(queryWrapper);
        
        try {
            // 第一步：清空知识库
            String clearUrl = aiServiceUrl + "/api/ai/rag/clear";
            restTemplate.postForEntity(clearUrl, null, Map.class);
            
            // 第二步：如果没有博客，直接返回
            if (blogs.isEmpty()) {
                return;
            }
            
            // 第三步：批量添加博客到知识库
            String url = aiServiceUrl + "/api/ai/rag/batch";
            
            List<Map<String, Object>> blogList = new ArrayList<>();
            for (Blog blog : blogs) {
                Map<String, Object> blogData = new HashMap<>();
                blogData.put("id", blog.getId());
                blogData.put("title", blog.getTitle());
                blogData.put("content", blog.getContent());
                blogList.add(blogData);
            }
            
            Map<String, Object> body = new HashMap<>();
            body.put("blogs", blogList);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            System.err.println("同步博客到知识库失败: " + e.getMessage());
        }
    }
}
