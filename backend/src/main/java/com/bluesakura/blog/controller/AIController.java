package com.bluesakura.blog.controller;

import com.bluesakura.blog.common.Result;
import com.bluesakura.blog.service.AIService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    
    private final AIService aiService;
    
    public AIController(AIService aiService) {
        this.aiService = aiService;
    }
    
    @PostMapping("/generate")
    public Result<String> generateBlog(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        if (topic == null || topic.isEmpty()) {
            return Result.error("主题不能为空");
        }
        
        String content = aiService.generateBlog(topic);
        return Result.success(content);
    }
    
    @PostMapping("/summary")
    public Result<String> summarizeContent(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        if (content == null || content.isEmpty()) {
            return Result.error("内容不能为空");
        }
        
        String summary = aiService.summarizeContent(content);
        return Result.success(summary);
    }
}
