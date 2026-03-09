# OidcRespondentFlowApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**completeRespondentOidc**](OidcRespondentFlowApi.md#completeRespondentOidc) | **GET** /api/v1/auth/respondent/oidc/callback | Complete OIDC callback and issue one-time responder access code |
| [**startRespondentOidc**](OidcRespondentFlowApi.md#startRespondentOidc) | **POST** /api/v1/auth/respondent/oidc/start | Start private responder OIDC flow |


<a id="completeRespondentOidc"></a>
# **completeRespondentOidc**
> OidcCallbackResponse completeRespondentOidc(state, code)

Complete OIDC callback and issue one-time responder access code

Why this endpoint is needed: Authorization-code OIDC requires callback handling to exchange code, verify trust, and convert external auth into internal responder access state.  What this endpoint does: It validates callback state/code, exchanges token at IdP, validates token claims, and issues one-time responder access code.  How this endpoint does it: OIDC state record is checked for expiry and one-time use, token exchange is executed via configured metadata, token validation service verifies claims/mappings, responder access code row is persisted, and either JSON response or redirect response is returned. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.OidcRespondentFlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    OidcRespondentFlowApi apiInstance = new OidcRespondentFlowApi(defaultClient);
    String state = "state_example"; // String | 
    String code = "code_example"; // String | 
    try {
      OidcCallbackResponse result = apiInstance.completeRespondentOidc(state, code);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OidcRespondentFlowApi#completeRespondentOidc");
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
| **state** | **String**|  | |
| **code** | **String**|  | |

### Return type

[**OidcCallbackResponse**](OidcCallbackResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Callback processed and access code returned |  -  |
| **302** | Redirect response emitted when returnPath was provided |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |

<a id="startRespondentOidc"></a>
# **startRespondentOidc**
> OidcStartResponse startRespondentOidc(oidcStartRequest)

Start private responder OIDC flow

Why this endpoint is needed: Private campaigns need a secure and tenant-aware way to initiate responder SSO without exposing platform-internal logic.  What this endpoint does: It validates campaign + tenant eligibility for OIDC flow, creates one-time state/nonce context, and returns IdP authorization URL details.  How this endpoint does it: The OIDC service checks campaign auth mode and tenant auth profile mode, fetches OIDC metadata, stores state with expiry, composes authorization URL with scopes/state/nonce, and returns OidcStartResponse. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.OidcRespondentFlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    OidcRespondentFlowApi apiInstance = new OidcRespondentFlowApi(defaultClient);
    OidcStartRequest oidcStartRequest = new OidcStartRequest(); // OidcStartRequest | 
    try {
      OidcStartResponse result = apiInstance.startRespondentOidc(oidcStartRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OidcRespondentFlowApi#startRespondentOidc");
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
| **oidcStartRequest** | [**OidcStartRequest**](OidcStartRequest.md)|  | |

### Return type

[**OidcStartResponse**](OidcStartResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OIDC authorization URL generated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

