package com.tqt.englishApp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private JavaMailSender emailSender;

    @Test
    void sendSimpleMessage_Success() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Content";

        mailService.sendSimpleMessage(to, subject, text);

        verify(emailSender).send(any(SimpleMailMessage.class));
    }
}
