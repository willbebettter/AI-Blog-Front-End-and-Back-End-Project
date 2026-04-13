package com.ai.backend.controller;

import com.ai.backend.common.Result;
import com.ai.backend.entity.Category;
import com.ai.backend.mapper.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public Result<List<Category>> getAllCategories() {
        return Result.success(categoryRepository.findAllByOrderBySortAsc());
    }

    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(Result::success)
                .orElse(Result.error(404, "分类不存在"));
    }

    @PostMapping
    public Result<Category> createCategory(@RequestBody Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            return Result.error(400, "分类名称已存在");
        }
        return Result.success(categoryRepository.save(category));
    }

    @PutMapping("/{id}")
    public Result<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return categoryRepository.findById(id)
                .map(existing -> {
                    category.setId(id);
                    return Result.success(categoryRepository.save(category));
                })
                .orElse(Result.error(404, "分类不存在"));
    }

    @DeleteMapping("/{id}")
    public Result<Object> deleteCategory(@PathVariable Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return Result.success(null);
        }
        return Result.error(404, "分类不存在");
    }
}
