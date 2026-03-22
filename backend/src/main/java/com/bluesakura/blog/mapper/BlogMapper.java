package com.bluesakura.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bluesakura.blog.entity.Blog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BlogMapper extends BaseMapper<Blog> {
    
    @Select("SELECT blog_id FROM blog_tag WHERE tag_id = #{tagId}")
    List<Long> selectBlogIdsByTagId(@Param("tagId") Long tagId);
}
