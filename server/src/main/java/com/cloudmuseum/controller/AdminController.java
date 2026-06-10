package com.cloudmuseum.controller;

import com.cloudmuseum.dto.AdminLoginRequest;
import com.cloudmuseum.dto.ApiResult;
import com.cloudmuseum.dto.LoginResponse;
import com.cloudmuseum.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        try {
            LoginResponse response = adminService.login(request);
            return ApiResult.success(response);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 获取管理员信息
     */
    @GetMapping("/info")
    public ApiResult<Map<String, Object>> getAdminInfo(HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("adminId");
        return ApiResult.success(adminService.getAdminInfo(adminId));
    }
}
