package com.reviewyme.paymentservice.handler;

public interface WebhookHandler {
    String handleWebhook(String transactionId);
}
