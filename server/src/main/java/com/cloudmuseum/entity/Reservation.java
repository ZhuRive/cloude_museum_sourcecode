package com.cloudmuseum.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "visitor_name", nullable = false, length = 50)
    private String visitorName;

    @Column(name = "visitor_phone", nullable = false, length = 20)
    private String visitorPhone;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "visit_time_slot", nullable = false, length = 50)
    private String visitTimeSlot;

    @Column(name = "visitor_count")
    private Integer visitorCount = 1;

    @Column(name = "id_card", length = 30)
    private String idCard;

    @Column(length = 500)
    private String notes;

    @Column(length = 20)
    private String status = "pending";

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

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
