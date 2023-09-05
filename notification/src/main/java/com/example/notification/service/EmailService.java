package com.example.notification.service;

public interface EmailService {

    void send(String to, String subject, String body);
}
