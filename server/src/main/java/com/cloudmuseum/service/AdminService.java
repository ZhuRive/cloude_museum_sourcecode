package com.cloudmuseum.service;

import com.cloudmuseum.dto.AdminLoginRequest;
import com.cloudmuseum.dto.LoginResponse;
import com.cloudmuseum.entity.Admin;
import com.cloudmuseum.repository.AdminRepository;
import com.cloudmuseum.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 管理员登录
     */
    public LoginResponse login(AdminLoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (admin.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        // 密码校验 (MD5加密比对，生产环境建议BCrypt)
        String encryptedPassword = DigestUtils.md5DigestAsHex(request.getPassword().getBytes());
        // 实际使用BCrypt: BCryptPasswordEncoder().matches(request.getPassword(), admin.getPassword())
        if (!admin.getPassword().equals(encryptedPassword)) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = jwtUtil.generateAdminToken(admin.getId(), admin.getUsername(), admin.getRole());

        Map<String, Object> adminInfo = new HashMap<>();
        adminInfo.put("id", admin.getId());
        adminInfo.put("username", admin.getUsername());
        adminInfo.put("realName", admin.getRealName());
        adminInfo.put("role", admin.getRole());

        return new LoginResponse(token, adminInfo);
    }

    /**
     * 获取管理员信息
     */
    public Map<String, Object> getAdminInfo(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));

        Map<String, Object> info = new HashMap<>();
        info.put("id", admin.getId());
        info.put("username", admin.getUsername());
        info.put("realName", admin.getRealName());
        info.put("phone", admin.getPhone());
        info.put("role", admin.getRole());
        return info;
    }
}
