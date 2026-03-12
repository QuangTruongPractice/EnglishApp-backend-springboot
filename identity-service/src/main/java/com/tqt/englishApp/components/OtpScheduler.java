package com.tqt.englishApp.components;

import com.tqt.englishApp.entity.OTP;
import com.tqt.englishApp.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OtpScheduler {
    @Autowired
    private OtpRepository otpRepository;

    @Scheduled(fixedRate = 5* 60 * 1000)
    public void disableExpiredAccounts() {
        List<OTP> otps = this.otpRepository.findAll();
        for (OTP otp : otps) {
            if (LocalDateTime.now().isAfter(otp.getExpiredAt())) {
                this.otpRepository.delete(otp);
            }
        }
        System.out.println(" Checked for expired otp at " + LocalDateTime.now());
    }
}
