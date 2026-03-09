# openapi_client.SubscriptionApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**checkout_subscription**](SubscriptionApi.md#checkout_subscription) | **POST** /api/v1/admin/subscriptions/checkout | Start or change tenant subscription plan
[**get_my_subscription**](SubscriptionApi.md#get_my_subscription) | **GET** /api/v1/admin/subscriptions/me | Get current tenant subscription details


# **checkout_subscription**
> SubscriptionResponse checkout_subscription(subscribe_request)

Start or change tenant subscription plan

Why this endpoint is needed:
SaaS tenants need a direct API path to activate or upgrade plans without out-of-band support workflows.

What this endpoint does:
It takes the requested plan, executes the checkout flow (mock success in MVP), updates subscription status,
records payment transaction metadata, and returns updated subscription state.

How this endpoint does it:
The service uses authenticated tenant context, validates requested plan existence, executes payment gateway abstraction,
persists subscription changes and payment reference details, and returns normalized quota-aware response fields.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.subscribe_request import SubscribeRequest
from openapi_client.models.subscription_response import SubscriptionResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.SubscriptionApi(api_client)
    subscribe_request = openapi_client.SubscribeRequest() # SubscribeRequest | 

    try:
        # Start or change tenant subscription plan
        api_response = api_instance.checkout_subscription(subscribe_request)
        print("The response of SubscriptionApi->checkout_subscription:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SubscriptionApi->checkout_subscription: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **subscribe_request** | [**SubscribeRequest**](SubscribeRequest.md)|  | 

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
**200** | Subscription activated or updated |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_my_subscription**
> SubscriptionResponse get_my_subscription()

Get current tenant subscription details

Why this endpoint is needed:
Tenant operators must understand their active entitlement before launching campaigns or scaling usage.

What this endpoint does:
It returns the authenticated tenant's current subscription record, including plan, status, billing period,
quota limits, and latest payment reference.

How this endpoint does it:
Tenant context is extracted from admin JWT claims, then subscription and plan metadata are joined and mapped into
a SubscriptionResponse object for a single, dashboard-friendly read.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.subscription_response import SubscriptionResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.SubscriptionApi(api_client)

    try:
        # Get current tenant subscription details
        api_response = api_instance.get_my_subscription()
        print("The response of SubscriptionApi->get_my_subscription:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SubscriptionApi->get_my_subscription: %s\n" % e)
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
**200** | Subscription details returned |  -  |
**401** | Authentication missing or invalid |  -  |
**402** | Active subscription is required for this operation |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

