package com.ai.backend.mapper;

import com.ai.backend.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUserIdOrderByCreateTimeDesc(Long userId);
}
