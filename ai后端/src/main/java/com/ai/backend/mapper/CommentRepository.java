package com.ai.backend.mapper;

import com.ai.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    @Query("SELECT c FROM Comment c WHERE c.blogId = :blogId ORDER BY c.createTime DESC")
    List<Comment> findByBlogId(@Param("blogId") Long blogId);
    
    @Query("SELECT c FROM Comment c WHERE c.userId = :userId ORDER BY c.createTime DESC")
    List<Comment> findByUserId(@Param("userId") Long userId);
}
