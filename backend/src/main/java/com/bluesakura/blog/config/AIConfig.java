package com.bluesakura.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {
    
    @Value("${ai.deepseek.api-key:sk-010f097d937349daa2a1858e78b846a2}")
    private String apiKey;
    
    @Value("${ai.deepseek.api-url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;
    
    @Value("${ai.deepseek.model:deepseek-chat}")
    private String model;
    
    @Value("${ai.deepseek.timeout:30}")
    private int timeout;
    
    public String getApiKey() {
        return apiKey;
    }
    
    public String getApiUrl() {
        return apiUrl;
    }
    
    public String getModel() {
        return model;
    }
    
    public int getTimeout() {
        return timeout;
    }
}