package com.Sola.Notification_Service.service.impl;

import com.Sola.Notification_Service.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
@Slf4j
public class GmailSmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailSender;

    public GmailSmtpEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public boolean sendEmail(String toEmail, String subject, String body) {
        log.info("in Gmail SMTP Service send - Email...");
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);

            helper.setFrom(emailSender);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            //TODO: uncomment line below
            mailSender.send(message);
            log.info("email sent successfully...");
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}