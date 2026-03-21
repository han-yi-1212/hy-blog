package com.bluesakura.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long blogId;
    
    private Long userId;
    
    private String content;
    
    private Integer likeCount;
    
    private Long parentId;
    
    private Long rootId;
    
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private String username;
    
    @TableField(exist = false)
    private String userAvatar;
    
    @TableField(exist = false)
    private Boolean liked;
    
    @TableField(exist = false)
    private List<Comment> children;
}
