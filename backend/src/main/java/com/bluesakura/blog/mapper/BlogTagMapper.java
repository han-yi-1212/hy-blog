package com.bluesakura.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bluesakura.blog.entity.BlogTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BlogTagMapper extends BaseMapper<BlogTag> {
    
    void deleteByBlogId(@Param("blogId") Long blogId);
}
