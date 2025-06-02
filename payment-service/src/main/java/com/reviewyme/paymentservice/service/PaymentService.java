package com.reviewyme.paymentservice.service;

import com.reviewyme.paymentservice.model.GetUserPaymentsResponse;
import com.reviewyme.paymentservice.model.InitiatePaymentRequest;
import com.reviewyme.paymentservice.model.InitiatePaymentResponse;
import com.reviewyme.paymentservice.model.VerifyPaymentResponse;

import java.util.List;

public interface PaymentService {
    InitiatePaymentResponse initiatePayment(InitiatePaymentRequest initiatePaymentRequest);

    VerifyPaymentResponse verifyPayment(String transactionId);

    List<GetUserPaymentsResponse> getUserPayments(String userId);
}
