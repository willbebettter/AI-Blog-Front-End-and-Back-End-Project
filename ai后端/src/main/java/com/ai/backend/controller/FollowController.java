package com.ai.backend.controller;

import com.ai.backend.common.Result;
import com.ai.backend.entity.Blog;
import com.ai.backend.entity.Follow;
import com.ai.backend.entity.User;
import com.ai.backend.mapper.BlogMapper;
import com.ai.backend.mapper.FollowRepository;
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
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogMapper blogMapper;

    @PostMapping
    public Result<Object> follow(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long followingId = request.get("followingId");
        if (userId == null || followingId == null) {
            return Result.error(400, "参数不完整");
        }
        if (userId.equals(followingId)) {
            return Result.error(400, "不能关注自己");
        }
        if (followRepository.existsByUserIdAndFollowingId(userId, followingId)) {
            return Result.error(400, "已经关注了该用户");
        }
        Follow follow = new Follow();
        follow.setUserId(userId);
        follow.setFollowingId(followingId);
        followRepository.save(follow);
        return Result.success(null);
    }

    @PostMapping("/unfollow")
    public Result<Object> unfollow(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long followingId = request.get("followingId");
        if (userId == null || followingId == null) {
            return Result.error(400, "参数不完整");
        }
        followRepository.deleteByUserIdAndFollowingId(userId, followingId);
        return Result.success(null);
    }

    @GetMapping("/{userId}/following")
    public Result<List<Map<String, Object>>> getFollowing(@PathVariable Long userId) {
        List<Follow> follows = followRepository.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Follow follow : follows) {
            userRepository.findById(follow.getFollowingId()).ifPresent(user -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", user.getId());
                map.put("username", user.getUsername());
                map.put("nickname", user.getNickname());
                map.put("avatar", user.getAvatar());
                map.put("bio", user.getBio());
                map.put("articleCount", blogMapper.countByAuthorId(user.getId()));
                result.add(map);
            });
        }
        return Result.success(result);
    }

    @GetMapping("/{userId}/followers")
    public Result<List<Map<String, Object>>> getFollowers(@PathVariable Long userId) {
        List<Follow> follows = followRepository.findByFollowingId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Follow follow : follows) {
            userRepository.findById(follow.getUserId()).ifPresent(user -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", user.getId());
                map.put("username", user.getUsername());
                map.put("nickname", user.getNickname());
                map.put("avatar", user.getAvatar());
                map.put("bio", user.getBio());
                result.add(map);
            });
        }
        return Result.success(result);
    }

    @GetMapping("/{userId}/following-count")
    public Result<Long> getFollowingCount(@PathVariable Long userId) {
        return Result.success(followRepository.countByUserId(userId));
    }

    @GetMapping("/{userId}/followers-count")
    public Result<Long> getFollowersCount(@PathVariable Long userId) {
        return Result.success(followRepository.countByFollowingId(userId));
    }

    @GetMapping("/{userId}/is-following")
    public Result<Boolean> isFollowing(@PathVariable Long userId, @RequestParam Long targetId) {
        return Result.success(followRepository.existsByUserIdAndFollowingId(userId, targetId));
    }

    @GetMapping("/{userId}/followed-blogs")
    public Result<List<Map<String, Object>>> getFollowedBlogs(@PathVariable Long userId) {
        List<Follow> follows = followRepository.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Follow follow : follows) {
            List<Blog> blogs = blogMapper.findByAuthorIdAndStatusOrderByCreateTimeDesc(
                    follow.getFollowingId(), Blog.Status.PUBLISHED);
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
                map.put("createTime", blog.getCreateTime());
                userRepository.findById(follow.getFollowingId()).ifPresent(author -> {
                    Map<String, Object> authorMap = new HashMap<>();
                    authorMap.put("id", author.getId());
                    authorMap.put("nickname", author.getNickname());
                    authorMap.put("avatar", author.getAvatar());
                    map.put("author", authorMap);
                });
                result.add(map);
            }
        }
        result.sort((a, b) -> {
            if (a.get("createTime") == null || b.get("createTime") == null) return 0;
            return b.get("createTime").toString().compareTo(a.get("createTime").toString());
        });
        return Result.success(result);
    }
}
