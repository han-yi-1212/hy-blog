package com.bluesakura.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("blog")
public class Blog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String coverImage;
    private Integer likeCount;
    private Integer viewCount;
    private Integer wordCount;
    private Integer readTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    private String status; // draft: 草稿, published: 已发布
    
    @TableField(exist = false)
    private String username;
    
    @TableField(exist = false)
    private Boolean liked;
}