package com.bluesakura.blog.service.impl;

import com.bluesakura.blog.service.AIService;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AIServiceImpl implements AIService {
    
    private final OkHttpClient client;
    private final String pythonServiceUrl = "http://localhost:8000";
    private final ObjectMapper objectMapper;
    
    public AIServiceImpl() {
        this.client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public String generateBlog(String topic) {
        try {
            String json = "{" +
                    "\"topic\": \"" + escapeJson(topic) + "\"" +
                    "}";
            
            return callPythonService("/api/ai/generate", json);
        } catch (Exception e) {
            // 错误兜底
            return "AI暂时不可用，请稍后再试";
        }
    }
    
    @Override
    public String summarizeContent(String content) {
        try {
            // 控制token消耗
            if (content.length() > 2000) {
                content = content.substring(0, 2000);
            }
            
            String json = "{" +
                    "\"content\": \"" + escapeJson(content) + "\"" +
                    "}";
            
            return callPythonService("/api/ai/summary", json);
        } catch (Exception e) {
            // 错误兜底
            return "AI暂时不可用，请稍后再试";
        }
    }
    
    private String callPythonService(String endpoint, String json) throws IOException {
        RequestBody body = RequestBody.create(
                json, MediaType.parse("application/json")
        );
        
        Request request = new Request.Builder()
                .url(pythonServiceUrl + endpoint)
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            String responseBody = response.body().string();
            // 解析响应
            return parsePythonResponse(responseBody);
        }
    }
    
    private String escapeJson(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
    
    private String parsePythonResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            if (root.has("content")) {
                return root.get("content").asText();
            } else {
                return "AI响应格式错误";
            }
        } catch (Exception e) {
            return "AI响应格式错误: " + e.getMessage();
        }
    }
}
