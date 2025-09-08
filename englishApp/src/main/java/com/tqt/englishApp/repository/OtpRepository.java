package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<OTP, Long> {
    OTP findByEmailAndOtp(String email, String otp);
}
