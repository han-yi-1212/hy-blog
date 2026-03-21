package com.bluesakura.blog.controller;

import com.bluesakura.blog.common.Result;
import com.bluesakura.blog.service.FeedbackService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }
    
    @PostMapping
    public Result<Void> createFeedback(
            @RequestParam Long blogId,
            @RequestParam Integer type,
            @RequestParam(required = false) String reason) {
        feedbackService.createFeedback(blogId, type, reason);
        return Result.success();
    }
}