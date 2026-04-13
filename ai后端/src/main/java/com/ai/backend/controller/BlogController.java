package com.ai.backend.controller;

import com.ai.backend.common.Result;
import com.ai.backend.entity.Blog;
import com.ai.backend.entity.BlogCollect;
import com.ai.backend.entity.BlogLike;
import com.ai.backend.entity.BlogView;
import com.ai.backend.entity.User;
import com.ai.backend.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private BlogViewRepository blogViewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogLikeRepository blogLikeRepository;

    @Autowired
    private BlogCollectRepository blogCollectRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping
    public Result<Map<String, Object>> getAllBlogs(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long authorId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "DESC") String order) {
        log.debug("获取博客列表(分页), category={}, authorId={}, page={}, size={}, sortBy={}, order={}", category, authorId, page, size, sortBy, order);

        if (size > 50) size = 50;
        if (size < 1) size = 10;
        if (page < 1) page = 1;

        Sort.Direction direction = "ASC".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = "views".equals(sortBy) ? "views" : "likes".equals(sortBy) ? "likes" : "createTime";
        Sort sort = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, sort);

        Page<Blog> blogPage;
        if (authorId != null) {
            blogPage = blogMapper.findByAuthorIdAndStatus(authorId, Blog.Status.PUBLISHED, pageable);
        } else if (category != null && !category.isEmpty()) {
            blogPage = blogMapper.findByCategoryAndStatus(category, Blog.Status.PUBLISHED, pageable);
        } else {
            blogPage = blogMapper.findByStatus(Blog.Status.PUBLISHED, pageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", buildBlogListWithAuthor(blogPage.getContent()));
        result.put("totalElements", blogPage.getTotalElements());
        result.put("totalPages", blogPage.getTotalPages());
        result.put("currentPage", page);
        result.put("pageSize", size);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getBlogById(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        log.debug("获取博客详情, id={}, userId={}", id, userId);
        return blogMapper.findById(id)
                .map(blog -> {
                    if (userId != null) {
                        long userViewCount = blogViewRepository.countByBlogIdAndUserId(id, userId);
                        if (userViewCount < 10) {
                            BlogView blogView = new BlogView();
                            blogView.setBlogId(id);
                            blogView.setUserId(userId);
                            blogViewRepository.save(blogView);
                            blog.setViews(blog.getViews() + 1);
                            blogMapper.save(blog);
                        }
                    } else {
                        blog.setViews(blog.getViews() + 1);
                        blogMapper.save(blog);
                    }
                    return Result.success(buildBlogDetailWithAuthor(blog));
                })
                .orElse(Result.error(404, "文章不存在"));
    }

    @GetMapping("/search")
    public Result<Map<String, Object>> searchBlogs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "DESC") String order) {
        log.debug("搜索博客(分页), keyword={}, page={}, size={}, sortBy={}, order={}", keyword, page, size, sortBy, order);
        Sort.Direction direction = "ASC".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = "views".equals(sortBy) ? "views" : "likes".equals(sortBy) ? "likes" : "createTime";
        Sort sort = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, sort);
        Page<Blog> blogPage = blogMapper.findByKeywordPaged(keyword, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", buildBlogListWithAuthor(blogPage.getContent()));
        result.put("totalElements", blogPage.getTotalElements());
        result.put("totalPages", blogPage.getTotalPages());
        result.put("currentPage", page);
        result.put("pageSize", size);
        return Result.success(result);
    }

    @PostMapping
    public Result<Map<String, Object>> createBlog(@RequestBody Blog blog) {
        log.debug("创建博客, title={}, authorId={}", blog.getTitle(), blog.getAuthorId());
        if (blog.getTitle() == null || blog.getTitle().trim().isEmpty()) {
            return Result.error(400, "标题不能为空");
        }
        if (blog.getTitle().length() > 200) {
            return Result.error(400, "标题不能超过200个字符");
        }
        if (blog.getContent() == null || blog.getContent().trim().isEmpty()) {
            return Result.error(400, "内容不能为空");
        }
        if (blog.getAuthorId() == null) {
            return Result.error(400, "作者ID不能为空");
        }
        if (blog.getTags() != null && blog.getTags().length() > 500) {
            return Result.error(400, "标签内容过长");
        }
        if (blog.getSummary() != null && blog.getSummary().length() > 500) {
            return Result.error(400, "摘要不能超过500个字符");
        }
        Blog saved = blogMapper.save(blog);
        log.debug("博客创建成功, id={}", saved.getId());
        return Result.success(buildBlogDetailWithAuthor(saved));
    }

    @PutMapping("/{id}")
    public Result<Map<String, Object>> updateBlog(@PathVariable Long id, @RequestBody Blog blog) {
        log.debug("更新博客, id={}", id);
        if (blog.getTitle() == null || blog.getTitle().trim().isEmpty()) {
            return Result.error(400, "标题不能为空");
        }
        if (blog.getTitle().length() > 200) {
            return Result.error(400, "标题不能超过200个字符");
        }
        if (blog.getContent() == null || blog.getContent().trim().isEmpty()) {
            return Result.error(400, "内容不能为空");
        }
        return blogMapper.findById(id)
                .map(existing -> {
                    blog.setId(id);
                    Blog saved = blogMapper.save(blog);
                    log.debug("博客更新成功, id={}", id);
                    return Result.success(buildBlogDetailWithAuthor(saved));
                })
                .orElse(Result.error(404, "文章不存在"));
    }

    @DeleteMapping("/{id}")
    public Result<Object> deleteBlog(@PathVariable Long id) {
        log.debug("删除博客, id={}", id);
        if (blogMapper.existsById(id)) {
            blogMapper.deleteById(id);
            log.debug("博客删除成功, id={}", id);
            return Result.success(null);
        }
        log.debug("博客不存�? id={}", id);
        return Result.error(404, "文章不存在");
    }

    @PostMapping("/{id}/like")
    public Result<Map<String, Object>> toggleLike(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        log.debug("切换点赞, blogId={}, userId={}", id, userId);
        return blogMapper.findById(id)
                .map(blog -> {
                    Map<String, Object> response = new HashMap<>();
                    if (userId != null && blogLikeRepository.existsByUserIdAndBlogId(userId, id)) {
                        blogLikeRepository.deleteByUserIdAndBlogId(userId, id);
                        blog.setLikes(Math.max(0, blog.getLikes() - 1));
                        blogMapper.save(blog);
                        response.put("liked", false);
                        log.debug("取消点赞成功, blogId={}, userId={}", id, userId);
                    } else {
                        if (userId != null) {
                            BlogLike like = new BlogLike();
                            like.setUserId(userId);
                            like.setBlogId(id);
                            blogLikeRepository.save(like);
                        }
                        blog.setLikes(blog.getLikes() + 1);
                        blogMapper.save(blog);
                        response.put("liked", true);
                        log.debug("点赞成功, blogId={}, userId={}", id, userId);
                    }
                    response.put("likes", blog.getLikes());
                    return Result.success(response);
                })
                .orElse(Result.error(404, "文章不存在"));
    }

    @PostMapping("/{id}/collect")
    public Result<Map<String, Object>> toggleCollect(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Object userIdObj = request.get("userId");
        Long userId = userIdObj != null ? Long.valueOf(userIdObj.toString()) : null;
        String action = (String) request.get("action");
        log.debug("切换收藏, blogId={}, userId={}, action={}", id, userId, action);
        return blogMapper.findById(id)
                .map(blog -> {
                    Map<String, Object> response = new HashMap<>();
                    if ("remove".equals(action) && userId != null) {
                        blogCollectRepository.deleteByUserIdAndBlogId(userId, id);
                        blog.setCollects(Math.max(0, blog.getCollects() - 1));
                        blogMapper.save(blog);
                        response.put("collected", false);
                        log.debug("取消收藏成功, blogId={}, userId={}", id, userId);
                    } else {
                        if (userId != null && !blogCollectRepository.existsByUserIdAndBlogId(userId, id)) {
                            BlogCollect collect = new BlogCollect();
                            collect.setUserId(userId);
                            collect.setBlogId(id);
                            blogCollectRepository.save(collect);
                            blog.setCollects(blog.getCollects() + 1);
                            blogMapper.save(blog);
                        }
                        response.put("collected", true);
                        log.debug("收藏成功, blogId={}, userId={}", id, userId);
                    }
                    response.put("collects", blog.getCollects());
                    return Result.success(response);
                })
                .orElse(Result.error(404, "文章不存在"));
    }

    @GetMapping("/popular")
    public Result<List<Map<String, Object>>> getPopularBlogs(@RequestParam(defaultValue = "10") int limit) {
        log.debug("获取热门博客, limit={}", limit);
        List<Blog> blogs = blogMapper.findPublishedAll().stream()
                .sorted((a, b) -> b.getViews().compareTo(a.getViews()))
                .limit(limit)
                .toList();
        return Result.success(buildBlogListWithAuthor(blogs));
    }

    @GetMapping("/tag/{tag}")
    public Result<Map<String, Object>> getBlogsByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "DESC") String order) {
        log.debug("按标签获取博客(分页), tag={}, page={}, size={}, sortBy={}, order={}", tag, page, size, sortBy, order);
        Sort.Direction direction = "ASC".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = "views".equals(sortBy) ? "views" : "likes".equals(sortBy) ? "likes" : "createTime";
        Sort sort = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, sort);
        Page<Blog> blogPage = blogMapper.findByTagPaged(tag, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", buildBlogListWithAuthor(blogPage.getContent()));
        result.put("totalElements", blogPage.getTotalElements());
        result.put("totalPages", blogPage.getTotalPages());
        result.put("currentPage", page);
        result.put("pageSize", size);
        return Result.success(result);
    }

    @GetMapping("/author/{authorId}")
    public Result<List<Map<String, Object>>> getBlogsByAuthor(@PathVariable Long authorId) {
        log.debug("按作者获取博�? authorId={}", authorId);
        List<Blog> blogs = blogMapper.findByAuthorIdAndStatusOrderByCreateTimeDesc(authorId, Blog.Status.PUBLISHED);
        return Result.success(buildBlogListWithAuthor(blogs));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        log.debug("获取统计数据");
        Map<String, Object> stats = new HashMap<>();
        stats.put("blogs", blogMapper.countByStatus(Blog.Status.PUBLISHED));
        stats.put("users", userRepository.count());
        stats.put("comments", commentRepository.count());
        return Result.success(stats);
    }

    @GetMapping("/{id}/is-liked")
    public Result<Boolean> isLiked(@PathVariable Long id, @RequestParam Long userId) {
        return Result.success(blogLikeRepository.existsByUserIdAndBlogId(userId, id));
    }

    @GetMapping("/{id}/is-collected")
    public Result<Boolean> isCollected(@PathVariable Long id, @RequestParam Long userId) {
        return Result.success(blogCollectRepository.existsByUserIdAndBlogId(userId, id));
    }

    private List<Map<String, Object>> buildBlogListWithAuthor(List<Blog> blogs) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Blog blog : blogs) {
            Map<String, Object> map = buildBlogMap(blog);
            if (blog.getAuthorId() != null) {
                userRepository.findById(blog.getAuthorId()).ifPresent(author -> {
                    map.put("author", buildAuthorMap(author));
                });
            }
            result.add(map);
        }
        return result;
    }

    private Map<String, Object> buildBlogDetailWithAuthor(Blog blog) {
        Map<String, Object> map = buildBlogMap(blog);
        if (blog.getAuthorId() != null) {
            userRepository.findById(blog.getAuthorId()).ifPresent(author -> {
                map.put("author", buildAuthorMap(author));
            });
        }
        return map;
    }

    private Map<String, Object> buildBlogMap(Blog blog) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", blog.getId());
        map.put("title", blog.getTitle());
        map.put("content", blog.getContent());
        map.put("coverImage", blog.getCoverImage());
        map.put("summary", blog.getSummary());
        map.put("category", blog.getCategory());
        map.put("tags", blog.getTags());
        map.put("authorId", blog.getAuthorId());
        map.put("views", blog.getViews());
        map.put("likes", blog.getLikes());
        map.put("collects", blog.getCollects());
        map.put("status", blog.getStatus());
        map.put("createTime", blog.getCreateTime());
        map.put("updateTime", blog.getUpdateTime());
        return map;
    }

    private Map<String, Object> buildAuthorMap(User author) {
        Map<String, Object> authorMap = new HashMap<>();
        authorMap.put("id", author.getId());
        authorMap.put("username", author.getUsername());
        authorMap.put("nickname", author.getUsername());
        authorMap.put("avatar", author.getAvatar());
        authorMap.put("bio", author.getBio());
        return authorMap;
    }
}
