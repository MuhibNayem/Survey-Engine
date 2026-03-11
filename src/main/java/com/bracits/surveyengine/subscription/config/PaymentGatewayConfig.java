package com.bracits.surveyengine.subscription.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SslCommerzProperties.class)
public class PaymentGatewayConfig {
}
