package com.bracits.surveyengine.config;

import com.bracits.surveyengine.auth.config.AuthCacheNames;
import com.bracits.surveyengine.auth.config.AuthCacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@EnableConfigurationProperties(AuthCacheProperties.class)
public class RedisCacheConfig {

    @Bean
    public RedisCacheManager redisCacheManager(
            RedisConnectionFactory redisConnectionFactory,
            AuthCacheProperties authCacheProperties) {

        RedisCacheConfiguration defaults = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(
                AuthCacheNames.OIDC_METADATA,
                buildCacheConfig(defaults, authCacheProperties.getOidcMetadata(), Duration.ofHours(24)));
        cacheConfigurations.put(
                AuthCacheNames.JWKS,
                buildCacheConfig(defaults, authCacheProperties.getJwks(), Duration.ofMinutes(15)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaults)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    private RedisCacheConfiguration buildCacheConfig(
            RedisCacheConfiguration defaults,
            AuthCacheProperties.Bucket bucket,
            Duration defaultTtl) {
        Duration ttl = bucket != null && bucket.getTtl() != null && !bucket.getTtl().isZero() && !bucket.getTtl().isNegative()
                ? bucket.getTtl()
                : defaultTtl;
        String keyPrefix = bucket != null && bucket.getKeyPrefix() != null && !bucket.getKeyPrefix().isBlank()
                ? bucket.getKeyPrefix()
                : "survey-engine:auth-cache";

        return defaults
                .entryTtl(ttl)
                .computePrefixWith(cacheName -> keyPrefix + ":" + cacheName + ":");
    }
}
