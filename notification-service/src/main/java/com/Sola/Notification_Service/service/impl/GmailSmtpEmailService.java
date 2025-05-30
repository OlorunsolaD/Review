package com.Sola.Notification_Service.service.impl;

import com.Sola.Notification_Service.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GmailSmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;

    private final String emailSender;

    public GmailSmtpEmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String emailSender
    ) {
        this.mailSender = mailSender;
        this.emailSender = emailSender;
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

            log.info("sending email... TO: {}", toEmail);
            log.info("sending email... SUBJECT: {}", subject);
            log.info("sending email... BODY: {}", body);
            //TODO: uncomment line below
//            mailSender.send(message);
            log.info("email sent successfully...");
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}