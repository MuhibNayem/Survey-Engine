package com.bracits.surveyengine.search.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "survey-engine.search")
@Validated
@Getter
@Setter
public class SearchProperties {

    @Min(1)
    @Max(10)
    private int minQueryLength = 2;

    @Min(8)
    @Max(512)
    private int maxQueryLength = 120;

    @Min(1)
    @Max(100)
    private int defaultLimit = 12;

    @Min(1)
    @Max(100)
    private int maxResults = 30;

    @Min(0)
    @Max(1)
    private double titleSimilarityThreshold = 0.24;

    @Min(0)
    @Max(1)
    private double bodySimilarityThreshold = 0.18;

    @Min(0)
    @Max(1)
    private double titleWordSimilarityThreshold = 0.45;

    @Min(0)
    @Max(1)
    private double bodyWordSimilarityThreshold = 0.35;

    @Min(250)
    @Max(15000)
    private int queryTimeoutMs = 3000;

    @Min(1)
    @Max(1000)
    private int rateLimitPerMinute = 30;

    @Min(1)
    @Max(600)
    private int cacheTtlSeconds = 300;

    @Min(0)
    @Max(3600)
    private int fallbackCacheGraceSeconds = 900;

    @Min(1)
    @Max(50000)
    private int cacheMaxEntries = 2000;

    @Min(50)
    @Max(15000)
    private int slowQueryThresholdMs = 500;

    @Min(1)
    @Max(100)
    private int circuitBreakerSlidingWindowSize = 20;

    @Min(1)
    @Max(100)
    private int circuitBreakerMinimumCalls = 10;

    @Min(1)
    @Max(100)
    private int circuitBreakerFailureRateThreshold = 50;

    @Min(1)
    @Max(300)
    private int circuitBreakerOpenStateSeconds = 30;

    @Min(1)
    @Max(20)
    private int circuitBreakerHalfOpenCalls = 3;
}
