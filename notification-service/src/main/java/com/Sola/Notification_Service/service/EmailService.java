package com.Sola.Notification_Service.service;

public interface EmailService {
    boolean sendEmail(String toEmail, String subject, String body);
}