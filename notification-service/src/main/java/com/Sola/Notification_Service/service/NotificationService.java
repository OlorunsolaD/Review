package com.Sola.Notification_Service.service;

import com.Sola.Notification_Service.model.NotificationRequest;

public interface NotificationService {
    boolean sendNotification(NotificationRequest notificationRequest);
}
