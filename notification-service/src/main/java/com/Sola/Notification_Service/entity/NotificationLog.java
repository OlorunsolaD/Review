package com.Sola.Notification_Service.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "notifications")
public class NotificationLog {
    private String id;
    private String expertId;
    private String customerId;
    private String email;
    private String target;
    private String cvId;
    private String downloadLink;
    private String subject;
    private String body;
    private LocalDateTime timestamp;
}