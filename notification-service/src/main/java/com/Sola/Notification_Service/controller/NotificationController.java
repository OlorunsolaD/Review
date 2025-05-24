package com.Sola.Notification_Service.controller;

import com.Sola.Notification_Service.model.NotificationRequest;
import com.Sola.Notification_Service.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public String sendNotification(@RequestBody NotificationRequest request) {
        boolean success = notificationService.sendNotification(request);
        return success ? "Notification sent successfully!" : "Failed to send notification.";
    }
}