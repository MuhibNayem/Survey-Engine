# openapi_client.SuperAdminApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**activate_tenant**](SuperAdminApi.md#activate_tenant) | **PUT** /api/v1/admin/superadmin/tenants/{tenantId}/activate | Activate a tenant
[**get_super_admin_metrics**](SuperAdminApi.md#get_super_admin_metrics) | **GET** /api/v1/admin/superadmin/metrics | Retrieve platform-level metrics snapshot
[**get_tenant_admin_metrics**](SuperAdminApi.md#get_tenant_admin_metrics) | **GET** /api/v1/admin/superadmin/tenants/metrics | Retrieve platform metrics via tenant-admin namespace
[**impersonate_tenant**](SuperAdminApi.md#impersonate_tenant) | **POST** /api/v1/admin/superadmin/tenants/{tenantId}/impersonate | Impersonate a tenant admin context
[**list_tenants_for_super_admin**](SuperAdminApi.md#list_tenants_for_super_admin) | **GET** /api/v1/admin/superadmin/tenants | List tenants with subscription and status overview
[**override_tenant_subscription**](SuperAdminApi.md#override_tenant_subscription) | **POST** /api/v1/admin/superadmin/tenants/{tenantId}/subscriptions/override | Override tenant subscription plan as super admin
[**suspend_tenant**](SuperAdminApi.md#suspend_tenant) | **PUT** /api/v1/admin/superadmin/tenants/{tenantId}/suspend | Suspend a tenant


# **activate_tenant**
> activate_tenant(tenant_id)

Activate a tenant

### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
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
    api_instance = openapi_client.SuperAdminApi(api_client)
    tenant_id = 'tenant_id_example' # str | 

    try:
        # Activate a tenant
        api_instance.activate_tenant(tenant_id)
    except Exception as e:
        print("Exception when calling SuperAdminApi->activate_tenant: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tenant_id** | **str**|  | 

### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**204** | Tenant activated |  -  |
**401** | Authentication missing or invalid |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_super_admin_metrics**
> SuperAdminMetricsResponse get_super_admin_metrics()

Retrieve platform-level metrics snapshot

Why this endpoint is needed:
Platform operators require fast, aggregate visibility into tenant and subscription health.

What this endpoint does:
It returns key platform counters such as total tenants, active subscriptions, and aggregate usage.

How this endpoint does it:
Super-admin service aggregates metrics from tenant/subscription/usage stores and maps them to response DTO.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.super_admin_metrics_response import SuperAdminMetricsResponse
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
    api_instance = openapi_client.SuperAdminApi(api_client)

    try:
        # Retrieve platform-level metrics snapshot
        api_response = api_instance.get_super_admin_metrics()
        print("The response of SuperAdminApi->get_super_admin_metrics:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SuperAdminApi->get_super_admin_metrics: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**SuperAdminMetricsResponse**](SuperAdminMetricsResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Platform metrics returned |  -  |
**401** | Authentication missing or invalid |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_tenant_admin_metrics**
> SuperAdminMetricsResponse get_tenant_admin_metrics()

Retrieve platform metrics via tenant-admin namespace

Why this endpoint is needed:
Some super-admin UI bundles consume tenant-admin namespace for platform metrics.

What this endpoint does:
It returns the same platform metrics payload as `/api/v1/admin/superadmin/metrics`.

How this endpoint does it:
Controller delegates to the same metrics service used by primary super-admin metrics endpoint.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.super_admin_metrics_response import SuperAdminMetricsResponse
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
    api_instance = openapi_client.SuperAdminApi(api_client)

    try:
        # Retrieve platform metrics via tenant-admin namespace
        api_response = api_instance.get_tenant_admin_metrics()
        print("The response of SuperAdminApi->get_tenant_admin_metrics:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SuperAdminApi->get_tenant_admin_metrics: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**SuperAdminMetricsResponse**](SuperAdminMetricsResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Platform metrics returned |  -  |
**401** | Authentication missing or invalid |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **impersonate_tenant**
> AuthUserResponse impersonate_tenant(tenant_id)

Impersonate a tenant admin context

### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.auth_user_response import AuthUserResponse
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
    api_instance = openapi_client.SuperAdminApi(api_client)
    tenant_id = 'tenant_id_example' # str | 

    try:
        # Impersonate a tenant admin context
        api_response = api_instance.impersonate_tenant(tenant_id)
        print("The response of SuperAdminApi->impersonate_tenant:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SuperAdminApi->impersonate_tenant: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tenant_id** | **str**|  | 

### Return type

[**AuthUserResponse**](AuthUserResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Impersonation session created |  -  |
**401** | Authentication missing or invalid |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_tenants_for_super_admin**
> PageTenantOverviewResponse list_tenants_for_super_admin(page=page, size=size, sort=sort)

List tenants with subscription and status overview

Why this endpoint is needed:
Super-admin workflows require tenant inventory with billing and active-state context for support and governance.

What this endpoint does:
It returns paged tenant overview data including subscription plan and activity state.

How this endpoint does it:
Tenant-admin service queries tenant/subscription aggregates and returns Spring Data paged response structure.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.page_tenant_overview_response import PageTenantOverviewResponse
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
    api_instance = openapi_client.SuperAdminApi(api_client)
    page = 0 # int |  (optional) (default to 0)
    size = 20 # int |  (optional) (default to 20)
    sort = 'sort_example' # str |  (optional)

    try:
        # List tenants with subscription and status overview
        api_response = api_instance.list_tenants_for_super_admin(page=page, size=size, sort=sort)
        print("The response of SuperAdminApi->list_tenants_for_super_admin:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SuperAdminApi->list_tenants_for_super_admin: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **page** | **int**|  | [optional] [default to 0]
 **size** | **int**|  | [optional] [default to 20]
 **sort** | **str**|  | [optional] 

### Return type

[**PageTenantOverviewResponse**](PageTenantOverviewResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Tenant page returned |  -  |
**401** | Authentication missing or invalid |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **override_tenant_subscription**
> override_tenant_subscription(tenant_id, override_subscription_request)

Override tenant subscription plan as super admin

### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.override_subscription_request import OverrideSubscriptionRequest
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
    api_instance = openapi_client.SuperAdminApi(api_client)
    tenant_id = 'tenant_id_example' # str | 
    override_subscription_request = openapi_client.OverrideSubscriptionRequest() # OverrideSubscriptionRequest | 

    try:
        # Override tenant subscription plan as super admin
        api_instance.override_tenant_subscription(tenant_id, override_subscription_request)
    except Exception as e:
        print("Exception when calling SuperAdminApi->override_tenant_subscription: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tenant_id** | **str**|  | 
 **override_subscription_request** | [**OverrideSubscriptionRequest**](OverrideSubscriptionRequest.md)|  | 

### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**204** | Subscription override applied |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **suspend_tenant**
> suspend_tenant(tenant_id)

Suspend a tenant

### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
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
    api_instance = openapi_client.SuperAdminApi(api_client)
    tenant_id = 'tenant_id_example' # str | 

    try:
        # Suspend a tenant
        api_instance.suspend_tenant(tenant_id)
    except Exception as e:
        print("Exception when calling SuperAdminApi->suspend_tenant: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tenant_id** | **str**|  | 

### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**204** | Tenant suspended |  -  |
**401** | Authentication missing or invalid |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

