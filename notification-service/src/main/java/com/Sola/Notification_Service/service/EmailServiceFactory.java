package com.Sola.Notification_Service.service;

import com.Sola.Notification_Service.service.impl.GmailSmtpEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

import static com.Sola.Notification_Service.constant.AppConstant.GMAIL;

@Configuration
@Slf4j
public class EmailServiceFactory {

    private final GmailSmtpEmailService gmailService;

    public EmailServiceFactory(GmailSmtpEmailService gmailService) {
        this.gmailService = gmailService;
    }

    public EmailService emailService(String emailServiceType) {
        if (Objects.equals(GMAIL, emailServiceType)) {
            return gmailService;
        }
        throw new UnsupportedOperationException("Unsupported email service type: " + emailServiceType);
    }
}