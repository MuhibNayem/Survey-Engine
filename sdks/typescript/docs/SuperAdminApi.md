# SuperAdminApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**activateTenant**](SuperAdminApi.md#activatetenant) | **PUT** /api/v1/admin/superadmin/tenants/{tenantId}/activate | Activate a tenant |
| [**getSuperAdminMetrics**](SuperAdminApi.md#getsuperadminmetrics) | **GET** /api/v1/admin/superadmin/metrics | Retrieve platform-level metrics snapshot |
| [**getTenantAdminMetrics**](SuperAdminApi.md#gettenantadminmetrics) | **GET** /api/v1/admin/superadmin/tenants/metrics | Retrieve platform metrics via tenant-admin namespace |
| [**impersonateTenant**](SuperAdminApi.md#impersonatetenant) | **POST** /api/v1/admin/superadmin/tenants/{tenantId}/impersonate | Impersonate a tenant admin context |
| [**listTenantsForSuperAdmin**](SuperAdminApi.md#listtenantsforsuperadmin) | **GET** /api/v1/admin/superadmin/tenants | List tenants with subscription and status overview |
| [**overrideTenantSubscription**](SuperAdminApi.md#overridetenantsubscription) | **POST** /api/v1/admin/superadmin/tenants/{tenantId}/subscriptions/override | Override tenant subscription plan as super admin |
| [**suspendTenant**](SuperAdminApi.md#suspendtenant) | **PUT** /api/v1/admin/superadmin/tenants/{tenantId}/suspend | Suspend a tenant |



## activateTenant

> activateTenant(tenantId)

Activate a tenant

### Example

```ts
import {
  Configuration,
  SuperAdminApi,
} from '@survey-engine/sdk';
import type { ActivateTenantRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new SuperAdminApi(config);

  const body = {
    // string
    tenantId: tenantId_example,
  } satisfies ActivateTenantRequest;

  try {
    const data = await api.activateTenant(body);
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
| **tenantId** | `string` |  | [Defaults to `undefined`] |

### Return type

`void` (Empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Tenant activated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getSuperAdminMetrics

> SuperAdminMetricsResponse getSuperAdminMetrics()

Retrieve platform-level metrics snapshot

Why this endpoint is needed: Platform operators require fast, aggregate visibility into tenant and subscription health.  What this endpoint does: It returns key platform counters such as total tenants, active subscriptions, and aggregate usage.  How this endpoint does it: Super-admin service aggregates metrics from tenant/subscription/usage stores and maps them to response DTO. 

### Example

```ts
import {
  Configuration,
  SuperAdminApi,
} from '@survey-engine/sdk';
import type { GetSuperAdminMetricsRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new SuperAdminApi(config);

  try {
    const data = await api.getSuperAdminMetrics();
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

[**SuperAdminMetricsResponse**](SuperAdminMetricsResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Platform metrics returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getTenantAdminMetrics

> SuperAdminMetricsResponse getTenantAdminMetrics()

Retrieve platform metrics via tenant-admin namespace

Why this endpoint is needed: Some super-admin UI bundles consume tenant-admin namespace for platform metrics.  What this endpoint does: It returns the same platform metrics payload as &#x60;/api/v1/admin/superadmin/metrics&#x60;.  How this endpoint does it: Controller delegates to the same metrics service used by primary super-admin metrics endpoint. 

### Example

```ts
import {
  Configuration,
  SuperAdminApi,
} from '@survey-engine/sdk';
import type { GetTenantAdminMetricsRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new SuperAdminApi(config);

  try {
    const data = await api.getTenantAdminMetrics();
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

[**SuperAdminMetricsResponse**](SuperAdminMetricsResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Platform metrics returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## impersonateTenant

> AuthUserResponse impersonateTenant(tenantId)

Impersonate a tenant admin context

### Example

```ts
import {
  Configuration,
  SuperAdminApi,
} from '@survey-engine/sdk';
import type { ImpersonateTenantRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new SuperAdminApi(config);

  const body = {
    // string
    tenantId: tenantId_example,
  } satisfies ImpersonateTenantRequest;

  try {
    const data = await api.impersonateTenant(body);
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
| **tenantId** | `string` |  | [Defaults to `undefined`] |

### Return type

[**AuthUserResponse**](AuthUserResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Impersonation session created |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listTenantsForSuperAdmin

> PageTenantOverviewResponse listTenantsForSuperAdmin(page, size, sort)

List tenants with subscription and status overview

Why this endpoint is needed: Super-admin workflows require tenant inventory with billing and active-state context for support and governance.  What this endpoint does: It returns paged tenant overview data including subscription plan and activity state.  How this endpoint does it: Tenant-admin service queries tenant/subscription aggregates and returns Spring Data paged response structure. 

### Example

```ts
import {
  Configuration,
  SuperAdminApi,
} from '@survey-engine/sdk';
import type { ListTenantsForSuperAdminRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new SuperAdminApi(config);

  const body = {
    // number (optional)
    page: 56,
    // number (optional)
    size: 56,
    // string (optional)
    sort: sort_example,
  } satisfies ListTenantsForSuperAdminRequest;

  try {
    const data = await api.listTenantsForSuperAdmin(body);
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
| **page** | `number` |  | [Optional] [Defaults to `0`] |
| **size** | `number` |  | [Optional] [Defaults to `20`] |
| **sort** | `string` |  | [Optional] [Defaults to `undefined`] |

### Return type

[**PageTenantOverviewResponse**](PageTenantOverviewResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Tenant page returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## overrideTenantSubscription

> overrideTenantSubscription(tenantId, overrideSubscriptionRequest)

Override tenant subscription plan as super admin

### Example

```ts
import {
  Configuration,
  SuperAdminApi,
} from '@survey-engine/sdk';
import type { OverrideTenantSubscriptionRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new SuperAdminApi(config);

  const body = {
    // string
    tenantId: tenantId_example,
    // OverrideSubscriptionRequest
    overrideSubscriptionRequest: ...,
  } satisfies OverrideTenantSubscriptionRequest;

  try {
    const data = await api.overrideTenantSubscription(body);
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
| **tenantId** | `string` |  | [Defaults to `undefined`] |
| **overrideSubscriptionRequest** | [OverrideSubscriptionRequest](OverrideSubscriptionRequest.md) |  | |

### Return type

`void` (Empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Subscription override applied |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## suspendTenant

> suspendTenant(tenantId)

Suspend a tenant

### Example

```ts
import {
  Configuration,
  SuperAdminApi,
} from '@survey-engine/sdk';
import type { SuspendTenantRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new SuperAdminApi(config);

  const body = {
    // string
    tenantId: tenantId_example,
  } satisfies SuspendTenantRequest;

  try {
    const data = await api.suspendTenant(body);
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
| **tenantId** | `string` |  | [Defaults to `undefined`] |

### Return type

`void` (Empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Tenant suspended |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

