# AuthProfilesApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createAuthProfile**](AuthProfilesApi.md#createAuthProfile) | **POST** /api/v1/auth/profiles | Create tenant responder-auth profile |
| [**getAuthProfileAudit**](AuthProfilesApi.md#getAuthProfileAudit) | **GET** /api/v1/auth/profiles/{id}/audit | Get auth profile audit history |
| [**getAuthProfileByTenant**](AuthProfilesApi.md#getAuthProfileByTenant) | **GET** /api/v1/auth/profiles/tenant/{tenantId} | Get auth profile for a tenant |
| [**getProviderTemplate**](AuthProfilesApi.md#getProviderTemplate) | **GET** /api/v1/auth/providers/templates/{providerCode} | Get one provider setup template |
| [**listProviderTemplates**](AuthProfilesApi.md#listProviderTemplates) | **GET** /api/v1/auth/providers/templates | List built-in provider setup templates |
| [**rotateAuthProfileKey**](AuthProfilesApi.md#rotateAuthProfileKey) | **POST** /api/v1/auth/profiles/{id}/rotate-key | Rotate auth profile key version |
| [**updateAuthProfile**](AuthProfilesApi.md#updateAuthProfile) | **PUT** /api/v1/auth/profiles/{id} | Update tenant responder-auth profile |
| [**validateResponderToken**](AuthProfilesApi.md#validateResponderToken) | **POST** /api/v1/auth/validate/{tenantId} | Validate responder token against tenant auth policy |


<a id="createAuthProfile"></a>
# **createAuthProfile**
> AuthProfileResponse createAuthProfile(authProfileRequest)

Create tenant responder-auth profile

Why this endpoint is needed: Private campaign authentication must be configured once per tenant so every campaign can reuse a consistent trust policy.  What this endpoint does: It creates a tenant auth profile containing auth mode, trust metadata, claim mappings, fallback policy, and OIDC/signing settings.  How this endpoint does it: The service validates tenant scope, enforces mapping rules (including required respondentId mapping), applies defaults (such as OIDC scope defaults), persists profile + mappings, and writes audit events. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AuthProfilesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AuthProfilesApi apiInstance = new AuthProfilesApi(defaultClient);
    AuthProfileRequest authProfileRequest = new AuthProfileRequest(); // AuthProfileRequest | 
    try {
      AuthProfileResponse result = apiInstance.createAuthProfile(authProfileRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AuthProfilesApi#createAuthProfile");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **authProfileRequest** | [**AuthProfileRequest**](AuthProfileRequest.md)|  | |

### Return type

[**AuthProfileResponse**](AuthProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Auth profile created |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |

<a id="getAuthProfileAudit"></a>
# **getAuthProfileAudit**
> List&lt;AuthConfigAudit&gt; getAuthProfileAudit(id)

Get auth profile audit history

Why this endpoint is needed: Security-sensitive configuration changes require traceability for compliance and troubleshooting.  What this endpoint does: It returns ordered audit entries for an auth profile including actor and before/after values.  How this endpoint does it: The repository queries audit rows by auth_profile_id in descending timestamp order and returns serialized audit objects. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AuthProfilesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AuthProfilesApi apiInstance = new AuthProfilesApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      List<AuthConfigAudit> result = apiInstance.getAuthProfileAudit(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AuthProfilesApi#getAuthProfileAudit");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **UUID**|  | |

### Return type

[**List&lt;AuthConfigAudit&gt;**](AuthConfigAudit.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Audit entries returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |

<a id="getAuthProfileByTenant"></a>
# **getAuthProfileByTenant**
> AuthProfileResponse getAuthProfileByTenant(tenantId)

Get auth profile for a tenant

Why this endpoint is needed: Operators need an easy way to verify effective auth configuration before launching or troubleshooting private campaigns.  What this endpoint does: It returns the tenant&#39;s auth profile and claim mapping configuration.  How this endpoint does it: Tenant access checks are performed, then profile data is loaded and mapped to response DTO. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AuthProfilesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AuthProfilesApi apiInstance = new AuthProfilesApi(defaultClient);
    String tenantId = "tenantId_example"; // String | 
    try {
      AuthProfileResponse result = apiInstance.getAuthProfileByTenant(tenantId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AuthProfilesApi#getAuthProfileByTenant");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **tenantId** | **String**|  | |

### Return type

[**AuthProfileResponse**](AuthProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Auth profile returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="getProviderTemplate"></a>
# **getProviderTemplate**
> ProviderTemplateResponse getProviderTemplate(providerCode)

Get one provider setup template

Why this endpoint is needed: Setup forms often require provider-specific field guidance and defaults rather than a full template catalog.  What this endpoint does: It returns one template by provider code (for example OKTA, AUTH0, AZURE_AD, KEYCLOAK).  How this endpoint does it: The service validates provider code input, resolves the template from in-memory catalog, and returns structured metadata. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AuthProfilesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AuthProfilesApi apiInstance = new AuthProfilesApi(defaultClient);
    String providerCode = "providerCode_example"; // String | 
    try {
      ProviderTemplateResponse result = apiInstance.getProviderTemplate(providerCode);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AuthProfilesApi#getProviderTemplate");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **providerCode** | **String**|  | |

### Return type

[**ProviderTemplateResponse**](ProviderTemplateResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Provider template returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="listProviderTemplates"></a>
# **listProviderTemplates**
> List&lt;ProviderTemplateResponse&gt; listProviderTemplates()

List built-in provider setup templates

Why this endpoint is needed: Tenant onboarding is faster and less error-prone when common IdP providers have pre-defined setup templates.  What this endpoint does: It returns template metadata for supported providers, including default scopes, required config fields, and default claim mapping suggestions.  How this endpoint does it: A template service returns static curated provider definitions consumed by UI and SDK onboarding flows. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AuthProfilesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AuthProfilesApi apiInstance = new AuthProfilesApi(defaultClient);
    try {
      List<ProviderTemplateResponse> result = apiInstance.listProviderTemplates();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AuthProfilesApi#listProviderTemplates");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;ProviderTemplateResponse&gt;**](ProviderTemplateResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Provider templates returned |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="rotateAuthProfileKey"></a>
# **rotateAuthProfileKey**
> AuthProfileResponse rotateAuthProfileKey(id)

Rotate auth profile key version

Why this endpoint is needed: Signing key version rotation is required for security hygiene and incident response readiness.  What this endpoint does: It increments the active key version for a tenant auth profile.  How this endpoint does it: The service updates active key version, persists change, and creates an audit record describing the rotation action. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AuthProfilesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AuthProfilesApi apiInstance = new AuthProfilesApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      AuthProfileResponse result = apiInstance.rotateAuthProfileKey(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AuthProfilesApi#rotateAuthProfileKey");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **UUID**|  | |

### Return type

[**AuthProfileResponse**](AuthProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Key version rotated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="updateAuthProfile"></a>
# **updateAuthProfile**
> AuthProfileResponse updateAuthProfile(id, authProfileRequest)

Update tenant responder-auth profile

Why this endpoint is needed: Identity provider details and claim models change over time; profile updates must be supported without rebuilding tenants.  What this endpoint does: It updates auth profile trust settings, claim mappings, scope behavior, and fallback policies.  How this endpoint does it: The service validates tenant access and mapping constraints, applies secure defaults where required, persists profile changes, and records before/after snapshots in auth audit. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AuthProfilesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AuthProfilesApi apiInstance = new AuthProfilesApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    AuthProfileRequest authProfileRequest = new AuthProfileRequest(); // AuthProfileRequest | 
    try {
      AuthProfileResponse result = apiInstance.updateAuthProfile(id, authProfileRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AuthProfilesApi#updateAuthProfile");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **UUID**|  | |
| **authProfileRequest** | [**AuthProfileRequest**](AuthProfileRequest.md)|  | |

### Return type

[**AuthProfileResponse**](AuthProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Auth profile updated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="validateResponderToken"></a>
# **validateResponderToken**
> TokenValidationResult validateResponderToken(tenantId, body)

Validate responder token against tenant auth policy

Why this endpoint is needed: Integrations and runtime checks require an explicit token-validation endpoint that applies tenant-specific trust policy.  What this endpoint does: It validates responder token input according to configured auth mode and claim-mapping rules, then returns standardized success/failure payload.  How this endpoint does it: Service loads tenant auth profile, routes validation by mode (public/signed/OIDC external trust), applies cryptographic and claim checks, and returns TokenValidationResult. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AuthProfilesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    AuthProfilesApi apiInstance = new AuthProfilesApi(defaultClient);
    String tenantId = "tenantId_example"; // String | 
    String body = "body_example"; // String | 
    try {
      TokenValidationResult result = apiInstance.validateResponderToken(tenantId, body);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AuthProfilesApi#validateResponderToken");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **tenantId** | **String**|  | |
| **body** | **String**|  | [optional] |

### Return type

[**TokenValidationResult**](TokenValidationResult.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: text/plain, application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Token validation result returned |  -  |

