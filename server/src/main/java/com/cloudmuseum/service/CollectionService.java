package com.cloudmuseum.service;

import com.cloudmuseum.entity.Category;
import com.cloudmuseum.entity.Collection;
import com.cloudmuseum.repository.CategoryRepository;
import com.cloudmuseum.repository.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CollectionService {

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * 分页查询藏品（用户端）
     */
    public Map<String, Object> getCollections(int page, int size, Long categoryId, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<Collection> collectionPage;
        if (keyword != null && !keyword.isEmpty()) {
            collectionPage = collectionRepository.searchByKeyword(keyword, pageable);
        } else if (categoryId != null) {
            collectionPage = collectionRepository.findByCategoryIdAndIsDeleted(categoryId, 0, pageable);
        } else {
            collectionPage = collectionRepository.findByIsDeleted(0, pageable);
        }

        // 填充分类名称
        collectionPage.getContent().forEach(this::fillCategoryName);

        Map<String, Object> result = new HashMap<>();
        result.put("list", collectionPage.getContent());
        result.put("total", collectionPage.getTotalElements());
        result.put("pages", collectionPage.getTotalPages());
        result.put("current", page);
        return result;
    }

    /**
     * 获取推荐藏品
     */
    public List<Collection> getFeaturedCollections() {
        List<Collection> list = collectionRepository.findByIsFeaturedAndIsDeleted(1, 0);
        list.forEach(this::fillCategoryName);
        return list;
    }

    /**
     * 获取藏品详情
     */
    public Collection getCollectionDetail(Long id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("藏品不存在"));
        // 增加浏览次数
        collection.setViewCount(collection.getViewCount() == null ? 1 : collection.getViewCount() + 1);
        collectionRepository.save(collection);
        fillCategoryName(collection);
        return collection;
    }

    // ========== 管理端方法 ==========

    /**
     * 管理端获取所有藏品列表
     */
    public List<Collection> getAllCollections() {
        List<Collection> list = collectionRepository.findByIsDeletedOrderByCreateTimeDesc(0);
        list.forEach(this::fillCategoryName);
        return list;
    }

    /**
     * 管理端分页查询
     */
    public Page<Collection> getCollectionsPage(int page, int size, Long categoryId, String status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<Collection> collectionPage;
        if (categoryId != null) {
            collectionPage = collectionRepository.findByCategoryIdAndIsDeleted(categoryId, 0, pageable);
        } else if (status != null && !status.isEmpty()) {
            collectionPage = collectionRepository.findByStatusAndIsDeleted(status, 0, pageable);
        } else {
            collectionPage = collectionRepository.findByIsDeleted(0, pageable);
        }

        collectionPage.getContent().forEach(this::fillCategoryName);
        return collectionPage;
    }

    public Collection createCollection(Collection collection) {
        collection.setViewCount(0);
        collection.setIsDeleted(0);
        collection.setCreateTime(LocalDateTime.now());
        collection.setUpdateTime(LocalDateTime.now());
        return collectionRepository.save(collection);
    }

    public Collection updateCollection(Collection collection) {
        Collection existing = collectionRepository.findById(collection.getId())
                .orElseThrow(() -> new RuntimeException("藏品不存在"));

        if (collection.getName() != null) existing.setName(collection.getName());
        if (collection.getCategoryId() != null) existing.setCategoryId(collection.getCategoryId());
        if (collection.getDynasty() != null) existing.setDynasty(collection.getDynasty());
        if (collection.getEra() != null) existing.setEra(collection.getEra());
        if (collection.getMaterial() != null) existing.setMaterial(collection.getMaterial());
        if (collection.getSizeDesc() != null) existing.setSizeDesc(collection.getSizeDesc());
        if (collection.getOriginPlace() != null) existing.setOriginPlace(collection.getOriginPlace());
        if (collection.getDescription() != null) existing.setDescription(collection.getDescription());
        if (collection.getCulturalSignificance() != null) existing.setCulturalSignificance(collection.getCulturalSignificance());
        if (collection.getCoverImage() != null) existing.setCoverImage(collection.getCoverImage());
        if (collection.getImages() != null) existing.setImages(collection.getImages());
        if (collection.getVideoUrl() != null) existing.setVideoUrl(collection.getVideoUrl());
        if (collection.getModelUrl() != null) existing.setModelUrl(collection.getModelUrl());
        if (collection.getStatus() != null) existing.setStatus(collection.getStatus());
        if (collection.getIsFeatured() != null) existing.setIsFeatured(collection.getIsFeatured());
        existing.setUpdateTime(LocalDateTime.now());

        return collectionRepository.save(existing);
    }

    public void deleteCollection(Long id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("藏品不存在"));
        collection.setIsDeleted(1);
        collection.setUpdateTime(LocalDateTime.now());
        collectionRepository.save(collection);
    }

    private void fillCategoryName(Collection collection) {
        if (collection.getCategoryId() != null) {
            categoryRepository.findById(collection.getCategoryId())
                    .ifPresent(category -> collection.setCategoryName(category.getName()));
        }
    }
}
