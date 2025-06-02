package com.reviewyme.notificationservice.controller;

import com.reviewyme.notificationservice.model.NotificationRequest;
import com.reviewyme.notificationservice.service.NotificationService;
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