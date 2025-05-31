package com.reviewyme.paymentservice.service.impl;

import com.reviewyme.paymentservice.config.AppConfig;
import com.reviewyme.paymentservice.handler.impl.SuccessWebhookHandler;
import com.reviewyme.paymentservice.model.Status;
import com.reviewyme.paymentservice.repository.PaymentRepository;
import com.reviewyme.paymentservice.service.helper.StripeCheckoutSession;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.Webhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebhookServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private StripeCheckoutSession stripeCheckoutSession;

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private WebhookServiceImpl webhookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessWebhook_Success() {
        String transactionId = "12345";

        // Mock the SuccessWebhookHandler behavior
        SuccessWebhookHandler successWebhookHandler = spy(new SuccessWebhookHandler(paymentRepository, stripeCheckoutSession));
        doReturn("SUCCESS").when(successWebhookHandler).handleWebhook(transactionId);

        String response = webhookService.processWebhook(transactionId, Status.SUCCESS);

        assertNotNull(response);
        assertTrue(response.contains("Payment for: "));
    }

    @Test
    void testProcessWebhook_Cancelled() {
        String transactionId = "67890";

        // Mock the SuccessWebhookHandler behavior for cancelled transaction
        SuccessWebhookHandler successWebhookHandler = spy(new SuccessWebhookHandler(paymentRepository, stripeCheckoutSession));
        doReturn("CANCELLED").when(successWebhookHandler).handleWebhook(transactionId);

        String response = webhookService.processWebhook(transactionId, Status.CANCELLED);

        assertNotNull(response);
        assertTrue(response.contains("Payment for: "));
    }

    @Test
    void testProcessWebhook_InvalidStatus() {
        String transactionId = "99999";

        String response = webhookService.processWebhook(transactionId, Status.FAILED);

        assertNull(response);
    }

    @Test
    void testIsValidRequest_ValidSignature() {
        String payload = "{}";
        String stripeSignature = "valid-signature";
        String webhookSecret = "test-secret";

        when(appConfig.getSecretKey()).thenReturn(webhookSecret);

        boolean isValid = webhookService.isValidRequest(payload, stripeSignature);

        assertTrue(isValid);
    }

    @Test
    void testIsValidRequest_InvalidSignature() {
        String payload = "{}";
        String stripeSignature = "invalid-signature";
        String webhookSecret = "test-secret";

        when(appConfig.getSecretKey()).thenReturn(webhookSecret);

        // Mock the exception when Webhook.constructEvent fails
        Mockito.mockStatic(Webhook.class).when(() -> Webhook.constructEvent(payload, stripeSignature, webhookSecret))
                .thenThrow(new SignatureVerificationException("Invalid signature", stripeSignature));

        assertThrows(RuntimeException.class, () -> webhookService.isValidRequest(payload, stripeSignature));
    }
}
