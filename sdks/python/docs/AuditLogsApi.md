# openapi_client.AuditLogsApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_platform_audit_logs**](AuditLogsApi.md#get_platform_audit_logs) | **GET** /api/v1/admin/superadmin/audit-logs | Query platform-wide audit logs (SUPER_ADMIN)
[**get_tenant_audit_logs**](AuditLogsApi.md#get_tenant_audit_logs) | **GET** /api/v1/audit-logs | Query tenant-scoped activity logs


# **get_platform_audit_logs**
> PageAuditLogResponse get_platform_audit_logs(page=page, size=size, action=action, entity_type=entity_type, tenant_id=tenant_id, actor=actor, var_from=var_from, to=to)

Query platform-wide audit logs (SUPER_ADMIN)

Why this endpoint is needed:
Super-admin operations require cross-tenant traceability for investigations and compliance.

What this endpoint does:
It returns paged audit logs across all tenants with optional filters for actor/action/entity/tenant.

How this endpoint does it:
Controller applies optional filter predicates and queries audit repository with descending timestamp ordering.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.page_audit_log_response import PageAuditLogResponse
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
    api_instance = openapi_client.AuditLogsApi(api_client)
    page = 0 # int |  (optional) (default to 0)
    size = 20 # int |  (optional) (default to 20)
    action = 'action_example' # str |  (optional)
    entity_type = 'entity_type_example' # str |  (optional)
    tenant_id = 'tenant_id_example' # str |  (optional)
    actor = 'actor_example' # str |  (optional)
    var_from = '2013-10-20T19:20:30+01:00' # datetime |  (optional)
    to = '2013-10-20T19:20:30+01:00' # datetime |  (optional)

    try:
        # Query platform-wide audit logs (SUPER_ADMIN)
        api_response = api_instance.get_platform_audit_logs(page=page, size=size, action=action, entity_type=entity_type, tenant_id=tenant_id, actor=actor, var_from=var_from, to=to)
        print("The response of AuditLogsApi->get_platform_audit_logs:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuditLogsApi->get_platform_audit_logs: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **page** | **int**|  | [optional] [default to 0]
 **size** | **int**|  | [optional] [default to 20]
 **action** | **str**|  | [optional] 
 **entity_type** | **str**|  | [optional] 
 **tenant_id** | **str**|  | [optional] 
 **actor** | **str**|  | [optional] 
 **var_from** | **datetime**|  | [optional] 
 **to** | **datetime**|  | [optional] 

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
**200** | Platform audit logs returned |  -  |
**401** | Authentication missing or invalid |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_tenant_audit_logs**
> PageAuditLogResponse get_tenant_audit_logs(page=page, size=size, action=action, entity_type=entity_type, var_from=var_from, to=to)

Query tenant-scoped activity logs

Why this endpoint is needed:
Tenant operators need visibility into operational activity for troubleshooting and governance.

What this endpoint does:
It returns paged audit logs filtered to the current tenant context.

How this endpoint does it:
Controller derives tenant id from security context and applies optional filter parameters on audit-log repository.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.page_audit_log_response import PageAuditLogResponse
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
    api_instance = openapi_client.AuditLogsApi(api_client)
    page = 0 # int |  (optional) (default to 0)
    size = 20 # int |  (optional) (default to 20)
    action = 'action_example' # str |  (optional)
    entity_type = 'entity_type_example' # str |  (optional)
    var_from = '2013-10-20T19:20:30+01:00' # datetime |  (optional)
    to = '2013-10-20T19:20:30+01:00' # datetime |  (optional)

    try:
        # Query tenant-scoped activity logs
        api_response = api_instance.get_tenant_audit_logs(page=page, size=size, action=action, entity_type=entity_type, var_from=var_from, to=to)
        print("The response of AuditLogsApi->get_tenant_audit_logs:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuditLogsApi->get_tenant_audit_logs: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **page** | **int**|  | [optional] [default to 0]
 **size** | **int**|  | [optional] [default to 20]
 **action** | **str**|  | [optional] 
 **entity_type** | **str**|  | [optional] 
 **var_from** | **datetime**|  | [optional] 
 **to** | **datetime**|  | [optional] 

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
**200** | Tenant audit logs returned |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

