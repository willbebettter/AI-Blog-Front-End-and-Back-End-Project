package com.ai.backend.mapper;

import com.ai.backend.entity.BlogView;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BlogViewRepository extends JpaRepository<BlogView, Long> {
    long countByBlogIdAndUserId(Long blogId, Long userId);
    List<BlogView> findByBlogIdAndUserIdOrderByCreateTimeDesc(Long blogId, Long userId);
}
