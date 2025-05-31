package com.reviewyme.paymentservice.service.helper;

import com.reviewyme.paymentservice.config.AppConfig;
import com.reviewyme.paymentservice.constant.AppConstant;
import com.reviewyme.paymentservice.model.StripeCheckoutDto;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeCheckoutSessionTest {

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private StripeCheckoutSession stripeCheckoutSession;

    private StripeCheckoutDto checkoutDto;
    private Session mockSession;

    @BeforeEach
    void setUp() {
        checkoutDto = new StripeCheckoutDto();
        checkoutDto.setSuccessUrl("https://success.com");
        checkoutDto.setCancelUrl("https://cancel.com");
        checkoutDto.setQuantity(1L);
        checkoutDto.setAmount(5000L);
        checkoutDto.setCurrency("USD");
        checkoutDto.setProductName("Test Product");

        mockSession = new Session();
        mockSession.setId("session_123");
        mockSession.setPaymentStatus(AppConstant.PAID);

        Mockito.lenient().when(appConfig.getSecretKey()).thenReturn("test-secret-key");
    }

    // Happy case: Successful checkout session creation
    @Test
    void testCreateCheckoutSession_Success() throws StripeException {
        try (var mockStaticStripe = Mockito.mockStatic(Session.class)) {
            mockStaticStripe.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(mockSession);

            Session session = stripeCheckoutSession.createCheckoutSession(checkoutDto);

            assertNotNull(session);
            assertEquals("session_123", session.getId());
            verify(appConfig, times(1)).getSecretKey();
        }
    }

    // Unhappy case: Stripe API exception during checkout session creation
    @Test
    void testCreateCheckoutSession_StripeException() throws StripeException {
        try (var mockStaticStripe = Mockito.mockStatic(Session.class)) {
            mockStaticStripe.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenThrow(new InvalidRequestException("Failed to create session", null, null, "400", null, null));

            assertThrows(InvalidRequestException.class, () -> stripeCheckoutSession.createCheckoutSession(checkoutDto));

            verify(appConfig, times(1)).getSecretKey();
        }
    }


    // Happy case: Checking if payment is successful
    @Test
    void testIsPaid_Success() throws StripeException {
        try (var mockStaticStripe = Mockito.mockStatic(Session.class)) {
            mockStaticStripe.when(() -> Session.retrieve("session_123")).thenReturn(mockSession);

            boolean isPaid = stripeCheckoutSession.isPaid("session_123");

            assertTrue(isPaid);
            verify(appConfig, times(1)).getSecretKey();
        }
    }

    // Unhappy case: Stripe API exception while retrieving session
    @Test
    void testIsPaid_StripeException() throws StripeException {
        try (var mockStaticStripe = Mockito.mockStatic(Session.class)) {
            mockStaticStripe.when(() -> Session.retrieve("invalid_session"))
                    .thenThrow(new InvalidRequestException("Session not found", null, null, "400", null, null));

            assertThrows(InvalidRequestException.class, () -> stripeCheckoutSession.isPaid("invalid_session"));

            verify(appConfig, times(1)).getSecretKey();
        }
    }


    // Unhappy case: Null session ID
    @Test
    void testIsPaid_NullSessionId() {
        assertThrows(InvalidRequestException.class, () -> stripeCheckoutSession.isPaid(null));
    }
}