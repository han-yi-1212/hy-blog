package com.bluesakura.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("feedback")
public class Feedback {
    private Long id;
    private Long blogId;
    private Integer type;
    private String reason;
    private LocalDateTime createdAt;
}