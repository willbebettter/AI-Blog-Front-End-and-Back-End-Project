package com.ai.backend.mapper;

import com.ai.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderBySortAsc();
    boolean existsByName(String name);
}
