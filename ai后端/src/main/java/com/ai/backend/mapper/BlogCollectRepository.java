package com.ai.backend.mapper;

import com.ai.backend.entity.BlogCollect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface BlogCollectRepository extends JpaRepository<BlogCollect, Long> {
    Optional<BlogCollect> findByUserIdAndBlogId(Long userId, Long blogId);
    boolean existsByUserIdAndBlogId(Long userId, Long blogId);
    List<BlogCollect> findByUserId(Long userId);
    
    @Transactional
    void deleteByUserIdAndBlogId(Long userId, Long blogId);
    
    long countByBlogId(Long blogId);
}
