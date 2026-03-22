package com.bluesakura.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bluesakura.blog.entity.Tag;
import com.bluesakura.blog.service.TagService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    
    @Select("SELECT t.* FROM tag t INNER JOIN blog_tag bt ON t.id = bt.tag_id WHERE bt.blog_id = #{blogId}")
    List<Tag> selectTagsByBlogId(@Param("blogId") Long blogId);
    
    @Select("SELECT t.name, COUNT(bt.blog_id) as count FROM tag t LEFT JOIN blog_tag bt ON t.id = bt.tag_id GROUP BY t.id")
    List<TagService.TagCount> selectTagCounts();
}
