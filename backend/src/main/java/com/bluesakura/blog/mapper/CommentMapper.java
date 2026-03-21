package com.bluesakura.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bluesakura.blog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    
    @Select("SELECT c.*, u.username, u.avatar as user_avatar FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.blog_id = #{blogId} " +
            "ORDER BY c.create_time DESC")
    List<Comment> selectByBlogId(@Param("blogId") Long blogId);
}
