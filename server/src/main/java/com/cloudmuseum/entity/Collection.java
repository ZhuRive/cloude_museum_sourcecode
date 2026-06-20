package com.cloudmuseum.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "collection")
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(length = 100)
    private String dynasty;

    @Column(length = 100)
    private String era;

    @Column(length = 100)
    private String material;

    @Column(name = "size_desc", length = 500)
    private String sizeDesc;

    @Column(name = "origin_place", length = 200)
    private String originPlace;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cultural_significance", columnDefinition = "TEXT")
    private String culturalSignificance;

    @Column(name = "cover_image", length = 500)
    private String coverImage;

    @Column(columnDefinition = "TEXT")
    private String images;

    @Column(name = "model_url", length = 500)
    private String modelUrl;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(length = 20)
    private String status = "on_display";

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "is_featured")
    private Integer isFeatured = 0;

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "is_deleted")
    private Integer isDeleted = 0;

    @Transient
    private String categoryName;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
