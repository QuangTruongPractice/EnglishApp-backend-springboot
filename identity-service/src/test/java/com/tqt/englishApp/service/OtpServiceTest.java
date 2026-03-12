package com.tqt.englishApp.service;

import com.tqt.englishApp.entity.OTP;
import com.tqt.englishApp.repository.OtpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @InjectMocks
    private OtpService otpService;

    @Mock
    private OtpRepository otpRepository;

    private String email = "test@example.com";
    private String otpValue = "1234";
    private OTP otp;

    @BeforeEach
    void init() {
        otp = OTP.builder()
                .email(email)
                .otp(otpValue)
                .expiredAt(LocalDateTime.now().plusMinutes(3))
                .build();
    }

    @Test
    void generateAndSaveOtp_Success() {
        String result = otpService.generateAndSaveOtp(email);

        assertNotNull(result);
        assertEquals(4, result.length());
        verify(otpRepository).save(any(OTP.class));
    }

    @Test
    void verifyOtp_Success() {
        when(otpRepository.findByEmailAndOtp(email, otpValue)).thenReturn(otp);

        String result = otpService.verifyOtp(email, otpValue);

        assertEquals("Xác thực OTP thành công, bạn có thể đổi mật khẩu", result);
    }

    @Test
    void verifyOtp_NotFound() {
        when(otpRepository.findByEmailAndOtp(email, otpValue)).thenReturn(null);

        String result = otpService.verifyOtp(email, otpValue);

        assertEquals("OTP không tồn tại hoặc không đúng", result);
    }

    @Test
    void verifyOtp_Expired() {
        otp.setExpiredAt(LocalDateTime.now().minusMinutes(1));
        when(otpRepository.findByEmailAndOtp(email, otpValue)).thenReturn(otp);

        String result = otpService.verifyOtp(email, otpValue);

        assertEquals("OTP đã hết hạn, vui lòng yêu cầu OTP mới", result);
        verify(otpRepository).delete(otp);
    }
}
