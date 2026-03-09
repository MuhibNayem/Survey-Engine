# SubscriptionApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**checkoutSubscription**](SubscriptionApi.md#checkoutsubscription) | **POST** /api/v1/admin/subscriptions/checkout | Start or change tenant subscription plan |
| [**getMySubscription**](SubscriptionApi.md#getmysubscription) | **GET** /api/v1/admin/subscriptions/me | Get current tenant subscription details |



## checkoutSubscription

> SubscriptionResponse checkoutSubscription(subscribeRequest)

Start or change tenant subscription plan

Why this endpoint is needed: SaaS tenants need a direct API path to activate or upgrade plans without out-of-band support workflows.  What this endpoint does: It takes the requested plan, executes the checkout flow (mock success in MVP), updates subscription status, records payment transaction metadata, and returns updated subscription state.  How this endpoint does it: The service uses authenticated tenant context, validates requested plan existence, executes payment gateway abstraction, persists subscription changes and payment reference details, and returns normalized quota-aware response fields. 

### Example

```ts
import {
  Configuration,
  SubscriptionApi,
} from '@survey-engine/sdk';
import type { CheckoutSubscriptionRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new SubscriptionApi(config);

  const body = {
    // SubscribeRequest
    subscribeRequest: ...,
  } satisfies CheckoutSubscriptionRequest;

  try {
    const data = await api.checkoutSubscription(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **subscribeRequest** | [SubscribeRequest](SubscribeRequest.md) |  | |

### Return type

[**SubscriptionResponse**](SubscriptionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Subscription activated or updated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getMySubscription

> SubscriptionResponse getMySubscription()

Get current tenant subscription details

Why this endpoint is needed: Tenant operators must understand their active entitlement before launching campaigns or scaling usage.  What this endpoint does: It returns the authenticated tenant\&#39;s current subscription record, including plan, status, billing period, quota limits, and latest payment reference.  How this endpoint does it: Tenant context is extracted from admin JWT claims, then subscription and plan metadata are joined and mapped into a SubscriptionResponse object for a single, dashboard-friendly read. 

### Example

```ts
import {
  Configuration,
  SubscriptionApi,
} from '@survey-engine/sdk';
import type { GetMySubscriptionRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new SubscriptionApi(config);

  try {
    const data = await api.getMySubscription();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**SubscriptionResponse**](SubscriptionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Subscription details returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **402** | Active subscription is required for this operation |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

