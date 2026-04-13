package com.ai.backend.mapper;

import com.ai.backend.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BlogMapper extends JpaRepository<Blog, Long> {

    Page<Blog> findByStatus(Blog.Status status, Pageable pageable);

    Page<Blog> findByAuthorIdAndStatus(Long authorId, Blog.Status status, Pageable pageable);

    Page<Blog> findByCategoryAndStatus(String category, Blog.Status status, Pageable pageable);

    Page<Blog> findByStatusOrderByCreateTimeDesc(Blog.Status status, Pageable pageable);

    Page<Blog> findByAuthorIdAndStatusOrderByCreateTimeDesc(Long authorId, Blog.Status status, Pageable pageable);

    Page<Blog> findByCategoryAndStatusOrderByCreateTimeDesc(String category, Blog.Status status, Pageable pageable);

    @Query("SELECT b FROM Blog b WHERE b.status = 'PUBLISHED' AND b.title LIKE CONCAT('%', :keyword, '%')")
    Page<Blog> findByKeywordPaged(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Blog b WHERE b.status = 'PUBLISHED' AND b.tags LIKE CONCAT('%', :tag, '%')")
    Page<Blog> findByTagPaged(@Param("tag") String tag, Pageable pageable);

    @Query("SELECT b FROM Blog b WHERE b.status = 'PUBLISHED' ORDER BY b.createTime DESC")
    List<Blog> findPublishedAll();

    @Query("SELECT b FROM Blog b WHERE b.title LIKE CONCAT('%', :keyword, '%') ORDER BY b.createTime DESC")
    List<Blog> findByKeyword(@Param("keyword") String keyword);

    List<Blog> findByAuthorIdAndStatusOrderByCreateTimeDesc(Long authorId, Blog.Status status);

    List<Blog> findByAuthorIdOrderByCreateTimeDesc(Long authorId);

    List<Blog> findByStatusOrderByCreateTimeDesc(Blog.Status status);

    List<Blog> findByCategoryAndStatusOrderByCreateTimeDesc(String category, Blog.Status status);

    @Query("SELECT b FROM Blog b WHERE b.status = 'PUBLISHED' AND b.category = :category ORDER BY b.createTime DESC")
    List<Blog> findPublishedByCategory(@Param("category") String category);

    long countByAuthorId(Long authorId);

    long countByStatus(Blog.Status status);
}
