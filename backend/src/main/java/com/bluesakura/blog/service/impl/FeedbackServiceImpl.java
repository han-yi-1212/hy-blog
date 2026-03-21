package com.bluesakura.blog.service.impl;

import com.bluesakura.blog.entity.Feedback;
import com.bluesakura.blog.mapper.FeedbackMapper;
import com.bluesakura.blog.service.FeedbackService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    
    private final FeedbackMapper feedbackMapper;
    
    public FeedbackServiceImpl(FeedbackMapper feedbackMapper) {
        this.feedbackMapper = feedbackMapper;
    }
    
    @Override
    public void createFeedback(Long blogId, Integer type, String reason) {
        Feedback feedback = new Feedback();
        feedback.setBlogId(blogId);
        feedback.setType(type);
        feedback.setReason(reason);
        feedback.setCreatedAt(LocalDateTime.now());
        feedbackMapper.insert(feedback);
    }
}