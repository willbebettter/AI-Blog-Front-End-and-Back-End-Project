package com.ai.backend.mapper;

import com.ai.backend.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByUserId(Long userId);
    List<Follow> findByFollowingId(Long followingId);
    Optional<Follow> findByUserIdAndFollowingId(Long userId, Long followingId);
    boolean existsByUserIdAndFollowingId(Long userId, Long followingId);
    long countByUserId(Long userId);
    long countByFollowingId(Long followingId);
    
    @Transactional
    void deleteByUserIdAndFollowingId(Long userId, Long followingId);
}
