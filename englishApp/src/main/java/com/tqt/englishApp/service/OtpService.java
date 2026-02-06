package com.tqt.englishApp.service;

import com.tqt.englishApp.entity.OTP;
import com.tqt.englishApp.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {
    @Autowired
    private OtpRepository otpRepository;

    public String generateAndSaveOtp(String email) {
        String otpValue = generateRandomNumber();
        OTP otp = OTP.builder()
                .email(email)
                .otp(otpValue)
                .expiredAt(LocalDateTime.now().plusMinutes(3))
                .build();
        otpRepository.save(otp);
        return otpValue;
    }

    public String verifyOtp(String email, String otpValue) {
        OTP otp = otpRepository.findByEmailAndOtp(email, otpValue);
        if (otp == null) {
            return "OTP không tồn tại hoặc không đúng";
        }

        if (otp.getExpiredAt().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            return "OTP đã hết hạn, vui lòng yêu cầu OTP mới";
        }
        return "Xác thực OTP thành công, bạn có thể đổi mật khẩu";
    }

    private String generateRandomNumber() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }
}
