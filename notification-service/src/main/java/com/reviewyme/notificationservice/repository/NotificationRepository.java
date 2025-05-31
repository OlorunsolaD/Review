package com.reviewyme.notificationservice.repository;

import com.reviewyme.notificationservice.entity.NotificationLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<NotificationLog, String> {
}