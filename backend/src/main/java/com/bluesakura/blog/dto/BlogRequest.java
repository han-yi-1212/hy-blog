package com.bluesakura.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class BlogRequest {
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    private String coverImage;
    private String status; // draft: 草稿, published: 已发布
    private List<Long> tagIds; // 标签ID列表
}
