package com.reviewyme.paymentservice.handler.impl;

import com.reviewyme.paymentservice.entity.Payment;
import com.reviewyme.paymentservice.model.Status;
import com.reviewyme.paymentservice.repository.PaymentRepository;
import com.reviewyme.paymentservice.service.helper.StripeCheckoutSession;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
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
class SuccessWebhookHandlerTest {

    @InjectMocks
    private SuccessWebhookHandler successWebhookHandler;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private StripeCheckoutSession stripeCheckoutSession;

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
    void testHandleWebhook_Success() throws StripeException {
        when(paymentRepository.findByUniqueTransactionId("txn_001")).thenReturn(Optional.of(payment));
        when(stripeCheckoutSession.isPaid("ref_001")).thenReturn(true);

        String response = successWebhookHandler.handleWebhook("txn_001");

        assertNotNull(response);
        assertTrue(response.contains("Payment for: txn_001 was SUCCESS"));
        assertEquals(String.valueOf(Status.SUCCESS), payment.getStatus());

        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testHandleWebhook_Cancelled() throws StripeException {
        when(paymentRepository.findByUniqueTransactionId("txn_001")).thenReturn(Optional.of(payment));
        when(stripeCheckoutSession.isPaid("ref_001")).thenReturn(false);

        String response = successWebhookHandler.handleWebhook("txn_001");

        assertNotNull(response);
        assertTrue(response.contains("Payment for: txn_001 was CANCELLED"));
        assertEquals(String.valueOf(Status.CANCELLED), payment.getStatus());

        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testHandleWebhook_PaymentNotFound() {
        when(paymentRepository.findByUniqueTransactionId("txn_002")).thenReturn(Optional.empty());

        String response = successWebhookHandler.handleWebhook("txn_002");

        assertNotNull(response);
        assertTrue(response.contains("Payment for: txn_002 was "));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testHandleWebhook_StripeException() throws StripeException {
        when(paymentRepository.findByUniqueTransactionId("txn_001")).thenReturn(Optional.of(payment));
        when(stripeCheckoutSession.isPaid("ref_001")).thenThrow(new InvalidRequestException("Stripe error", null, null, "400", null, null));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> successWebhookHandler.handleWebhook("txn_001"));

        assertEquals("com.stripe.exception.InvalidRequestException: Stripe error; code: 400", exception.getMessage());
    }

}
