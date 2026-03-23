package com.bluesakura.blog.dto;

import com.bluesakura.blog.entity.Blog;
import com.bluesakura.blog.entity.Tag;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BlogVO {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String title;
    private String content;
    private String coverImage;
    private Integer likeCount;
    private Integer viewCount;
    private Integer wordCount;
    private Integer readTime;
    private Boolean liked;
    private String status;
    private List<Tag> tags;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    public static BlogVO fromBlog(Blog blog) {
        BlogVO vo = new BlogVO();
        vo.setId(blog.getId());
        vo.setUserId(blog.getUserId());
        vo.setTitle(blog.getTitle());
        vo.setContent(blog.getContent());
        vo.setCoverImage(blog.getCoverImage());
        vo.setLikeCount(blog.getLikeCount());
        vo.setViewCount(blog.getViewCount());
        vo.setWordCount(blog.getWordCount());
        vo.setReadTime(blog.getReadTime());
        vo.setCreateTime(blog.getCreateTime());
        vo.setUpdateTime(blog.getUpdateTime());
        return vo;
    }
}