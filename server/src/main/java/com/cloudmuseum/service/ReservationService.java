package com.cloudmuseum.service;

import com.cloudmuseum.dto.ReservationRequest;
import com.cloudmuseum.entity.Reservation;
import com.cloudmuseum.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    private static final int MAX_DAILY_MORNING = 100;
    private static final int MAX_DAILY_AFTERNOON = 100;

    /**
     * 用户创建预约
     */
    public Reservation createReservation(Long userId, ReservationRequest request) {
        // 检查预约容量
        int count = reservationRepository.countByVisitDateAndVisitTimeSlotAndStatusNot(
                request.getVisitDate(), request.getVisitTimeSlot(), "cancelled");

        int maxCapacity = "上午".equals(request.getVisitTimeSlot()) ? MAX_DAILY_MORNING : MAX_DAILY_AFTERNOON;
        if (count >= maxCapacity) {
            throw new RuntimeException("该时段预约已满，请选择其他时段");
        }

        // 检查是否已预约同一天
        List<Reservation> existing = reservationRepository.findByUserIdOrderByCreateTimeDesc(userId);
        boolean hasDuplicate = existing.stream()
                .anyMatch(r -> r.getVisitDate().equals(request.getVisitDate())
                        && !"cancelled".equals(r.getStatus()));
        if (hasDuplicate) {
            throw new RuntimeException("您已预约该日期，请勿重复预约");
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setVisitDate(request.getVisitDate());
        reservation.setVisitTimeSlot(request.getVisitTimeSlot());
        reservation.setVisitorName(request.getVisitorName());
        reservation.setVisitorPhone(request.getVisitorPhone());
        reservation.setVisitorCount(request.getVisitorCount());
        reservation.setIdCard(request.getIdCard());
        reservation.setNotes(request.getNotes());
        reservation.setStatus("pending");

        return reservationRepository.save(reservation);
    }

    /**
     * 用户取消预约
     */
    public void cancelReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new RuntimeException("预约记录不存在"));

        if (!"pending".equals(reservation.getStatus()) && !"approved".equals(reservation.getStatus())) {
            throw new RuntimeException("当前状态不允许取消");
        }

        reservation.setStatus("cancelled");
        reservationRepository.save(reservation);
    }

    /**
     * 用户查询自己的预约列表
     */
    public List<Reservation> getUserReservations(Long userId) {
        return reservationRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    public Map<String, Object> getUserReservationsPage(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Reservation> reservationPage = reservationRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("list", reservationPage.getContent());
        result.put("total", reservationPage.getTotalElements());
        result.put("pages", reservationPage.getTotalPages());
        result.put("current", page);
        return result;
    }

    /**
     * 获取用户的预约详情
     */
    public Reservation getReservationDetail(Long userId, Long reservationId) {
        return reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new RuntimeException("预约记录不存在"));
    }

    // ========== 管理端方法 ==========

    public Map<String, Object> getAllReservations(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<Reservation> reservationPage;
        if (status != null && !status.isEmpty() && !"all".equals(status)) {
            reservationPage = reservationRepository.findByStatusOrderByCreateTimeDesc(status, pageable);
        } else {
            reservationPage = reservationRepository.findAllByOrderByCreateTimeDesc(pageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", reservationPage.getContent());
        result.put("total", reservationPage.getTotalElements());
        result.put("pages", reservationPage.getTotalPages());
        result.put("current", page);
        return result;
    }

    public void approveReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预约记录不存在"));
        reservation.setStatus("approved");
        reservationRepository.save(reservation);
    }

    public void rejectReservation(Long id, String reason) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预约记录不存在"));
        reservation.setStatus("rejected");
        reservation.setRejectReason(reason);
        reservationRepository.save(reservation);
    }

    public void completeReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预约记录不存在"));
        reservation.setStatus("completed");
        reservationRepository.save(reservation);
    }

    /**
     * 获取统计数据
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 总藏品数
        List<Reservation> all = reservationRepository.findAll();
        long total = all.size();
        long pending = all.stream().filter(r -> "pending".equals(r.getStatus())).count();
        long approved = all.stream().filter(r -> "approved".equals(r.getStatus())).count();
        long completed = all.stream().filter(r -> "completed".equals(r.getStatus())).count();
        long cancelled = all.stream().filter(r -> "cancelled".equals(r.getStatus())).count();

        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("approved", approved);
        stats.put("completed", completed);
        stats.put("cancelled", cancelled);

        return stats;
    }
}
