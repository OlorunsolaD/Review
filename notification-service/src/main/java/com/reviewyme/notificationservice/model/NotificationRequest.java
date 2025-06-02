package com.reviewyme.notificationservice.model;

import lombok.Data;

@Data
public class NotificationRequest {
    private String customerId;
    private String expertId;
    private NotificationTarget target;
    private String cvId;
}
