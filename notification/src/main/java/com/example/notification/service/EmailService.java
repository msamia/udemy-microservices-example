package com.example.notification.service;

import org.thymeleaf.context.Context;

public interface EmailService {

    void send(String to, String subject, String body);
    void sendEmailWithHtmlTemplate(String to, String subject, String body, String templateName);
}
