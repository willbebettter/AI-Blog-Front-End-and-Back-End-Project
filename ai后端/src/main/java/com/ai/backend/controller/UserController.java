package com.ai.backend.controller;

import com.ai.backend.common.Result;
import com.ai.backend.entity.Blog;
import com.ai.backend.entity.BlogCollect;
import com.ai.backend.entity.BlogLike;
import com.ai.backend.entity.User;
import com.ai.backend.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private BlogLikeRepository blogLikeRepository;

    @Autowired
    private BlogCollectRepository blogCollectRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        log.debug("用户登录, username={}", username);

        return userRepository.findByUsername(username)
                .map(user -> {
                    if (passwordEncoder.matches(password, user.getPassword()) ||
                        user.getPassword().equals(password)) {
                        log.debug("用户登录成功, username={}", username);
                        return Result.success(buildUserData(user));
                    } else {
                        log.debug("用户登录失败-密码错误, username={}", username);
                        return Result.<Map<String, Object>>error(401, "密码错误");
                    }
                })
                .orElseGet(() -> {
                    log.debug("用户登录失败-用户不存�? username={}", username);
                    return Result.error(404, "用户不存在");
                });
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username");
        String password = registerRequest.get("password");
        String email = registerRequest.get("email");
        String nickname = registerRequest.get("nickname");
        log.debug("用户注册, username={}, email={}", username, email);

        if (userRepository.existsByUsername(username)) {
            log.debug("注册失败-用户名已存在, username={}", username);
            return Result.error(400, "用户名已存在");
        }

        if (email != null && !email.isEmpty() && userRepository.existsByEmail(email)) {
            log.debug("注册失败-邮箱已存�? email={}", email);
            return Result.error(400, "邮箱已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setNickname(username);
        user.setRole("USER");

        User savedUser = userRepository.save(user);
        log.debug("用户注册成功, id={}, username={}", savedUser.getId(), username);
        return Result.success("注册成功", buildUserData(savedUser));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getUserById(@PathVariable Long id) {
        log.debug("获取用户信息, id={}", id);
        return userRepository.findById(id)
                .map(user -> Result.success(buildUserData(user)))
                .orElse(Result.error(404, "用户不存在"));
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestParam Long userId) {
        log.debug("获取用户信息, userId={}", userId);
        return userRepository.findById(userId)
                .map(user -> Result.success(buildUserData(user)))
                .orElse(Result.error(404, "用户不存在"));
    }

    @PutMapping("/{id}")
    public Result<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody Map<String, String> updateRequest) {
        log.debug("更新用户信息, id={}", id);
        return userRepository.findById(id)
                .map(user -> {
                    if (updateRequest.containsKey("avatar")) {
                        user.setAvatar(updateRequest.get("avatar"));
                    }
                    if (updateRequest.containsKey("bio")) {
                        user.setBio(updateRequest.get("bio"));
                    }
                    User saved = userRepository.save(user);
                    log.debug("用户信息更新成功, id={}", id);
                    return Result.success(buildUserData(saved));
                })
                .orElse(Result.error(404, "用户不存在"));
    }

    @GetMapping("/{userId}/liked-blogs")
    public Result<List<Map<String, Object>>> getLikedBlogs(@PathVariable Long userId) {
        log.debug("获取点赞博客, userId={}", userId);
        List<BlogLike> likes = blogLikeRepository.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (BlogLike like : likes) {
            blogMapper.findById(like.getBlogId()).ifPresent(blog -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", blog.getId());
                map.put("title", blog.getTitle());
                map.put("summary", blog.getSummary());
                map.put("coverImage", blog.getCoverImage());
                map.put("views", blog.getViews());
                map.put("likes", blog.getLikes());
                map.put("tags", blog.getTags());
                map.put("category", blog.getCategory());
                map.put("createTime", blog.getCreateTime());
                if (blog.getAuthorId() != null) {
                    userRepository.findById(blog.getAuthorId()).ifPresent(author -> {
                        Map<String, Object> authorMap = new HashMap<>();
                        authorMap.put("id", author.getId());
                        authorMap.put("username", author.getUsername());
                        authorMap.put("avatar", author.getAvatar());
                        map.put("author", authorMap);
                    });
                }
                result.add(map);
            });
        }
        return Result.success(result);
    }

    @GetMapping("/{userId}/collected-blogs")
    public Result<List<Map<String, Object>>> getCollectedBlogs(@PathVariable Long userId) {
        log.debug("获取收藏博客, userId={}", userId);
        List<BlogCollect> collects = blogCollectRepository.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (BlogCollect collect : collects) {
            blogMapper.findById(collect.getBlogId()).ifPresent(blog -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", blog.getId());
                map.put("title", blog.getTitle());
                map.put("summary", blog.getSummary());
                map.put("coverImage", blog.getCoverImage());
                map.put("views", blog.getViews());
                map.put("likes", blog.getLikes());
                map.put("tags", blog.getTags());
                map.put("category", blog.getCategory());
                map.put("createTime", blog.getCreateTime());
                if (blog.getAuthorId() != null) {
                    userRepository.findById(blog.getAuthorId()).ifPresent(author -> {
                        Map<String, Object> authorMap = new HashMap<>();
                        authorMap.put("id", author.getId());
                        authorMap.put("username", author.getUsername());
                        authorMap.put("avatar", author.getAvatar());
                        map.put("author", authorMap);
                    });
                }
                result.add(map);
            });
        }
        return Result.success(result);
    }

    @GetMapping("/followed")
    public Result<List<Map<String, Object>>> getFollowedBlogs(@RequestParam Long userId) {
        log.debug("获取关注博客, userId={}", userId);
        List<Map<String, Object>> result = new ArrayList<>();
        followRepository.findByUserId(userId).forEach(follow -> {
            blogMapper.findByAuthorIdAndStatusOrderByCreateTimeDesc(
                    follow.getFollowingId(), Blog.Status.PUBLISHED).forEach(blog -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", blog.getId());
                map.put("title", blog.getTitle());
                map.put("summary", blog.getSummary());
                map.put("coverImage", blog.getCoverImage());
                map.put("views", blog.getViews());
                map.put("likes", blog.getLikes());
                map.put("tags", blog.getTags());
                map.put("category", blog.getCategory());
                map.put("createTime", blog.getCreateTime());
                userRepository.findById(follow.getFollowingId()).ifPresent(author -> {
                    Map<String, Object> authorMap = new HashMap<>();
                    authorMap.put("id", author.getId());
                    authorMap.put("username", author.getUsername());
                    authorMap.put("avatar", author.getAvatar());
                    map.put("author", authorMap);
                });
                result.add(map);
            });
        });
        return Result.success(result);
    }

    @GetMapping("/{userId}/blogs")
    public Result<List<Map<String, Object>>> getUserBlogs(@PathVariable Long userId) {
        log.debug("获取用户博客, userId={}", userId);
        List<Blog> blogs = blogMapper.findByAuthorIdOrderByCreateTimeDesc(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Blog blog : blogs) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", blog.getId());
            map.put("title", blog.getTitle());
            map.put("summary", blog.getSummary());
            map.put("coverImage", blog.getCoverImage());
            map.put("views", blog.getViews());
            map.put("likes", blog.getLikes());
            map.put("tags", blog.getTags());
            map.put("category", blog.getCategory());
            map.put("status", blog.getStatus());
            map.put("createTime", blog.getCreateTime());
            map.put("updateTime", blog.getUpdateTime());
            result.add(map);
        }
        return Result.success(result);
    }

    private Map<String, Object> buildUserData(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("username", user.getUsername());
        userData.put("nickname", user.getNickname());
        userData.put("email", user.getEmail());
        userData.put("avatar", user.getAvatar());
        userData.put("bio", user.getBio());
        userData.put("role", user.getRole());
        userData.put("createTime", user.getCreateTime());
        userData.put("lastLoginTime", user.getLastLoginTime());
        userData.put("articleCount", blogMapper.countByAuthorId(user.getId()));
        userData.put("followingCount", followRepository.countByUserId(user.getId()));
        userData.put("followersCount", followRepository.countByFollowingId(user.getId()));
        return userData;
    }
}
