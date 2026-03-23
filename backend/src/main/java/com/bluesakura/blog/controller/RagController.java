package com.bluesakura.blog.controller;

import com.bluesakura.blog.common.Result;
import com.bluesakura.blog.dto.RagRequest;
import com.bluesakura.blog.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class RagController {

    @Autowired
    private RagService ragService;

    @PostMapping("/rag")
    public Result<Map<String, Object>> ragQuery(@RequestBody RagRequest request) {
        Map<String, Object> result = ragService.query(request.getQuestion());
        return Result.success(result);
    }

    @GetMapping("/rag/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = ragService.getStats();
        return Result.success(stats);
    }

    @PostMapping("/rag/sync")
    public Result<String> syncBlogs() {
        ragService.syncAllBlogs();
        return Result.success("同步完成");
    }
}
