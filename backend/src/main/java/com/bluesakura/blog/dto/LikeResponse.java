package com.bluesakura.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeResponse {
    private Long likeCount;
    private Boolean isLiked;
}
