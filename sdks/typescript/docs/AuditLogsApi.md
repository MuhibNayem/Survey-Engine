# AuditLogsApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getPlatformAuditLogs**](AuditLogsApi.md#getplatformauditlogs) | **GET** /api/v1/admin/superadmin/audit-logs | Query platform-wide audit logs (SUPER_ADMIN) |
| [**getTenantAuditLogs**](AuditLogsApi.md#gettenantauditlogs) | **GET** /api/v1/audit-logs | Query tenant-scoped activity logs |



## getPlatformAuditLogs

> PageAuditLogResponse getPlatformAuditLogs(page, size, action, entityType, tenantId, actor, from, to)

Query platform-wide audit logs (SUPER_ADMIN)

Why this endpoint is needed: Super-admin operations require cross-tenant traceability for investigations and compliance.  What this endpoint does: It returns paged audit logs across all tenants with optional filters for actor/action/entity/tenant.  How this endpoint does it: Controller applies optional filter predicates and queries audit repository with descending timestamp ordering. 

### Example

```ts
import {
  Configuration,
  AuditLogsApi,
} from '@survey-engine/sdk';
import type { GetPlatformAuditLogsRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new AuditLogsApi(config);

  const body = {
    // number (optional)
    page: 56,
    // number (optional)
    size: 56,
    // string (optional)
    action: action_example,
    // string (optional)
    entityType: entityType_example,
    // string (optional)
    tenantId: tenantId_example,
    // string (optional)
    actor: actor_example,
    // Date (optional)
    from: 2013-10-20T19:20:30+01:00,
    // Date (optional)
    to: 2013-10-20T19:20:30+01:00,
  } satisfies GetPlatformAuditLogsRequest;

  try {
    const data = await api.getPlatformAuditLogs(body);
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
| **action** | `string` |  | [Optional] [Defaults to `undefined`] |
| **entityType** | `string` |  | [Optional] [Defaults to `undefined`] |
| **tenantId** | `string` |  | [Optional] [Defaults to `undefined`] |
| **actor** | `string` |  | [Optional] [Defaults to `undefined`] |
| **from** | `Date` |  | [Optional] [Defaults to `undefined`] |
| **to** | `Date` |  | [Optional] [Defaults to `undefined`] |

### Return type

[**PageAuditLogResponse**](PageAuditLogResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Platform audit logs returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getTenantAuditLogs

> PageAuditLogResponse getTenantAuditLogs(page, size, action, entityType, from, to)

Query tenant-scoped activity logs

Why this endpoint is needed: Tenant operators need visibility into operational activity for troubleshooting and governance.  What this endpoint does: It returns paged audit logs filtered to the current tenant context.  How this endpoint does it: Controller derives tenant id from security context and applies optional filter parameters on audit-log repository. 

### Example

```ts
import {
  Configuration,
  AuditLogsApi,
} from '@survey-engine/sdk';
import type { GetTenantAuditLogsRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new AuditLogsApi(config);

  const body = {
    // number (optional)
    page: 56,
    // number (optional)
    size: 56,
    // string (optional)
    action: action_example,
    // string (optional)
    entityType: entityType_example,
    // Date (optional)
    from: 2013-10-20T19:20:30+01:00,
    // Date (optional)
    to: 2013-10-20T19:20:30+01:00,
  } satisfies GetTenantAuditLogsRequest;

  try {
    const data = await api.getTenantAuditLogs(body);
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
| **action** | `string` |  | [Optional] [Defaults to `undefined`] |
| **entityType** | `string` |  | [Optional] [Defaults to `undefined`] |
| **from** | `Date` |  | [Optional] [Defaults to `undefined`] |
| **to** | `Date` |  | [Optional] [Defaults to `undefined`] |

### Return type

[**PageAuditLogResponse**](PageAuditLogResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Tenant audit logs returned |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

