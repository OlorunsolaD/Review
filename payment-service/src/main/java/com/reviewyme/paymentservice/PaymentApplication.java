package com.reviewyme.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
        (scanBasePackages = {
                "com.reviewyme.paymentservice",
                "com.reviewyme.chassis",
        })
public class PaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class);
    }
}
