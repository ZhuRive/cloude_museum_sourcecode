package com.cloudmuseum.controller;

import com.cloudmuseum.dto.ApiResult;
import com.cloudmuseum.entity.Category;
import com.cloudmuseum.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类（公开接口）
     */
    @GetMapping("/list")
    public ApiResult<List<Category>> getAll() {
        return ApiResult.success(categoryService.getAllCategories());
    }

    /**
     * 新增分类
     */
    @PostMapping("/admin")
    public ApiResult<Category> create(@RequestBody Category category) {
        try {
            return ApiResult.success(categoryService.createCategory(category));
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 修改分类
     */
    @PutMapping("/admin")
    public ApiResult<Category> update(@RequestBody Category category) {
        try {
            return ApiResult.success(categoryService.updateCategory(category));
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/admin/{id}")
    public ApiResult<?> delete(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ApiResult.success();
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }
}
