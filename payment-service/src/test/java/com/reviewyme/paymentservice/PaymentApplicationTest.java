package com.reviewyme.paymentservice;

import com.reviewyme.paymentservice.service.WebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PaymentApplicationTest {

    @MockBean
    private WebhookService webhookService;

    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> PaymentApplication.main(new String[]{}));
    }
}
