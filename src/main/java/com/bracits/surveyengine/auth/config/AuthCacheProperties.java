package com.bracits.surveyengine.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "survey-engine.auth.cache")
@Validated
@Getter
@Setter
public class AuthCacheProperties {

    private Bucket oidcMetadata = new Bucket(
            Duration.ofHours(24),
            "survey-engine:auth-cache:oidc-metadata");

    private Bucket jwks = new Bucket(
            Duration.ofMinutes(15),
            "survey-engine:auth-cache:jwks");

    @Getter
    @Setter
    public static class Bucket {
        private Duration ttl;
        private String keyPrefix;

        public Bucket() {
        }

        public Bucket(Duration ttl, String keyPrefix) {
            this.ttl = ttl;
            this.keyPrefix = keyPrefix;
        }
    }
}
