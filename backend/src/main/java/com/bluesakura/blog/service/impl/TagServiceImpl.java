package com.bluesakura.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bluesakura.blog.entity.BlogTag;
import com.bluesakura.blog.entity.Tag;
import com.bluesakura.blog.mapper.BlogTagMapper;
import com.bluesakura.blog.mapper.TagMapper;
import com.bluesakura.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    
    @Autowired
    private TagMapper tagMapper;
    
    @Autowired
    private BlogTagMapper blogTagMapper;
    
    @Override
    public List<Tag> list() {
        return tagMapper.selectList(null);
    }
    
    @Override
    public Tag getById(Long id) {
        return tagMapper.selectById(id);
    }
    
    @Override
    public Tag create(String name) {
        // 检查是否已存在
        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name);
        Tag existing = tagMapper.selectOne(wrapper);
        if (existing != null) {
            return existing;
        }
        
        Tag tag = new Tag();
        tag.setName(name);
        tag.setCreateTime(LocalDateTime.now());
        tagMapper.insert(tag);
        return tag;
    }
    
    @Override
    public void delete(Long id) {
        tagMapper.deleteById(id);
    }
    
    @Override
    public List<Tag> getTagsByBlogId(Long blogId) {
        return tagMapper.selectTagsByBlogId(blogId);
    }
    
    @Override
    @Transactional
    public void addTagsToBlog(Long blogId, List<Long> tagIds) {
        // 先删除旧的关联
        removeTagsFromBlog(blogId);
        
        // 添加新的关联
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                BlogTag blogTag = new BlogTag();
                blogTag.setBlogId(blogId);
                blogTag.setTagId(tagId);
                blogTagMapper.insert(blogTag);
            }
        }
    }
    
    @Override
    public void removeTagsFromBlog(Long blogId) {
        QueryWrapper<BlogTag> wrapper = new QueryWrapper<>();
        wrapper.eq("blog_id", blogId);
        blogTagMapper.delete(wrapper);
    }
    
    @Override
    public List<TagCount> getTagCounts() {
        return tagMapper.selectTagCounts();
    }
}
