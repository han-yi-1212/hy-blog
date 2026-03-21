package com.bluesakura.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bluesakura.blog.entity.Blog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogMapper extends BaseMapper<Blog> {
}
