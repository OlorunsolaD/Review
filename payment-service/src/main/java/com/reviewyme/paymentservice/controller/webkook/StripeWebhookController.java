package com.reviewyme.paymentservice.controller.webkook;

import com.reviewyme.paymentservice.model.Status;
import com.reviewyme.paymentservice.service.WebhookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/v1/webhook/transactions")
public class StripeWebhookController {

    private final WebhookService webhookService;

    public StripeWebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @GetMapping("/{transactionId}")
    public String handleSuccessWebhook(@PathVariable String transactionId) {
        log.info("Received success callback for transaction id: {}", transactionId);
        return webhookService.processWebhook(transactionId, Status.SUCCESS);
    }

    @GetMapping("/cancel/{transactionId}")
    public String handleCancelWebhook(@PathVariable String transactionId) {
        log.info("Received cancel callback for transaction id: {}", transactionId);
        return webhookService.processWebhook(transactionId, Status.CANCELLED);
    }
}
