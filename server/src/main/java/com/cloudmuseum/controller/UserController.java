package com.cloudmuseum.controller;

import com.cloudmuseum.dto.ApiResult;
import com.cloudmuseum.dto.LoginRequest;
import com.cloudmuseum.dto.LoginResponse;
import com.cloudmuseum.service.SmsService;
import com.cloudmuseum.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SmsService smsService;

    /**
     * 发送验证码
     */
    @PostMapping("/send-code")
    public ApiResult<String> sendCode(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            return ApiResult.error("手机号格式不正确");
        }
        try {
            String code = smsService.sendCode(phone);
            // 开发环境返回验证码
            return ApiResult.success(code);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 手机号验证码登录
     */
    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return ApiResult.success(response);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public ApiResult<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ApiResult.success(userService.getUserInfo(userId));
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    public ApiResult<?> updateUserInfo(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getAttribute("userId");
        String nickname = (String) params.get("nickname");
        String avatar = (String) params.get("avatar");
        Integer gender = (Integer) params.get("gender");
        return ApiResult.success(userService.updateUser(userId, nickname, avatar, gender));
    }
}
