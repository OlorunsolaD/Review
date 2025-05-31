package com.reviewyme.paymentservice.handler.impl;

import com.reviewyme.paymentservice.entity.Payment;
import com.reviewyme.paymentservice.handler.WebhookHandler;
import com.reviewyme.paymentservice.model.Status;
import com.reviewyme.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CancelWebhookHandler implements WebhookHandler {
    private final PaymentRepository paymentRepository;

    @Override
    public String handleWebhook(String transactionId) {
        log.info("Transaction Id: {} is Cancelled.", transactionId);
        Optional<Payment> cancelledTransaction = paymentRepository.findByUniqueTransactionId(transactionId);
        cancelledTransaction.ifPresent(payment -> {
            log.info("Updating transaction status...");
            payment.setStatus(String.valueOf(Status.CANCELLED));
            paymentRepository.save(payment);
        });

        return "Payment Cancelled for: " + transactionId;
    }
}