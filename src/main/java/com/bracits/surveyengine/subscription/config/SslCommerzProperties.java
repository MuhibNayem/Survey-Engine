package com.bracits.surveyengine.subscription.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "survey-engine.payment.sslcommerz")
@Validated
@Getter
@Setter
public class SslCommerzProperties {

    private boolean enabled = true;

    @NotBlank
    private String storeId;

    @NotBlank
    private String storePassword;

    @NotBlank
    private String sessionApiUrl;

    @NotBlank
    private String validationApiUrl;

    @NotBlank
    private String callbackBaseUrl;

    @NotBlank
    private String resultPagePath = "/payment/checkout";

    @NotBlank
    private String successPath = "/api/v1/public/payments/sslcommerz/success";

    @NotBlank
    private String failPath = "/api/v1/public/payments/sslcommerz/fail";

    @NotBlank
    private String cancelPath = "/api/v1/public/payments/sslcommerz/cancel";
}
