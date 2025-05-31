package com.reviewyme.notificationservice.service;

public interface EmailService {
    boolean sendEmail(String toEmail, String subject, String body);
}