package com.reviewyme.paymentservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StripeCheckoutDto {
    private String productName;
    private Long amount;
    private Long quantity;
    private String currency;
    private String successUrl;
    private String cancelUrl;
}
