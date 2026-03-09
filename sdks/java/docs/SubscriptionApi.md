# SubscriptionApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**checkoutSubscription**](SubscriptionApi.md#checkoutSubscription) | **POST** /api/v1/admin/subscriptions/checkout | Start or change tenant subscription plan |
| [**getMySubscription**](SubscriptionApi.md#getMySubscription) | **GET** /api/v1/admin/subscriptions/me | Get current tenant subscription details |


<a id="checkoutSubscription"></a>
# **checkoutSubscription**
> SubscriptionResponse checkoutSubscription(subscribeRequest)

Start or change tenant subscription plan

Why this endpoint is needed: SaaS tenants need a direct API path to activate or upgrade plans without out-of-band support workflows.  What this endpoint does: It takes the requested plan, executes the checkout flow (mock success in MVP), updates subscription status, records payment transaction metadata, and returns updated subscription state.  How this endpoint does it: The service uses authenticated tenant context, validates requested plan existence, executes payment gateway abstraction, persists subscription changes and payment reference details, and returns normalized quota-aware response fields. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.SubscriptionApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SubscriptionApi apiInstance = new SubscriptionApi(defaultClient);
    SubscribeRequest subscribeRequest = new SubscribeRequest(); // SubscribeRequest | 
    try {
      SubscriptionResponse result = apiInstance.checkoutSubscription(subscribeRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SubscriptionApi#checkoutSubscription");
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
| **subscribeRequest** | [**SubscribeRequest**](SubscribeRequest.md)|  | |

### Return type

[**SubscriptionResponse**](SubscriptionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Subscription activated or updated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="getMySubscription"></a>
# **getMySubscription**
> SubscriptionResponse getMySubscription()

Get current tenant subscription details

Why this endpoint is needed: Tenant operators must understand their active entitlement before launching campaigns or scaling usage.  What this endpoint does: It returns the authenticated tenant&#39;s current subscription record, including plan, status, billing period, quota limits, and latest payment reference.  How this endpoint does it: Tenant context is extracted from admin JWT claims, then subscription and plan metadata are joined and mapped into a SubscriptionResponse object for a single, dashboard-friendly read. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.SubscriptionApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SubscriptionApi apiInstance = new SubscriptionApi(defaultClient);
    try {
      SubscriptionResponse result = apiInstance.getMySubscription();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SubscriptionApi#getMySubscription");
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

[**SubscriptionResponse**](SubscriptionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Subscription details returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **402** | Active subscription is required for this operation |  -  |

