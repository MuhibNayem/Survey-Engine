# SuperAdminApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**activateTenant**](SuperAdminApi.md#activateTenant) | **PUT** /api/v1/admin/superadmin/tenants/{tenantId}/activate | Activate a tenant |
| [**getSuperAdminMetrics**](SuperAdminApi.md#getSuperAdminMetrics) | **GET** /api/v1/admin/superadmin/metrics | Retrieve platform-level metrics snapshot |
| [**getTenantAdminMetrics**](SuperAdminApi.md#getTenantAdminMetrics) | **GET** /api/v1/admin/superadmin/tenants/metrics | Retrieve platform metrics via tenant-admin namespace |
| [**impersonateTenant**](SuperAdminApi.md#impersonateTenant) | **POST** /api/v1/admin/superadmin/tenants/{tenantId}/impersonate | Impersonate a tenant admin context |
| [**listTenantsForSuperAdmin**](SuperAdminApi.md#listTenantsForSuperAdmin) | **GET** /api/v1/admin/superadmin/tenants | List tenants with subscription and status overview |
| [**overrideTenantSubscription**](SuperAdminApi.md#overrideTenantSubscription) | **POST** /api/v1/admin/superadmin/tenants/{tenantId}/subscriptions/override | Override tenant subscription plan as super admin |
| [**suspendTenant**](SuperAdminApi.md#suspendTenant) | **PUT** /api/v1/admin/superadmin/tenants/{tenantId}/suspend | Suspend a tenant |


<a id="activateTenant"></a>
# **activateTenant**
> activateTenant(tenantId)

Activate a tenant

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.SuperAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SuperAdminApi apiInstance = new SuperAdminApi(defaultClient);
    String tenantId = "tenantId_example"; // String | 
    try {
      apiInstance.activateTenant(tenantId);
    } catch (ApiException e) {
      System.err.println("Exception when calling SuperAdminApi#activateTenant");
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

null (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Tenant activated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="getSuperAdminMetrics"></a>
# **getSuperAdminMetrics**
> SuperAdminMetricsResponse getSuperAdminMetrics()

Retrieve platform-level metrics snapshot

Why this endpoint is needed: Platform operators require fast, aggregate visibility into tenant and subscription health.  What this endpoint does: It returns key platform counters such as total tenants, active subscriptions, and aggregate usage.  How this endpoint does it: Super-admin service aggregates metrics from tenant/subscription/usage stores and maps them to response DTO. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.SuperAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SuperAdminApi apiInstance = new SuperAdminApi(defaultClient);
    try {
      SuperAdminMetricsResponse result = apiInstance.getSuperAdminMetrics();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SuperAdminApi#getSuperAdminMetrics");
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

[**SuperAdminMetricsResponse**](SuperAdminMetricsResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Platform metrics returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |

<a id="getTenantAdminMetrics"></a>
# **getTenantAdminMetrics**
> SuperAdminMetricsResponse getTenantAdminMetrics()

Retrieve platform metrics via tenant-admin namespace

Why this endpoint is needed: Some super-admin UI bundles consume tenant-admin namespace for platform metrics.  What this endpoint does: It returns the same platform metrics payload as &#x60;/api/v1/admin/superadmin/metrics&#x60;.  How this endpoint does it: Controller delegates to the same metrics service used by primary super-admin metrics endpoint. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.SuperAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SuperAdminApi apiInstance = new SuperAdminApi(defaultClient);
    try {
      SuperAdminMetricsResponse result = apiInstance.getTenantAdminMetrics();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SuperAdminApi#getTenantAdminMetrics");
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

[**SuperAdminMetricsResponse**](SuperAdminMetricsResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Platform metrics returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |

<a id="impersonateTenant"></a>
# **impersonateTenant**
> AuthUserResponse impersonateTenant(tenantId)

Impersonate a tenant admin context

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.SuperAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SuperAdminApi apiInstance = new SuperAdminApi(defaultClient);
    String tenantId = "tenantId_example"; // String | 
    try {
      AuthUserResponse result = apiInstance.impersonateTenant(tenantId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SuperAdminApi#impersonateTenant");
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

[**AuthUserResponse**](AuthUserResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Impersonation session created |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="listTenantsForSuperAdmin"></a>
# **listTenantsForSuperAdmin**
> PageTenantOverviewResponse listTenantsForSuperAdmin(page, size, sort)

List tenants with subscription and status overview

Why this endpoint is needed: Super-admin workflows require tenant inventory with billing and active-state context for support and governance.  What this endpoint does: It returns paged tenant overview data including subscription plan and activity state.  How this endpoint does it: Tenant-admin service queries tenant/subscription aggregates and returns Spring Data paged response structure. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.SuperAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SuperAdminApi apiInstance = new SuperAdminApi(defaultClient);
    Integer page = 0; // Integer | 
    Integer size = 20; // Integer | 
    String sort = "sort_example"; // String | 
    try {
      PageTenantOverviewResponse result = apiInstance.listTenantsForSuperAdmin(page, size, sort);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SuperAdminApi#listTenantsForSuperAdmin");
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
| **page** | **Integer**|  | [optional] [default to 0] |
| **size** | **Integer**|  | [optional] [default to 20] |
| **sort** | **String**|  | [optional] |

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
| **200** | Tenant page returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |

<a id="overrideTenantSubscription"></a>
# **overrideTenantSubscription**
> overrideTenantSubscription(tenantId, overrideSubscriptionRequest)

Override tenant subscription plan as super admin

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.SuperAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SuperAdminApi apiInstance = new SuperAdminApi(defaultClient);
    String tenantId = "tenantId_example"; // String | 
    OverrideSubscriptionRequest overrideSubscriptionRequest = new OverrideSubscriptionRequest(); // OverrideSubscriptionRequest | 
    try {
      apiInstance.overrideTenantSubscription(tenantId, overrideSubscriptionRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling SuperAdminApi#overrideTenantSubscription");
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
| **overrideSubscriptionRequest** | [**OverrideSubscriptionRequest**](OverrideSubscriptionRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Subscription override applied |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="suspendTenant"></a>
# **suspendTenant**
> suspendTenant(tenantId)

Suspend a tenant

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.SuperAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SuperAdminApi apiInstance = new SuperAdminApi(defaultClient);
    String tenantId = "tenantId_example"; // String | 
    try {
      apiInstance.suspendTenant(tenantId);
    } catch (ApiException e) {
      System.err.println("Exception when calling SuperAdminApi#suspendTenant");
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

null (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Tenant suspended |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

