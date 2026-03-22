package com.bluesakura.blog.service;

import com.bluesakura.blog.entity.Tag;
import java.util.List;

public interface TagService {
    List<Tag> list();
    Tag getById(Long id);
    Tag create(String name);
    void delete(Long id);
    List<Tag> getTagsByBlogId(Long blogId);
    void addTagsToBlog(Long blogId, List<Long> tagIds);
    void removeTagsFromBlog(Long blogId);
    List<TagCount> getTagCounts();
    
    class TagCount {
        private String name;
        private Integer count;
        
        public TagCount(String name, Integer count) {
            this.name = name;
            this.count = count;
        }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }
}
