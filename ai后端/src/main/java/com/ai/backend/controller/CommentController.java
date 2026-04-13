package com.ai.backend.controller;

import com.ai.backend.common.Result;
import com.ai.backend.entity.Comment;
import com.ai.backend.entity.User;
import com.ai.backend.mapper.BlogMapper;
import com.ai.backend.mapper.CommentRepository;
import com.ai.backend.mapper.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogMapper blogMapper;

    @GetMapping("/blog/{blogId}")
    public Result<List<Map<String, Object>>> getCommentsByBlogId(@PathVariable Long blogId) {
        List<Comment> comments = commentRepository.findByBlogId(blogId);
        Long authorId = null;
        try {
            var blogOpt = blogMapper.findById(blogId);
            if (blogOpt.isPresent()) {
                authorId = blogOpt.get().getAuthorId();
            }
        } catch (Exception e) {
            log.warn("获取博客作者信息失败, blogId={}", blogId);
        }
        final Long finalAuthorId = authorId;
        List<Map<String, Object>> authorComments = new ArrayList<>();
        List<Map<String, Object>> otherComments = new ArrayList<>();
        for (Comment comment : comments) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", comment.getId());
            map.put("blogId", comment.getBlogId());
            map.put("userId", comment.getUserId());
            map.put("parentId", comment.getParentId());
            map.put("content", comment.getContent());
            map.put("createTime", comment.getCreateTime());
            boolean isAuthor = finalAuthorId != null && finalAuthorId.equals(comment.getUserId());
            map.put("isAuthor", isAuthor);
            if (comment.getUserId() != null) {
                userRepository.findById(comment.getUserId()).ifPresent(user -> {
                    map.put("nickname", user.getUsername());
                    map.put("avatar", user.getAvatar());
                });
            }
            if (isAuthor) {
                authorComments.add(map);
            } else {
                otherComments.add(map);
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        result.addAll(authorComments);
        result.addAll(otherComments);
        return Result.success(result);
    }

    @PostMapping
    public Result<Map<String, Object>> createComment(@RequestBody Comment comment) {
        log.debug("创建评论, blogId={}, userId={}", comment.getBlogId(), comment.getUserId());
        if (comment.getBlogId() == null) {
            return Result.error(400, "博客ID不能为空");
        }
        if (comment.getUserId() == null) {
            return Result.error(400, "用户ID不能为空");
        }
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            return Result.error(400, "评论内容不能为空");
        }
        if (comment.getContent().length() > 2000) {
            return Result.error(400, "评论内容不能超过2000个字符");
        }
        if (comment.getNickname() == null && comment.getUserId() != null) {
            userRepository.findById(comment.getUserId()).ifPresent(user -> {
                comment.setNickname(user.getUsername());
                comment.setAvatar(user.getAvatar());
            });
        }
        Comment saved = commentRepository.save(comment);
        Map<String, Object> result = new HashMap<>();
        result.put("id", saved.getId());
        result.put("blogId", saved.getBlogId());
        result.put("userId", saved.getUserId());
        result.put("parentId", saved.getParentId());
        result.put("content", saved.getContent());
        result.put("nickname", saved.getNickname());
        result.put("avatar", saved.getAvatar());
        result.put("createTime", saved.getCreateTime());
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Object> deleteComment(@PathVariable Long id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
            return Result.success(null);
        }
        return Result.error(404, "评论不存在");
    }
}
