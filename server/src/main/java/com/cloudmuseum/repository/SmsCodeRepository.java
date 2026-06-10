package com.cloudmuseum.repository;

import com.cloudmuseum.entity.SmsCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SmsCodeRepository extends JpaRepository<SmsCode, Long> {
    Optional<SmsCode> findTopByPhoneAndTypeAndUsedOrderByCreateTimeDesc(String phone, String type, Integer used);
    int countByPhoneAndCreateTimeAfter(String phone, LocalDateTime time);
}
