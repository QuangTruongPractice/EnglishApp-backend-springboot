package com.tqt.englishApp.service;

import com.tqt.englishApp.entity.OTP;
import com.tqt.englishApp.entity.ResetToken;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.repository.OtpRepository;
import com.tqt.englishApp.repository.ResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class OtpService {
    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private ResetTokenRepository resetTokenRepository;

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
            throw new AppException(ErrorCode.OTP_INVALID);
        }

        if (otp.getExpiredAt().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }

        otpRepository.delete(otp);

        // Tạo reset token (UUID) làm bằng chứng đã verify OTP
        String tokenValue = UUID.randomUUID().toString();
        ResetToken resetToken = ResetToken.builder()
                .token(tokenValue)
                .email(email)
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();
        resetTokenRepository.save(resetToken);

        return tokenValue;
    }

    public String verifyResetToken(String token) {
        ResetToken resetToken = resetTokenRepository.findByToken(token);
        if (resetToken == null) {
            throw new AppException(ErrorCode.RESET_TOKEN_INVALID);
        }
        if (resetToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            resetTokenRepository.delete(resetToken);
            throw new AppException(ErrorCode.RESET_TOKEN_EXPIRED);
        }

        String email = resetToken.getEmail();
        resetTokenRepository.delete(resetToken); // Dùng 1 lần rồi xóa
        return email;
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
