package com.reviewyme.notificationservice.service;

import com.reviewyme.notificationservice.model.NotificationRequest;

public interface NotificationService {
    boolean sendNotification(NotificationRequest notificationRequest);
}
