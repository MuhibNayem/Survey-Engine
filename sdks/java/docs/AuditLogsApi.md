# AuditLogsApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getPlatformAuditLogs**](AuditLogsApi.md#getPlatformAuditLogs) | **GET** /api/v1/admin/superadmin/audit-logs | Query platform-wide audit logs (SUPER_ADMIN) |
| [**getTenantAuditLogs**](AuditLogsApi.md#getTenantAuditLogs) | **GET** /api/v1/audit-logs | Query tenant-scoped activity logs |


<a id="getPlatformAuditLogs"></a>
# **getPlatformAuditLogs**
> PageAuditLogResponse getPlatformAuditLogs(page, size, action, entityType, tenantId, actor, from, to)

Query platform-wide audit logs (SUPER_ADMIN)

Why this endpoint is needed: Super-admin operations require cross-tenant traceability for investigations and compliance.  What this endpoint does: It returns paged audit logs across all tenants with optional filters for actor/action/entity/tenant.  How this endpoint does it: Controller applies optional filter predicates and queries audit repository with descending timestamp ordering. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AuditLogsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AuditLogsApi apiInstance = new AuditLogsApi(defaultClient);
    Integer page = 0; // Integer | 
    Integer size = 20; // Integer | 
    String action = "action_example"; // String | 
    String entityType = "entityType_example"; // String | 
    String tenantId = "tenantId_example"; // String | 
    String actor = "actor_example"; // String | 
    OffsetDateTime from = OffsetDateTime.now(); // OffsetDateTime | 
    OffsetDateTime to = OffsetDateTime.now(); // OffsetDateTime | 
    try {
      PageAuditLogResponse result = apiInstance.getPlatformAuditLogs(page, size, action, entityType, tenantId, actor, from, to);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AuditLogsApi#getPlatformAuditLogs");
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
| **action** | **String**|  | [optional] |
| **entityType** | **String**|  | [optional] |
| **tenantId** | **String**|  | [optional] |
| **actor** | **String**|  | [optional] |
| **from** | **OffsetDateTime**|  | [optional] |
| **to** | **OffsetDateTime**|  | [optional] |

### Return type

[**PageAuditLogResponse**](PageAuditLogResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Platform audit logs returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |

<a id="getTenantAuditLogs"></a>
# **getTenantAuditLogs**
> PageAuditLogResponse getTenantAuditLogs(page, size, action, entityType, from, to)

Query tenant-scoped activity logs

Why this endpoint is needed: Tenant operators need visibility into operational activity for troubleshooting and governance.  What this endpoint does: It returns paged audit logs filtered to the current tenant context.  How this endpoint does it: Controller derives tenant id from security context and applies optional filter parameters on audit-log repository. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AuditLogsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AuditLogsApi apiInstance = new AuditLogsApi(defaultClient);
    Integer page = 0; // Integer | 
    Integer size = 20; // Integer | 
    String action = "action_example"; // String | 
    String entityType = "entityType_example"; // String | 
    OffsetDateTime from = OffsetDateTime.now(); // OffsetDateTime | 
    OffsetDateTime to = OffsetDateTime.now(); // OffsetDateTime | 
    try {
      PageAuditLogResponse result = apiInstance.getTenantAuditLogs(page, size, action, entityType, from, to);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AuditLogsApi#getTenantAuditLogs");
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
| **action** | **String**|  | [optional] |
| **entityType** | **String**|  | [optional] |
| **from** | **OffsetDateTime**|  | [optional] |
| **to** | **OffsetDateTime**|  | [optional] |

### Return type

[**PageAuditLogResponse**](PageAuditLogResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Tenant audit logs returned |  -  |
| **401** | Authentication missing or invalid |  -  |

