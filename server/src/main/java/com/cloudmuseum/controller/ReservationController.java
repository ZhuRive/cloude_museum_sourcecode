package com.cloudmuseum.controller;

import com.cloudmuseum.dto.ApiResult;
import com.cloudmuseum.dto.ReservationRequest;
import com.cloudmuseum.entity.Reservation;
import com.cloudmuseum.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    // ==================== 用户端接口 ====================

    /**
     * 创建预约
     */
    @PostMapping("/create")
    public ApiResult<Reservation> create(HttpServletRequest request,
                                         @Valid @RequestBody ReservationRequest reservationRequest) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            return ApiResult.success(reservationService.createReservation(userId, reservationRequest));
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 用户取消预约
     */
    @PostMapping("/cancel/{id}")
    public ApiResult<?> cancel(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            reservationService.cancelReservation(userId, id);
            return ApiResult.success();
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 用户查询自己的预约列表
     */
    @GetMapping("/my")
    public ApiResult<List<Reservation>> myReservations(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ApiResult.success(reservationService.getUserReservations(userId));
    }

    /**
     * 用户查询预约详情
     */
    @GetMapping("/detail/{id}")
    public ApiResult<Reservation> detail(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            return ApiResult.success(reservationService.getReservationDetail(userId, id));
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    // ==================== 管理端接口 ====================

    /**
     * 管理端分页查询所有预约
     */
    @GetMapping("/admin/list")
    public ApiResult<Map<String, Object>> adminList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return ApiResult.success(reservationService.getAllReservations(page, size, status));
    }

    /**
     * 审核通过
     */
    @PostMapping("/admin/approve/{id}")
    public ApiResult<?> approve(@PathVariable Long id) {
        try {
            reservationService.approveReservation(id);
            return ApiResult.success();
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 审核拒绝
     */
    @PostMapping("/admin/reject/{id}")
    public ApiResult<?> reject(@PathVariable Long id, @RequestBody Map<String, String> params) {
        try {
            String reason = params.getOrDefault("reason", "暂不满足参观条件");
            reservationService.rejectReservation(id, reason);
            return ApiResult.success();
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 标记为已完成
     */
    @PostMapping("/admin/complete/{id}")
    public ApiResult<?> complete(@PathVariable Long id) {
        try {
            reservationService.completeReservation(id);
            return ApiResult.success();
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 获取统计数据
     */
    @GetMapping("/admin/stats")
    public ApiResult<Map<String, Object>> stats() {
        return ApiResult.success(reservationService.getStats());
    }
}
