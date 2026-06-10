package com.cloudmuseum.service;

import com.cloudmuseum.dto.LoginRequest;
import com.cloudmuseum.dto.LoginResponse;
import com.cloudmuseum.entity.User;
import com.cloudmuseum.repository.UserRepository;
import com.cloudmuseum.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SmsService smsService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 手机号验证码登录
     */
    public LoginResponse login(LoginRequest request) {
        // 验证验证码
        boolean verified = smsService.verifyCode(request.getPhone(), request.getCode());
        if (!verified) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 查找或创建用户
        User user = userRepository.findByPhoneAndIsDeleted(request.getPhone(), 0)
                .orElseGet(() -> registerUser(request.getPhone()));

        // 生成 Token
        String token = jwtUtil.generateUserToken(user.getId(), user.getPhone());

        // 构建用户信息
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("phone", user.getPhone());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("avatar", user.getAvatar());

        return new LoginResponse(token, userInfo);
    }

    /**
     * 获取用户信息
     */
    public Map<String, Object> getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("phone", user.getPhone());
        info.put("nickname", user.getNickname());
        info.put("avatar", user.getAvatar());
        info.put("gender", user.getGender());
        info.put("createTime", user.getCreateTime());
        return info;
    }

    /**
     * 更新用户信息
     */
    public User updateUser(Long userId, String nickname, String avatar, Integer gender) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (nickname != null) user.setNickname(nickname);
        if (avatar != null) user.setAvatar(avatar);
        if (gender != null) user.setGender(gender);
        user.setUpdateTime(LocalDateTime.now());

        return userRepository.save(user);
    }

    private User registerUser(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickname("用户" + phone.substring(phone.length() - 4));
        user.setGender(0);
        user.setIsDeleted(0);
        return userRepository.save(user);
    }
}
