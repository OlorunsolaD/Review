package com.reviewyme.paymentservice.service.impl;

import com.mongodb.MongoException;
import com.reviewyme.paymentservice.config.AppConfig;
import com.reviewyme.paymentservice.entity.Payment;
import com.reviewyme.paymentservice.exception.PaymentException;
import com.reviewyme.paymentservice.model.*;
import com.reviewyme.paymentservice.repository.PaymentRepository;
import com.reviewyme.paymentservice.service.PaymentService;
import com.reviewyme.paymentservice.service.helper.StripeCheckoutSession;
import com.reviewyme.paymentservice.util.PaymentServiceUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.reviewyme.paymentservice.constant.AppConstant.*;
import static com.reviewyme.paymentservice.model.Status.PENDING;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final StripeCheckoutSession stripeCheckoutSession;
    private final PaymentRepository paymentRepository;
    private final AppConfig appConfig;

    private Payment buildPaymentDocument(InitiatePaymentRequest initiatePaymentRequest, String transactionId, String referenceId) {

        return Payment.builder()
                .customerId(initiatePaymentRequest.getCustomer().getId())
                .productName(initiatePaymentRequest.getProduct().getName())
                .amount(initiatePaymentRequest.getPayment().getAmount())
                .quantity(initiatePaymentRequest.getProduct().getQuantity())
                .currency(String.valueOf(initiatePaymentRequest.getPayment().getCurrency()))
                .uniqueTransactionId(transactionId)
                .referenceId(referenceId)
                .status(String.valueOf(PENDING))
                .build();
    }

    private StripeCheckoutDto buildStripeCheckoutDto(InitiatePaymentRequest initiatePaymentRequest, String transactionId) {
        return StripeCheckoutDto.builder()
                .productName(initiatePaymentRequest.getProduct().getName())
                .amount(getAmount(initiatePaymentRequest))
                .quantity(initiatePaymentRequest.getProduct().getQuantity().longValue())
                .currency(String.valueOf(initiatePaymentRequest.getPayment().getCurrency()))
                .successUrl(appConfig.getSuccessUrl().concat(transactionId))
                .cancelUrl(appConfig.getCancelUrl().concat(transactionId))
                .build();
    }

    private static long getAmount(InitiatePaymentRequest initiatePaymentRequest) {
        BigDecimal originalAmount = BigDecimal.valueOf(initiatePaymentRequest.getPayment().getAmount());
        BigDecimal multiplier = new BigDecimal("100");
        BigDecimal multipliedAmount = originalAmount
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(multiplier);
        return multipliedAmount.longValue();
    }

    @Override
    public InitiatePaymentResponse initiatePayment(InitiatePaymentRequest initiatePaymentRequest) {
        InitiatePaymentResponse initiatePaymentResponse = new InitiatePaymentResponse();
        try {
            String transactionId = PaymentServiceUtil.generateUniqueTransactionId();
            initiatePaymentResponse.setTransactionId(transactionId);

            StripeCheckoutDto stripeCheckoutDto = buildStripeCheckoutDto(initiatePaymentRequest, transactionId);

            Session session = stripeCheckoutSession.createCheckoutSession(stripeCheckoutDto);
            String referenceId = session.getId();
            initiatePaymentResponse.setPaymentLink(session.getUrl());

            Payment payment = buildPaymentDocument(initiatePaymentRequest, transactionId, referenceId);
            paymentRepository.save(payment);

        } catch (StripeException e) {
            log.error("Error occurred during Stripe Checkout.{}", e.getMessage());
            throw new PaymentException(
                    ERROR_CODE_5,
                    INTERNAL_ERROR,
                    ERROR_OCCURRED_DURING_INITIATE_PAYMENT
            );
        }
        return initiatePaymentResponse;
    }

    @Override
    public VerifyPaymentResponse verifyPayment(String transactionId) {
        VerifyPaymentResponse verifyPaymentResponse = new VerifyPaymentResponse();
        try {
            Optional<Payment> paymentOptional = paymentRepository.findByUniqueTransactionId(transactionId);
            if (paymentOptional.isPresent()) {
                verifyPaymentResponse.setPaymentStatus(paymentOptional.get().getStatus());
                verifyPaymentResponse.setReferenceId(paymentOptional.get().getReferenceId());
            } else {
                log.error("no record with transaction Id: {} found in db", transactionId);
                throw new PaymentException(
                        ERROR_CODE_4,
                        NO_RECORD_FOUND,
                        INVALID_TRANSACTION_ID_SUPPLIED
                );
            }
        } catch (MongoException e) {
            log.error("error occurred...{}", e.getMessage());
            throw new PaymentException(
                    ERROR_CODE_5,
                    INTERNAL_ERROR,
                    ERROR_OCCURRED_WHILE_FETCHING_DATA_FROM_DB
            );
        }
        return verifyPaymentResponse;
    }

    @Override
    public List<GetUserPaymentsResponse> getUserPayments(String userId) {
        List<GetUserPaymentsResponse> getUserPaymentsResponseList = new ArrayList<>();
        Optional<List<Payment>> optionalPaymentList = paymentRepository.findByCustomerId(userId);
        if (optionalPaymentList.isPresent()) {
            optionalPaymentList.get().forEach(payment -> {
                GetUserPaymentsResponse getUserPaymentsResponse = new GetUserPaymentsResponse();
                getUserPaymentsResponse.setTransactionId(payment.getUniqueTransactionId());
                getUserPaymentsResponseList.add(getUserPaymentsResponse);
            });
            return getUserPaymentsResponseList;
        } else {
            log.error("No payment record found for user: {}", userId);
            throw new PaymentException(
                    ERROR_CODE_4,
                    BAD_REQUEST,
                    NO_PAYMENT_RECORD_FOUND_FOR_USER
            );
        }
    }

}
