package com.bracits.surveyengine.subscription.service;

import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.subscription.config.SslCommerzProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "survey-engine.payment.sslcommerz", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SslCommerzPaymentGateway implements PaymentGateway {

    private final SslCommerzProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public CheckoutSession initiateCheckout(CheckoutRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("store_id", properties.getStoreId());
        form.add("store_passwd", properties.getStorePassword());
        form.add("total_amount", request.amount().setScale(2).toPlainString());
        form.add("currency", request.currency());
        form.add("tran_id", request.gatewayReference());
        form.add("success_url", absoluteUrl(properties.getSuccessPath()));
        form.add("fail_url", absoluteUrl(properties.getFailPath()));
        form.add("cancel_url", absoluteUrl(properties.getCancelPath()));
        form.add("ipn_url", absoluteUrl(properties.getSuccessPath()));
        form.add("product_name", request.plan().name() + " Subscription");
        form.add("product_category", "SaaS Subscription");
        form.add("product_profile", "general");
        form.add("shipping_method", "NO");
        form.add("num_of_item", "1");
        form.add("cus_name", request.customerName());
        form.add("cus_email", request.customerEmail());
        form.add("cus_add1", request.tenantName());
        form.add("cus_city", "Dhaka");
        form.add("cus_country", "Bangladesh");
        form.add("cus_phone", "01700000000");
        form.add("value_a", request.tenantId());
        form.add("value_b", request.plan().name());
        form.add("value_c", request.checkoutSource());
        form.add("value_d", providerName());

        JsonNode response = postForm(properties.getSessionApiUrl(), form);
        String status = response.path("status").asText();
        String paymentUrl = response.path("GatewayPageURL").asText();
        if (!"SUCCESS".equalsIgnoreCase(status) || paymentUrl.isBlank()) {
            throw new BusinessException(ErrorCode.PAYMENT_FAILED, "Unable to create SSLCommerz checkout session");
        }

        return new CheckoutSession(
                paymentUrl,
                request.gatewayReference(),
                response.path("sessionkey").asText(null),
                status);
    }

    @Override
    public ValidationResult validatePayment(ValidationRequest request) {
        URI uri = UriComponentsBuilder.fromUriString(properties.getValidationApiUrl())
                .queryParam("val_id", request.validationReference())
                .queryParam("store_id", properties.getStoreId())
                .queryParam("store_passwd", properties.getStorePassword())
                .queryParam("v", "1")
                .queryParam("format", "json")
                .build(true)
                .toUri();
        JsonNode response = getJson(uri);
        String status = response.path("status").asText();
        boolean success = isSuccessfulValidationStatus(status)
                && request.gatewayReference().equals(response.path("tran_id").asText())
                && request.currency().equalsIgnoreCase(response.path("currency_type").asText(request.currency()))
                && request.amount().compareTo(new BigDecimal(response.path("amount").asText(request.amount().toPlainString()))) == 0;

        return new ValidationResult(
                success,
                response.path("tran_id").asText(request.gatewayReference()),
                request.validationReference(),
                status);
    }

    @Override
    public String providerName() {
        return "SSLCOMMERZ";
    }

    private boolean isSuccessfulValidationStatus(String status) {
        return "VALID".equalsIgnoreCase(status) || "VALIDATED".equalsIgnoreCase(status);
    }

    private String absoluteUrl(String path) {
        return UriComponentsBuilder.fromUriString(properties.getCallbackBaseUrl())
                .path(path)
                .build()
                .toUriString();
    }

    private JsonNode postForm(String url, MultiValueMap<String, String> form) {
        String body = form.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(value -> encode(entry.getKey()) + "=" + encode(value)))
                .reduce((left, right) -> left + "&" + right)
                .orElse("");
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return send(request);
    }

    private JsonNode getJson(URI uri) {
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        return send(request);
    }

    private JsonNode send(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new BusinessException(ErrorCode.PAYMENT_FAILED, "SSLCommerz request failed with HTTP " + response.statusCode());
            }
            return objectMapper.readTree(response.body());
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new BusinessException(ErrorCode.PAYMENT_FAILED, "Unable to communicate with SSLCommerz");
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
