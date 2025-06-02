package com.reviewyme.paymentservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "stripe")
@Data
public class AppConfig {
    private String secretKey;
    private String successUrl;
    private String cancelUrl;
}
