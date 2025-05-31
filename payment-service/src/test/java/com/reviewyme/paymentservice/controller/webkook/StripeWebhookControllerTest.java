package com.reviewyme.paymentservice.controller.webkook;

import com.reviewyme.paymentservice.model.Status;
import com.reviewyme.paymentservice.service.WebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeWebhookControllerTest {

    @Mock
    private WebhookService webhookService;

    @InjectMocks
    private StripeWebhookController stripeWebhookController;

    @BeforeEach
    void setUp() {
        // Using lenient stubbing to avoid unnecessary stubbing exceptions
        Mockito.lenient().when(webhookService.processWebhook(anyString(), eq(Status.SUCCESS))).thenReturn("Success");
        Mockito.lenient().when(webhookService.processWebhook(anyString(), eq(Status.CANCELLED))).thenReturn("Cancelled");
    }

    @Test
    void testHandleSuccessWebhook() {
        String transactionId = "txn123";
        String response = stripeWebhookController.handleSuccessWebhook(transactionId);

        assertNotNull(response);
        assertEquals("Success", response);
        verify(webhookService, times(1)).processWebhook(transactionId, Status.SUCCESS);
    }

    @Test
    void testHandleCancelWebhook() {
        String transactionId = "txn456";
        String response = stripeWebhookController.handleCancelWebhook(transactionId);

        assertNotNull(response);
        assertEquals("Cancelled", response);
        verify(webhookService, times(1)).processWebhook(transactionId, Status.CANCELLED);
    }

    @Test
    void testHandleSuccessWebhook_FailureScenario() {
        String transactionId = "invalidTxn";

        doThrow(new RuntimeException("Webhook processing failed")).when(webhookService).processWebhook(anyString(), eq(Status.SUCCESS));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> stripeWebhookController.handleSuccessWebhook(transactionId));
        assertEquals("Webhook processing failed", exception.getMessage());

        verify(webhookService, times(1)).processWebhook(transactionId, Status.SUCCESS);
    }

    @Test
    void testHandleCancelWebhook_FailureScenario() {
        String transactionId = "invalidTxn";

        when(webhookService.processWebhook(transactionId, Status.CANCELLED)).thenThrow(new RuntimeException("Cancellation webhook failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> stripeWebhookController.handleCancelWebhook(transactionId));
        assertEquals("Cancellation webhook failed", exception.getMessage());

        verify(webhookService, times(1)).processWebhook(transactionId, Status.CANCELLED);
    }

    @Test
    void testHandleSuccessWebhook_NullTransactionId() {
        String transactionId = null;

        when(webhookService.processWebhook(transactionId, Status.SUCCESS)).thenThrow(new IllegalArgumentException("Transaction ID cannot be null"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> stripeWebhookController.handleSuccessWebhook(transactionId));
        assertEquals("Transaction ID cannot be null", exception.getMessage());

        verify(webhookService, times(1)).processWebhook(transactionId, Status.SUCCESS);
    }

    @Test
    void testHandleCancelWebhook_NullTransactionId() {
        String transactionId = null;

        when(webhookService.processWebhook(transactionId, Status.CANCELLED)).thenThrow(new IllegalArgumentException("Transaction ID cannot be null"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> stripeWebhookController.handleCancelWebhook(transactionId));
        assertEquals("Transaction ID cannot be null", exception.getMessage());

        verify(webhookService, times(1)).processWebhook(transactionId, Status.CANCELLED);
    }
}
