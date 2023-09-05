package com.example.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Profile(value = "smtp")
@Service@RequiredArgsConstructor
public class SMTPEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String body) {
        log.info("Email sending process has started with typed: {} to: {}", subject, to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        this.mailSender.send(message);
    }
}
