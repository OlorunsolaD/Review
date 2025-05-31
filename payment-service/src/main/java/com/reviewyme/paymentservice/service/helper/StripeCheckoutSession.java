package com.reviewyme.paymentservice.service.helper;

import com.reviewyme.paymentservice.config.AppConfig;
import com.reviewyme.paymentservice.constant.AppConstant;
import com.reviewyme.paymentservice.model.StripeCheckoutDto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class StripeCheckoutSession {

    private final AppConfig appConfig;

    public Session createCheckoutSession(StripeCheckoutDto stripeCheckoutDto) throws StripeException {
        Stripe.apiKey = appConfig.getSecretKey();

        // Create session parameters
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(stripeCheckoutDto.getSuccessUrl())
                .setCancelUrl(stripeCheckoutDto.getCancelUrl())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(stripeCheckoutDto.getQuantity())
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(stripeCheckoutDto.getCurrency())
                                                .setUnitAmount(stripeCheckoutDto.getAmount())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(stripeCheckoutDto.getProductName())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        // Create and return the Stripe Checkout Session
        return Session.create(params);
    }

    public boolean isPaid(String sessionId) throws StripeException {
        Stripe.apiKey = appConfig.getSecretKey();

        Session session = Session.retrieve(sessionId);
        return Objects.equals(AppConstant.PAID, session.getPaymentStatus());
    }
}
