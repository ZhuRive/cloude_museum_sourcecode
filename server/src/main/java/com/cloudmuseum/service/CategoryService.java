package com.cloudmuseum.service;

import com.cloudmuseum.entity.Category;
import com.cloudmuseum.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findByIsDeletedOrderBySortOrder(0);
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
    }

    public Category createCategory(Category category) {
        category.setIsDeleted(0);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        return categoryRepository.save(category);
    }

    public Category updateCategory(Category category) {
        Category existing = getCategoryById(category.getId());
        if (category.getName() != null) existing.setName(category.getName());
        if (category.getDescription() != null) existing.setDescription(category.getDescription());
        if (category.getIcon() != null) existing.setIcon(category.getIcon());
        if (category.getSortOrder() != null) existing.setSortOrder(category.getSortOrder());
        existing.setUpdateTime(LocalDateTime.now());
        return categoryRepository.save(existing);
    }

    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        category.setIsDeleted(1);
        category.setUpdateTime(LocalDateTime.now());
        categoryRepository.save(category);
    }
}
