package com.bluesakura.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bluesakura.blog.dto.BlogRequest;
import com.bluesakura.blog.dto.BlogVO;
import com.bluesakura.blog.entity.Blog;
import com.bluesakura.blog.entity.User;
import com.bluesakura.blog.mapper.BlogMapper;
import com.bluesakura.blog.mapper.UserMapper;
import com.bluesakura.blog.service.BlogService;
import com.bluesakura.blog.service.LikeService;
import com.bluesakura.blog.service.RagService;
import com.bluesakura.blog.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService {
    
    private final BlogMapper blogMapper;
    private final UserMapper userMapper;
    private final LikeService likeService;
    private final TagService tagService;
    private final RagService ragService;
    
    public BlogServiceImpl(BlogMapper blogMapper, UserMapper userMapper, LikeService likeService, TagService tagService, RagService ragService) {
        this.blogMapper = blogMapper;
        this.userMapper = userMapper;
        this.likeService = likeService;
        this.tagService = tagService;
        this.ragService = ragService;
    }
    
    @Override
    @Transactional
    public void createBlog(Long userId, BlogRequest request) {
        // 标签校验 - 发布状态必须选择标签
        if ("published".equals(request.getStatus()) && (request.getTagIds() == null || request.getTagIds().isEmpty())) {
            throw new RuntimeException("发布文章必须选择至少一个标签");
        }
        
        Blog blog = new Blog();
        blog.setUserId(userId);
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setCoverImage(request.getCoverImage());
        blog.setStatus(request.getStatus() != null ? request.getStatus() : "published");
        blog.setLikeCount(0);
        blog.setViewCount(0);
        
        // 计算字数和阅读时间
        int wordCount = request.getContent().length();
        int readTime = (int) Math.ceil(wordCount / 300.0);
        blog.setWordCount(wordCount);
        blog.setReadTime(readTime);
        
        blog.setCreateTime(LocalDateTime.now());
        blog.setUpdateTime(LocalDateTime.now());
        
        blogMapper.insert(blog);
        
        // 添加标签
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            tagService.addTagsToBlog(blog.getId(), request.getTagIds());
        }
        
        // 添加到知识库
        if ("published".equals(blog.getStatus())) {
            ragService.addBlogToKnowledge(blog.getId(), blog.getTitle(), blog.getContent());
        }
    }
    
    @Override
    @Transactional
    public void updateBlog(Long userId, Long blogId, BlogRequest request) {
        Blog blog = blogMapper.selectById(blogId);
        
        if (blog == null) {
            throw new RuntimeException("博客不存在");
        }
        
        if (!blog.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此博客");
        }
        
        // 标签校验 - 发布状态必须选择标签
        String status = request.getStatus() != null ? request.getStatus() : blog.getStatus();
        if ("published".equals(status) && (request.getTagIds() == null || request.getTagIds().isEmpty())) {
            throw new RuntimeException("发布文章必须选择至少一个标签");
        }
        
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setCoverImage(request.getCoverImage());
        if (request.getStatus() != null) {
            blog.setStatus(request.getStatus());
        }
        
        // 重新计算字数和阅读时间
        int wordCount = request.getContent().length();
        int readTime = (int) Math.ceil(wordCount / 300.0);
        blog.setWordCount(wordCount);
        blog.setReadTime(readTime);
        
        blog.setUpdateTime(LocalDateTime.now());
        
        blogMapper.updateById(blog);
        
        // 更新标签
        if (request.getTagIds() != null) {
            tagService.addTagsToBlog(blogId, request.getTagIds());
        }
        
        // 更新知识库
        if ("published".equals(blog.getStatus())) {
            ragService.addBlogToKnowledge(blog.getId(), blog.getTitle(), blog.getContent());
        }
    }
    
    @Override
    @Transactional
    public void deleteBlog(Long userId, Long blogId) {
        Blog blog = blogMapper.selectById(blogId);
        
        if (blog == null) {
            throw new RuntimeException("博客不存在");
        }
        
        if (!blog.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此博客");
        }
        
        // 删除标签关联
        tagService.removeTagsFromBlog(blogId);
        
        blogMapper.deleteById(blogId);
    }
    
    @Override
    public Page<BlogVO> getBlogList(Integer page, Integer size, Long currentUserId) {
        Page<Blog> blogPage = new Page<>(page, size);
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        // 查询已发布的或status为null的（兼容旧数据）
        queryWrapper.and(wrapper -> wrapper
            .eq(Blog::getStatus, "published")
            .or()
            .isNull(Blog::getStatus)
        );
        queryWrapper.orderByDesc(Blog::getCreateTime);
        
        blogMapper.selectPage(blogPage, queryWrapper);
        
        Page<BlogVO> voPage = new Page<>(blogPage.getCurrent(), blogPage.getSize(), blogPage.getTotal());
        
        List<BlogVO> voList = blogPage.getRecords().stream().map(blog -> {
            BlogVO vo = convertToVO(blog, currentUserId);
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
    
    @Override
    public BlogVO getBlogDetail(Long blogId, Long currentUserId) {
        Blog blog = blogMapper.selectById(blogId);
        
        if (blog == null) {
            throw new RuntimeException("博客不存在");
        }
        
        return convertToVO(blog, currentUserId);
    }
    
    @Override
    public void incrementViewCount(Long blogId) {
        Blog blog = blogMapper.selectById(blogId);
        if (blog != null) {
            blog.setViewCount(blog.getViewCount() + 1);
            blogMapper.updateById(blog);
        }
    }
    
    @Override
    public Page<BlogVO> searchBlogs(String keyword, Integer page, Integer size, Long currentUserId) {
        Page<Blog> blogPage = new Page<>(page, size);
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        // 查询已发布的或status为null的（兼容旧数据）
        queryWrapper.and(wrapper -> wrapper
            .eq(Blog::getStatus, "published")
            .or()
            .isNull(Blog::getStatus)
        );
        queryWrapper.and(wrapper -> wrapper
            .like(Blog::getTitle, keyword)
            .or()
            .like(Blog::getContent, keyword)
        );
        queryWrapper.orderByDesc(Blog::getCreateTime);
        
        blogMapper.selectPage(blogPage, queryWrapper);
        
        Page<BlogVO> voPage = new Page<>(blogPage.getCurrent(), blogPage.getSize(), blogPage.getTotal());
        
        List<BlogVO> voList = blogPage.getRecords().stream().map(blog -> {
            BlogVO vo = convertToVO(blog, currentUserId);
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
    
    @Override
    public List<BlogVO> getRandomBlogs(Integer size, Long currentUserId) {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        // 查询已发布的或status为null的（兼容旧数据）
        queryWrapper.and(wrapper -> wrapper
            .eq(Blog::getStatus, "published")
            .or()
            .isNull(Blog::getStatus)
        );
        queryWrapper.last("ORDER BY RAND() LIMIT " + size);
        
        List<Blog> blogs = blogMapper.selectList(queryWrapper);
        
        return blogs.stream().map(blog -> {
            return convertToVO(blog, currentUserId);
        }).collect(Collectors.toList());
    }
    
    @Override
    public Page<BlogVO> getBlogsByTag(Long tagId, Integer page, Integer size, Long currentUserId) {
        Page<Blog> blogPage = new Page<>(page, size);
        List<Long> blogIds = blogMapper.selectBlogIdsByTagId(tagId);
        
        if (blogIds.isEmpty()) {
            return new Page<>();
        }
        
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Blog::getId, blogIds);
        // 查询已发布的或status为null的（兼容旧数据）
        queryWrapper.and(wrapper -> wrapper
            .eq(Blog::getStatus, "published")
            .or()
            .isNull(Blog::getStatus)
        );
        queryWrapper.orderByDesc(Blog::getCreateTime);
        
        blogMapper.selectPage(blogPage, queryWrapper);
        
        Page<BlogVO> voPage = new Page<>(blogPage.getCurrent(), blogPage.getSize(), blogPage.getTotal());
        
        List<BlogVO> voList = blogPage.getRecords().stream().map(blog -> {
            return convertToVO(blog, currentUserId);
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
    
    @Override
    public Page<BlogVO> getDraftList(Long userId, Integer page, Integer size) {
        Page<Blog> blogPage = new Page<>(page, size);
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getUserId, userId);
        queryWrapper.eq(Blog::getStatus, "draft");
        queryWrapper.orderByDesc(Blog::getUpdateTime);
        
        blogMapper.selectPage(blogPage, queryWrapper);
        
        Page<BlogVO> voPage = new Page<>(blogPage.getCurrent(), blogPage.getSize(), blogPage.getTotal());
        
        List<BlogVO> voList = blogPage.getRecords().stream().map(blog -> {
            return convertToVO(blog, userId);
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
    
    private BlogVO convertToVO(Blog blog, Long currentUserId) {
        BlogVO vo = BlogVO.fromBlog(blog);
        
        User user = userMapper.selectById(blog.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        }
        
        if (currentUserId != null) {
            vo.setLiked(likeService.hasLiked(blog.getId(), currentUserId));
        } else {
            vo.setLiked(false);
        }
        
        // 加载标签
        vo.setTags(tagService.getTagsByBlogId(blog.getId()));
        
        return vo;
    }
}
