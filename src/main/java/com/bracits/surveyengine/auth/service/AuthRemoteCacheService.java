package com.bracits.surveyengine.auth.service;

import com.bracits.surveyengine.auth.config.AuthCacheNames;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AuthRemoteCacheService {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Cacheable(cacheNames = AuthCacheNames.OIDC_METADATA, key = "#discoveryUrl", sync = true)
    public String getOidcMetadataJson(String discoveryUrl) {
        return fetchJson(discoveryUrl, "OIDC discovery");
    }

    @CacheEvict(cacheNames = AuthCacheNames.OIDC_METADATA, key = "#discoveryUrl")
    public void evictOidcMetadata(String discoveryUrl) {
        // cache eviction by annotation
    }

    @Cacheable(cacheNames = AuthCacheNames.JWKS, key = "#jwksEndpoint", sync = true)
    public String getJwksJson(String jwksEndpoint) {
        return fetchJson(jwksEndpoint, "JWKS");
    }

    @CacheEvict(cacheNames = AuthCacheNames.JWKS, key = "#jwksEndpoint")
    public void evictJwks(String jwksEndpoint) {
        // cache eviction by annotation
    }

    private String fetchJson(String url, String label) {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                throw new RuntimeException(label + " fetch failed with status " + resp.statusCode());
            }
            String body = resp.body();
            if (body == null || body.isBlank()) {
                throw new RuntimeException(label + " response was empty");
            }
            return body;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(label + " fetch failed: " + ex.getMessage(), ex);
        }
    }
}
