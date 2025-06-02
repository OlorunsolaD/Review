package com.reviewyme.notificationservice.util;

import com.reviewyme.notificationservice.repository.NotificationRepository;
import com.reviewyme.notificationservice.entity.NotificationLog;
import com.reviewyme.notificationservice.model.NotificationRequest;
import com.reviewyme.notificationservice.model.feign.UserResponse;

import java.time.LocalDateTime;

public class NotificationLogger {

    public static void logNotificationToDb(NotificationRequest request, UserResponse user, NotificationRepository repository, String subject, String body) {
        NotificationLog logEntry = new NotificationLog();
        logEntry.setExpertId(request.getExpertId());
        logEntry.setCustomerId(request.getCustomerId());
        logEntry.setEmail(user.getEmail());
        logEntry.setTarget(String.valueOf(request.getTarget()));
        logEntry.setCvId(request.getCvId());
        logEntry.setSubject(subject);
        logEntry.setBody(body);
        logEntry.setTimestamp(LocalDateTime.now());

        repository.save(logEntry);
    }
}