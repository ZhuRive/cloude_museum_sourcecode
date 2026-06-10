package com.cloudmuseum.service;

import com.cloudmuseum.entity.SmsCode;
import com.cloudmuseum.repository.SmsCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class SmsService {

    @Autowired
    private SmsCodeRepository smsCodeRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final long SMS_COOLDOWN_SECONDS = 60; // 发送冷却时间
    private static final long SMS_EXPIRE_MINUTES = 5;     // 验证码有效期

    /**
     * 发送验证码（开发阶段固定为 123456）
     */
    public String sendCode(String phone) {
        // 检查发送频率（Redis）
        String cooldownKey = "sms:cooldown:" + phone;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            long ttl = redisTemplate.getExpire(cooldownKey, TimeUnit.SECONDS);
            throw new RuntimeException("发送过于频繁，请 " + ttl + " 秒后再试");
        }

        // 生成6位随机验证码
        String code = String.format("%06d", new Random().nextInt(1000000));

        // 开发阶段固定验证码
        code = "123456";

        // 存入数据库
        SmsCode smsCode = new SmsCode();
        smsCode.setPhone(phone);
        smsCode.setCode(code);
        smsCode.setType("login");
        smsCode.setExpireTime(LocalDateTime.now().plusMinutes(SMS_EXPIRE_MINUTES));
        smsCode.setUsed(0);
        smsCodeRepository.save(smsCode);

        // Redis 缓存验证码
        String codeKey = "sms:code:" + phone;
        redisTemplate.opsForValue().set(codeKey, code, SMS_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 设置冷却时间
        redisTemplate.opsForValue().set(cooldownKey, "1", SMS_COOLDOWN_SECONDS, TimeUnit.SECONDS);

        // 实际接入短信服务商时调用发送接口，开发阶段直接返回
        return code;
    }

    /**
     * 验证验证码
     */
    public boolean verifyCode(String phone, String code) {
        // 先从 Redis 验证
        String codeKey = "sms:code:" + phone;
        String cachedCode = (String) redisTemplate.opsForValue().get(codeKey);
        if (cachedCode != null && cachedCode.equals(code)) {
            redisTemplate.delete(codeKey);
            markCodeAsUsed(phone, code);
            return true;
        }

        // Redis 没有则从数据库验证
        Optional<SmsCode> smsCodeOpt = smsCodeRepository
                .findTopByPhoneAndTypeAndUsedOrderByCreateTimeDesc(phone, "login", 0);

        if (smsCodeOpt.isPresent()) {
            SmsCode smsCode = smsCodeOpt.get();
            if (smsCode.getCode().equals(code)
                    && smsCode.getExpireTime().isAfter(LocalDateTime.now())) {
                markCodeAsUsed(phone, code);
                return true;
            }
        }
        return false;
    }

    private void markCodeAsUsed(String phone, String code) {
        smsCodeRepository.findTopByPhoneAndTypeAndUsedOrderByCreateTimeDesc(phone, "login", 0)
                .ifPresent(smsCode -> {
                    smsCode.setUsed(1);
                    smsCodeRepository.save(smsCode);
                });
    }
}
