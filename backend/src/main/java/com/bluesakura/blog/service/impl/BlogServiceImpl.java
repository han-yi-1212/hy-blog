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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService {
    
    private final BlogMapper blogMapper;
    private final UserMapper userMapper;
    private final LikeService likeService;
    
    public BlogServiceImpl(BlogMapper blogMapper, UserMapper userMapper, LikeService likeService) {
        this.blogMapper = blogMapper;
        this.userMapper = userMapper;
        this.likeService = likeService;
    }
    
    @Override
    public void createBlog(Long userId, BlogRequest request) {
        Blog blog = new Blog();
        blog.setUserId(userId);
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setCoverImage(request.getCoverImage());
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
    }
    
    @Override
    public void updateBlog(Long userId, Long blogId, BlogRequest request) {
        Blog blog = blogMapper.selectById(blogId);
        
        if (blog == null) {
            throw new RuntimeException("博客不存在");
        }
        
        if (!blog.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此博客");
        }
        
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setCoverImage(request.getCoverImage());
        
        // 重新计算字数和阅读时间
        int wordCount = request.getContent().length();
        int readTime = (int) Math.ceil(wordCount / 300.0);
        blog.setWordCount(wordCount);
        blog.setReadTime(readTime);
        
        blog.setUpdateTime(LocalDateTime.now());
        
        blogMapper.updateById(blog);
    }
    
    @Override
    public void deleteBlog(Long userId, Long blogId) {
        Blog blog = blogMapper.selectById(blogId);
        
        if (blog == null) {
            throw new RuntimeException("博客不存在");
        }
        
        if (!blog.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此博客");
        }
        
        blogMapper.deleteById(blogId);
    }
    
    @Override
    public Page<BlogVO> getBlogList(Integer page, Integer size, Long currentUserId) {
        Page<Blog> blogPage = new Page<>(page, size);
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Blog::getCreateTime);
        
        blogMapper.selectPage(blogPage, queryWrapper);
        
        Page<BlogVO> voPage = new Page<>(blogPage.getCurrent(), blogPage.getSize(), blogPage.getTotal());
        
        List<BlogVO> voList = blogPage.getRecords().stream().map(blog -> {
            BlogVO vo = BlogVO.fromBlog(blog);
            
            User user = userMapper.selectById(blog.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
            }
            
            if (currentUserId != null) {
                vo.setLiked(likeService.hasLiked(blog.getId(), currentUserId));
            } else {
                vo.setLiked(false);
            }
            
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
        
        BlogVO vo = BlogVO.fromBlog(blog);
        
        User user = userMapper.selectById(blog.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }
        
        if (currentUserId != null) {
            vo.setLiked(likeService.hasLiked(blog.getId(), currentUserId));
        } else {
            vo.setLiked(false);
        }
        
        return vo;
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
        queryWrapper.like(Blog::getTitle, keyword)
                   .or()
                   .like(Blog::getContent, keyword)
                   .orderByDesc(Blog::getCreateTime);
        
        blogMapper.selectPage(blogPage, queryWrapper);
        
        Page<BlogVO> voPage = new Page<>(blogPage.getCurrent(), blogPage.getSize(), blogPage.getTotal());
        
        List<BlogVO> voList = blogPage.getRecords().stream().map(blog -> {
            BlogVO vo = BlogVO.fromBlog(blog);
            
            User user = userMapper.selectById(blog.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
            }
            
            if (currentUserId != null) {
                vo.setLiked(likeService.hasLiked(blog.getId(), currentUserId));
            } else {
                vo.setLiked(false);
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
    
    @Override
    public List<BlogVO> getRandomBlogs(Integer size, Long currentUserId) {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.last("ORDER BY RAND() LIMIT " + size);
        
        List<Blog> blogs = blogMapper.selectList(queryWrapper);
        
        return blogs.stream().map(blog -> {
            BlogVO vo = BlogVO.fromBlog(blog);
            
            User user = userMapper.selectById(blog.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
            }
            
            if (currentUserId != null) {
                vo.setLiked(likeService.hasLiked(blog.getId(), currentUserId));
            } else {
                vo.setLiked(false);
            }
            
            return vo;
        }).collect(Collectors.toList());
    }
}