package com.reviewyme.paymentservice.exception;

import com.reviewyme.chassis.error.ApiException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentException extends ApiException {

    public PaymentException(String code, String message, String reason) {
        super(code, message, reason);
    }

}
