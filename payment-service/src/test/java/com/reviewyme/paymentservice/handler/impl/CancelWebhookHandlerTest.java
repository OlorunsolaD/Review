package com.reviewyme.paymentservice.handler.impl;

import com.reviewyme.paymentservice.entity.Payment;
import com.reviewyme.paymentservice.model.Status;
import com.reviewyme.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelWebhookHandlerTest {

    @InjectMocks
    private CancelWebhookHandler cancelWebhookHandler;

    @Mock
    private PaymentRepository paymentRepository;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setCustomerId("12345");
        payment.setUniqueTransactionId("txn_001");
        payment.setReferenceId("ref_001");
        payment.setStatus("PENDING");
    }

    @Test
    void testHandleWebhook_Success() {
        when(paymentRepository.findByUniqueTransactionId("txn_001")).thenReturn(Optional.of(payment));

        String response = cancelWebhookHandler.handleWebhook("txn_001");

        assertNotNull(response);
        assertEquals("Payment Cancelled for: txn_001", response);
        assertEquals(String.valueOf(Status.CANCELLED), payment.getStatus());

        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testHandleWebhook_PaymentNotFound() {
        when(paymentRepository.findByUniqueTransactionId("txn_002")).thenReturn(Optional.empty());

        String response = cancelWebhookHandler.handleWebhook("txn_002");

        assertNotNull(response);
        assertEquals("Payment Cancelled for: txn_002", response);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

}
