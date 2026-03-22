package com.bluesakura.blog.controller;

import com.bluesakura.blog.common.Result;
import com.bluesakura.blog.entity.Tag;
import com.bluesakura.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class TagController {
    
    @Autowired
    private TagService tagService;
    
    @GetMapping("/list")
    public Result list() {
        return Result.success(tagService.list());
    }
    
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        Tag tag = tagService.getById(id);
        if (tag == null) {
            return Result.error("标签不存在");
        }
        return Result.success(tag);
    }
    
    @PostMapping
    public Result create(@RequestParam String name) {
        Tag tag = tagService.create(name);
        return Result.success(tag);
    }
    
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        tagService.delete(id);
        return Result.success();
    }
    
    @GetMapping("/blog/{blogId}")
    public Result getTagsByBlogId(@PathVariable Long blogId) {
        return Result.success(tagService.getTagsByBlogId(blogId));
    }
    
    @PostMapping("/blog/{blogId}")
    public Result addTagsToBlog(@PathVariable Long blogId, @RequestBody List<Long> tagIds) {
        tagService.addTagsToBlog(blogId, tagIds);
        return Result.success();
    }
    
    @GetMapping("/cloud")
    public Result getTagCloud() {
        return Result.success(tagService.getTagCounts());
    }
}
