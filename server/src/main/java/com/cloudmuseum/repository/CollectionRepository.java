package com.cloudmuseum.repository;

import com.cloudmuseum.entity.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

    // 按分类查询未删除的藏品
    Page<Collection> findByCategoryIdAndIsDeleted(Long categoryId, Integer isDeleted, Pageable pageable);

    // 查询未删除的藏品
    Page<Collection> findByIsDeleted(Integer isDeleted, Pageable pageable);

    // 查询推荐藏品
    List<Collection> findByIsFeaturedAndIsDeleted(Integer isFeatured, Integer isDeleted);

    // 按状态查询
    Page<Collection> findByStatusAndIsDeleted(String status, Integer isDeleted, Pageable pageable);

    // 搜索藏品（按名称或描述）
    @Query("SELECT c FROM Collection c WHERE c.isDeleted = 0 AND (c.name LIKE %:keyword% OR c.description LIKE %:keyword%)")
    Page<Collection> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 在指定分类下搜索藏品
    @Query("SELECT c FROM Collection c WHERE c.isDeleted = 0 AND c.categoryId = :categoryId AND (c.name LIKE %:keyword% OR c.description LIKE %:keyword%)")
    Page<Collection> searchByKeywordAndCategory(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);

    // 获取所有未删除藏品（不分页，用于管理端列表）
    List<Collection> findByIsDeletedOrderByCreateTimeDesc(Integer isDeleted);
}
