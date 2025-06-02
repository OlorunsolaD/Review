package com.reviewyme.paymentservice.service.impl;

import com.reviewyme.paymentservice.config.AppConfig;
import com.reviewyme.paymentservice.entity.Payment;
import com.reviewyme.paymentservice.exception.PaymentException;
import com.reviewyme.paymentservice.model.*;
import com.reviewyme.paymentservice.repository.PaymentRepository;
import com.reviewyme.paymentservice.service.helper.StripeCheckoutSession;
import com.mongodb.MongoException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private StripeCheckoutSession stripeCheckoutSession;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AppConfig appConfig;

    private InitiatePaymentRequest request;
    private Payment payment;

    @BeforeEach
    void setUp() {
        request = new InitiatePaymentRequest();
        request.getCustomer().setId("12345");
        request.getProduct().setName("Test Product");
        request.getPayment().setAmount(100.0);
        request.getProduct().setQuantity(1);
        request.getPayment().setCurrency(PaymentInfo.CurrencyEnum.USD);

        payment = new Payment();
        payment.setCustomerId("12345");
        payment.setUniqueTransactionId("txn_001");
        payment.setReferenceId("ref_001");
        payment.setStatus("PENDING");
    }

    @Test
    void testInitiatePayment_Success() throws StripeException {
        String transactionId = "txn_001";
        String referenceId = "ref_001";
        String sessionUrl = "https://stripe.com/payment";

        Session session = mock(Session.class);
        when(session.getId()).thenReturn(referenceId);
        when(session.getUrl()).thenReturn(sessionUrl);

        when(stripeCheckoutSession.createCheckoutSession(any(StripeCheckoutDto.class))).thenReturn(session);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        when(appConfig.getSuccessUrl()).thenReturn("http://success_url");
        when(appConfig.getCancelUrl()).thenReturn("http://cancel_url");

        InitiatePaymentResponse response = paymentService.initiatePayment(request);

        assertNotNull(response);
        assertNotNull(response.getTransactionId());
        assertEquals(sessionUrl, response.getPaymentLink());
    }

    @Test
    void testVerifyPayment_Success() {
        when(paymentRepository.findByUniqueTransactionId("txn_001")).thenReturn(Optional.of(payment));

        VerifyPaymentResponse response = paymentService.verifyPayment("txn_001");

        assertNotNull(response);
        assertEquals("PENDING", response.getPaymentStatus());
        assertEquals("ref_001", response.getReferenceId());
    }

    @Test
    void testGetUserPayments_Success() {
        when(paymentRepository.findByCustomerId("12345")).thenReturn(Optional.of(List.of(payment)));

        List<GetUserPaymentsResponse> responses = paymentService.getUserPayments("12345");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("txn_001", responses.get(0).getTransactionId());
    }

    @Test
    void testInitiatePayment_StripeException() throws StripeException {
        when(stripeCheckoutSession.createCheckoutSession(any(StripeCheckoutDto.class)))
                .thenThrow(new InvalidRequestException("Stripe error", null, null, "400", null, null));

        when(appConfig.getSuccessUrl()).thenReturn("http://success_url");
        when(appConfig.getCancelUrl()).thenReturn("http://cancel_url");

        PaymentException exception = assertThrows(PaymentException.class, () -> paymentService.initiatePayment(request));

        assertEquals("Internal Error", exception.getMessage());
    }

    @Test
    void testVerifyPayment_PaymentNotFound() {
        when(paymentRepository.findByUniqueTransactionId("txn_002")).thenReturn(Optional.empty());

        PaymentException exception = assertThrows(PaymentException.class, () -> paymentService.verifyPayment("txn_002"));

        assertEquals("No record found", exception.getMessage());
    }

    @Test
    void testVerifyPayment_MongoException() {
        when(paymentRepository.findByUniqueTransactionId("txn_001")).thenThrow(new MongoException("Database error"));

        PaymentException exception = assertThrows(PaymentException.class, () -> paymentService.verifyPayment("txn_001"));

        assertEquals("Internal Error", exception.getMessage());
    }

    @Test
    void testGetUserPayments_NoRecordsFound() {
        when(paymentRepository.findByCustomerId("99999")).thenReturn(Optional.empty());

        PaymentException exception = assertThrows(PaymentException.class, () -> paymentService.getUserPayments("99999"));

        assertEquals("Bad Request", exception.getMessage());
    }



}
