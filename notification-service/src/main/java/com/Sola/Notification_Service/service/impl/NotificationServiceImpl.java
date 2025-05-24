package com.Sola.Notification_Service.service.impl;

import com.Sola.Notification_Service.repository.NotificationRepository;
import com.Sola.Notification_Service.model.NotificationRequest;
import com.Sola.Notification_Service.model.feign.UserResponse;
import com.Sola.Notification_Service.service.EmailServiceFactory;
import com.Sola.Notification_Service.service.NotificationService;
import com.Sola.Notification_Service.feign.UserClient;
import com.Sola.Notification_Service.util.NotificationHelper;
import com.Sola.Notification_Service.util.NotificationLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.Sola.Notification_Service.model.NotificationTarget.CUSTOMER;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final UserClient userClient;
    private final EmailServiceFactory emailServiceFactory;
    private final NotificationRepository notificationRepository;

    @Value("${notification.email.enabled}")
    private String isEmailEnabled;

    @Value("${email.service.type}")
    private String emailServiceType;

    public NotificationServiceImpl(UserClient userClient,
                                   EmailServiceFactory emailServiceFactory,
                                   NotificationRepository notificationRepository) {
        this.userClient = userClient;
        this.emailServiceFactory = emailServiceFactory;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public boolean sendNotification(NotificationRequest request) {
        log.info("sending notification...");
        boolean notificationSent = false;
        UserResponse user = new UserResponse();

        String subject = NotificationHelper.getEmailSubject(request);
        String body = NotificationHelper.generateEmailContent(request);

        // if notification target is expert - that means source is customer

        // create logic to set the expert reviewer email -
        // which should be the reviewer that has lesser reviews to make at this time.

        // logic to set target email
        String targetEmail = Strings.EMPTY;

        if (Objects.equals(CUSTOMER,  request.getTarget())){
            user = userClient.getUserInfo(request.getCustomerId());
            targetEmail = user.getEmail();
        }

        boolean emailNotificationEnabled = Objects.equals(Boolean.TRUE.toString(), isEmailEnabled);
        if (emailNotificationEnabled)
            notificationSent = emailServiceFactory.emailService(emailServiceType).sendEmail(targetEmail, subject, body);
        else
            log.info("email notification is not enabled...");

        // Log Notification in MongoDB
        NotificationLogger.logNotificationToDb(request, user, notificationRepository, subject, body);

        return notificationSent;
    }

}