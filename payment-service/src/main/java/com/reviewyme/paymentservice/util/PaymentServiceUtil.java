package com.reviewyme.paymentservice.util;

import org.apache.logging.log4j.util.Strings;

import java.time.Instant;
import java.util.UUID;

import static com.reviewyme.paymentservice.constant.AppConstant.HYPHEN;

public class PaymentServiceUtil {

    public static String generateUniqueTransactionId() {
        return UUID.randomUUID().toString()
                .concat(HYPHEN)
                .concat(String.valueOf(Instant.now().getEpochSecond()));
    }

    public static String generateInternalSessionId() {
        return UUID.randomUUID().toString()
                .concat(HYPHEN)
                .concat(String.valueOf(Instant.now().getEpochSecond()))
                .replace(HYPHEN, Strings.EMPTY);
    }
}
