package com.cloudmuseum.controller;

import com.cloudmuseum.dto.ApiResult;
import com.cloudmuseum.entity.Collection;
import com.cloudmuseum.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/collection")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    // ==================== 用户端接口 ====================

    /**
     * 分页查询藏品
     */
    @GetMapping("/list")
    public ApiResult<Map<String, Object>> getCollections(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        return ApiResult.success(collectionService.getCollections(page, size, categoryId, keyword));
    }

    /**
     * 获取推荐藏品
     */
    @GetMapping("/featured")
    public ApiResult<List<Collection>> getFeatured() {
        return ApiResult.success(collectionService.getFeaturedCollections());
    }

    /**
     * 获取藏品详情
     */
    @GetMapping("/detail/{id}")
    public ApiResult<Collection> getDetail(@PathVariable Long id) {
        try {
            return ApiResult.success(collectionService.getCollectionDetail(id));
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    // ==================== 管理端接口 ====================

    /**
     * 管理端获取所有藏品列表
     */
    @GetMapping("/admin/list")
    public ApiResult<List<Collection>> adminGetAll() {
        return ApiResult.success(collectionService.getAllCollections());
    }

    /**
     * 管理端分页查询
     */
    @GetMapping("/admin/page")
    public ApiResult<Page<Collection>> adminGetPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        return ApiResult.success(collectionService.getCollectionsPage(page, size, categoryId, status));
    }

    /**
     * 新增藏品
     */
    @PostMapping("/admin")
    public ApiResult<Collection> create(@RequestBody Collection collection) {
        try {
            return ApiResult.success(collectionService.createCollection(collection));
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 修改藏品
     */
    @PutMapping("/admin")
    public ApiResult<Collection> update(@RequestBody Collection collection) {
        try {
            return ApiResult.success(collectionService.updateCollection(collection));
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 删除藏品
     */
    @DeleteMapping("/admin/{id}")
    public ApiResult<?> delete(@PathVariable Long id) {
        try {
            collectionService.deleteCollection(id);
            return ApiResult.success();
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }
}
