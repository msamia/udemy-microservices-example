package com.example.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Profile(value = "smtp")
@Service
public class SMTPEmailService implements EmailService {


    @Override
    public void send(String to, String subject, String body) {
        log.info("Email sending process has started with typed: {} to: {}", subject, to);

    }
}
