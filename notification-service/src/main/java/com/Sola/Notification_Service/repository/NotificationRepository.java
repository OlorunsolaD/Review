package com.Sola.Notification_Service.repository;

import com.Sola.Notification_Service.entity.NotificationLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<NotificationLog, String> {
}