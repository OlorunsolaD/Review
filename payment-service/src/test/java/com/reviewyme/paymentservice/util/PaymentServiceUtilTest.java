package com.reviewyme.paymentservice.util;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceUtilTest {

    @Test
    void testGenerateUniqueTransactionId() {
        String transactionId1 = PaymentServiceUtil.generateUniqueTransactionId();
        String transactionId2 = PaymentServiceUtil.generateUniqueTransactionId();

        assertNotNull(transactionId1);
        assertNotNull(transactionId2);
        assertNotEquals(transactionId1, transactionId2);

        assertTrue(transactionId1.contains("-"));
        assertTrue(transactionId2.contains("-"));

        assertTrue(Pattern.matches("^[a-f0-9\\-]+$", transactionId1)); // UUID format check
    }

    @Test
    void testGenerateInternalSessionId() {
        String sessionId1 = PaymentServiceUtil.generateInternalSessionId();
        String sessionId2 = PaymentServiceUtil.generateInternalSessionId();

        assertNotNull(sessionId1);
        assertNotNull(sessionId2);
        assertNotEquals(sessionId1, sessionId2);

        assertFalse(sessionId1.contains("-")); // Ensuring hyphens were replaced
        assertFalse(sessionId2.contains("-"));

        assertTrue(Pattern.matches("^[a-f0-9]+$", sessionId1)); // UUID without hyphen format check
    }
}
