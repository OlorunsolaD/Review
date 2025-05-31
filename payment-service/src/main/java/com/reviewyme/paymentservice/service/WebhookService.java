package com.reviewyme.paymentservice.service;

import com.reviewyme.paymentservice.model.Status;

public interface WebhookService {
    String processWebhook(String transactionId, Status transactionStatus);

    boolean isValidRequest(String payload, String stripeSignature);
}
