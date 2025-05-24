package com.Sola.Notification_Service.util;

import com.Sola.Notification_Service.model.NotificationRequest;
import com.Sola.Notification_Service.model.NotificationTarget;

public class NotificationHelper {
    
    public static String generateEmailContent(NotificationRequest request) {
        String baseUrl = "https://localhost:8080/";
        String reviewLink = request.getTarget() == NotificationTarget.EXPERT ?
                baseUrl + "link_to_review_pending_document" :
                baseUrl + "link_to_review_completed_document";

        return String.format("CV ID: %s%nDownload: %s%n%s",
                request.getCvId(),
                reviewLink,
                request.getTarget() == NotificationTarget.EXPERT ? "Please review." : "Your CV review is complete.");
    }

    public static String getEmailSubject(NotificationRequest request) {
        return request.getTarget() == NotificationTarget.EXPERT ?
                "Expert CV Review Required" :
                "CV Review Status";
    }
}