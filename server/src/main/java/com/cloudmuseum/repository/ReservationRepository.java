package com.cloudmuseum.repository;

import com.cloudmuseum.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 按用户ID查询
    Page<Reservation> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    List<Reservation> findByUserIdOrderByCreateTimeDesc(Long userId);

    // 按状态查询（管理端）
    Page<Reservation> findByStatusOrderByCreateTimeDesc(String status, Pageable pageable);

    // 查询所有预约（管理端）
    Page<Reservation> findAllByOrderByCreateTimeDesc(Pageable pageable);

    // 按日期范围查询
    List<Reservation> findByVisitDateBetween(LocalDate start, LocalDate end);

    // 按日期和时段查询数量
    int countByVisitDateAndVisitTimeSlotAndStatusNot(LocalDate visitDate, String visitTimeSlot, String status);

    Optional<Reservation> findByIdAndUserId(Long id, Long userId);
}
