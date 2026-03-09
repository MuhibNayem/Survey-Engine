# \AuditLogsAPI

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**GetPlatformAuditLogs**](AuditLogsAPI.md#GetPlatformAuditLogs) | **Get** /api/v1/admin/superadmin/audit-logs | Query platform-wide audit logs (SUPER_ADMIN)
[**GetTenantAuditLogs**](AuditLogsAPI.md#GetTenantAuditLogs) | **Get** /api/v1/audit-logs | Query tenant-scoped activity logs



## GetPlatformAuditLogs

> PageAuditLogResponse GetPlatformAuditLogs(ctx).Page(page).Size(size).Action(action).EntityType(entityType).TenantId(tenantId).Actor(actor).From(from).To(to).Execute()

Query platform-wide audit logs (SUPER_ADMIN)



### Example

```go
package main

import (
	"context"
	"fmt"
	"os"
    "time"
	openapiclient "github.com/GIT_USER_ID/GIT_REPO_ID"
)

func main() {
	page := int32(56) // int32 |  (optional) (default to 0)
	size := int32(56) // int32 |  (optional) (default to 20)
	action := "action_example" // string |  (optional)
	entityType := "entityType_example" // string |  (optional)
	tenantId := "tenantId_example" // string |  (optional)
	actor := "actor_example" // string |  (optional)
	from := time.Now() // time.Time |  (optional)
	to := time.Now() // time.Time |  (optional)

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.AuditLogsAPI.GetPlatformAuditLogs(context.Background()).Page(page).Size(size).Action(action).EntityType(entityType).TenantId(tenantId).Actor(actor).From(from).To(to).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `AuditLogsAPI.GetPlatformAuditLogs``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `GetPlatformAuditLogs`: PageAuditLogResponse
	fmt.Fprintf(os.Stdout, "Response from `AuditLogsAPI.GetPlatformAuditLogs`: %v\n", resp)
}
```

### Path Parameters



### Other Parameters

Other parameters are passed through a pointer to a apiGetPlatformAuditLogsRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **page** | **int32** |  | [default to 0]
 **size** | **int32** |  | [default to 20]
 **action** | **string** |  | 
 **entityType** | **string** |  | 
 **tenantId** | **string** |  | 
 **actor** | **string** |  | 
 **from** | **time.Time** |  | 
 **to** | **time.Time** |  | 

### Return type

[**PageAuditLogResponse**](PageAuditLogResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetTenantAuditLogs

> PageAuditLogResponse GetTenantAuditLogs(ctx).Page(page).Size(size).Action(action).EntityType(entityType).From(from).To(to).Execute()

Query tenant-scoped activity logs



### Example

```go
package main

import (
	"context"
	"fmt"
	"os"
    "time"
	openapiclient "github.com/GIT_USER_ID/GIT_REPO_ID"
)

func main() {
	page := int32(56) // int32 |  (optional) (default to 0)
	size := int32(56) // int32 |  (optional) (default to 20)
	action := "action_example" // string |  (optional)
	entityType := "entityType_example" // string |  (optional)
	from := time.Now() // time.Time |  (optional)
	to := time.Now() // time.Time |  (optional)

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.AuditLogsAPI.GetTenantAuditLogs(context.Background()).Page(page).Size(size).Action(action).EntityType(entityType).From(from).To(to).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `AuditLogsAPI.GetTenantAuditLogs``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `GetTenantAuditLogs`: PageAuditLogResponse
	fmt.Fprintf(os.Stdout, "Response from `AuditLogsAPI.GetTenantAuditLogs`: %v\n", resp)
}
```

### Path Parameters



### Other Parameters

Other parameters are passed through a pointer to a apiGetTenantAuditLogsRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **page** | **int32** |  | [default to 0]
 **size** | **int32** |  | [default to 20]
 **action** | **string** |  | 
 **entityType** | **string** |  | 
 **from** | **time.Time** |  | 
 **to** | **time.Time** |  | 

### Return type

[**PageAuditLogResponse**](PageAuditLogResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)

