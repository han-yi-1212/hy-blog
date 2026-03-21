package com.bluesakura.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("blog_like")
public class BlogLike {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long blogId;
}
