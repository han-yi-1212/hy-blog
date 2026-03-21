package com.bluesakura.blog.service;

import com.bluesakura.blog.entity.Feedback;

public interface FeedbackService {
    void createFeedback(Long blogId, Integer type, String reason);
}