package com.cloudmuseum.controller;

import com.cloudmuseum.dto.ApiResult;
import com.cloudmuseum.repository.CollectionRepository;
import com.cloudmuseum.repository.ReservationRepository;
import com.cloudmuseum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Pageable;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
public class DashboardController {

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/stats")
    public ApiResult<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 藏品总数
        long collectionCount = collectionRepository.count();
        // 用户总数
        long userCount = userRepository.count();
        // 预约总数
        long reservationCount = reservationRepository.count();
        // 待审核预约数
        long pendingCount = reservationRepository.findByStatusOrderByCreateTimeDesc("pending", Pageable.unpaged()).getTotalElements();

        stats.put("collectionCount", collectionCount);
        stats.put("userCount", userCount);
        stats.put("reservationCount", reservationCount);
        stats.put("pendingCount", pendingCount);

        return ApiResult.success(stats);
    }
}
