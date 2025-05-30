package com.Sola.Notification_Service.service.impl;

import com.Sola.Notification_Service.feign.UserClient;
import com.Sola.Notification_Service.model.NotificationRequest;
import com.Sola.Notification_Service.model.feign.UserResponse;
import com.Sola.Notification_Service.repository.NotificationRepository;
import com.Sola.Notification_Service.service.EmailServiceFactory;
import com.Sola.Notification_Service.service.NotificationService;
import com.Sola.Notification_Service.util.NotificationHelper;
import com.Sola.Notification_Service.util.NotificationLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final UserClient userClient;
    private final EmailServiceFactory emailServiceFactory;
    private final NotificationRepository notificationRepository;
    private final boolean isEmailEnabled;
    private final String emailServiceType;

    public NotificationServiceImpl(
            UserClient userClient,
            EmailServiceFactory emailServiceFactory,
            NotificationRepository notificationRepository,
            @Value("${notification.email.enabled}") String emailEnabledRaw,
            @Value("${email.service.type}") String emailServiceType
    ) {
        this.userClient = userClient;
        this.emailServiceFactory = emailServiceFactory;
        this.notificationRepository = notificationRepository;

        this.isEmailEnabled = Boolean.parseBoolean(emailEnabledRaw); // Converts "true" or "false" properly
        this.emailServiceType = emailServiceType;
    }

    @Override
    public boolean sendNotification(NotificationRequest request) {
        log.info("Processing notification to: {}", request.getTarget());

        UserResponse user = resolveTargetUser(request);
        String subject = NotificationHelper.getEmailSubject(request);
        String body = NotificationHelper.generateEmailContent(request);

        boolean notificationSent = false;

        if (isEmailEnabled) {
            notificationSent = emailServiceFactory.emailService(emailServiceType)
                    .sendEmail(user.getEmail(), subject, body);
        } else {
            log.info("Email notification is disabled.");
        }

        // Log Notification in MongoDB
        NotificationLogger.logNotificationToDb(request, user, notificationRepository, subject, body);

        return notificationSent;
    }

    private UserResponse resolveTargetUser(NotificationRequest request) {
        log.info("Resolving target...");

        return switch (request.getTarget()) {
            case CUSTOMER -> userClient.getUserInfo(request.getCustomerId());
            case EXPERT -> userClient.getAvailableExpertUserInfo();
        };
    }

}