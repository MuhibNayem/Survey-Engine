# \FeatureManagementAPI

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**ConfigureFeatureForTenant**](FeatureManagementAPI.md#ConfigureFeatureForTenant) | **Post** /api/v1/admin/features/{featureKey}/tenants/{tenantId}/configure | Configure feature for a specific tenant
[**CreateFeature**](FeatureManagementAPI.md#CreateFeature) | **Post** /api/v1/admin/features | Create a new feature definition
[**DeleteFeature**](FeatureManagementAPI.md#DeleteFeature) | **Delete** /api/v1/admin/features/{featureKey} | Delete a feature definition
[**GetFeatureAnalytics**](FeatureManagementAPI.md#GetFeatureAnalytics) | **Get** /api/v1/admin/features/{featureKey}/analytics | Get usage analytics for a feature
[**ListFeatures**](FeatureManagementAPI.md#ListFeatures) | **Get** /api/v1/admin/features | List all feature definitions
[**UpdateFeature**](FeatureManagementAPI.md#UpdateFeature) | **Put** /api/v1/admin/features/{featureKey} | Update an existing feature definition



## ConfigureFeatureForTenant

> ConfigureFeatureForTenant(ctx, featureKey, tenantId).TenantFeatureConfigDTO(tenantFeatureConfigDTO).Execute()

Configure feature for a specific tenant



### Example

```go
package main

import (
	"context"
	"fmt"
	"os"
	openapiclient "github.com/GIT_USER_ID/GIT_REPO_ID"
)

func main() {
	featureKey := "featureKey_example" // string | 
	tenantId := "tenantId_example" // string | 
	tenantFeatureConfigDTO := *openapiclient.NewTenantFeatureConfigDTO() // TenantFeatureConfigDTO | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	r, err := apiClient.FeatureManagementAPI.ConfigureFeatureForTenant(context.Background(), featureKey, tenantId).TenantFeatureConfigDTO(tenantFeatureConfigDTO).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `FeatureManagementAPI.ConfigureFeatureForTenant``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**featureKey** | **string** |  | 
**tenantId** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiConfigureFeatureForTenantRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------


 **tenantFeatureConfigDTO** | [**TenantFeatureConfigDTO**](TenantFeatureConfigDTO.md) |  | 

### Return type

 (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## CreateFeature

> FeatureDefinitionDTO CreateFeature(ctx).CreateFeatureRequest(createFeatureRequest).Execute()

Create a new feature definition



### Example

```go
package main

import (
	"context"
	"fmt"
	"os"
	openapiclient "github.com/GIT_USER_ID/GIT_REPO_ID"
)

func main() {
	createFeatureRequest := *openapiclient.NewCreateFeatureRequest("FeatureKey_example", openapiclient.FeatureType("TOUR"), openapiclient.FeatureCategory("GENERAL"), "Name_example", false) // CreateFeatureRequest | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.FeatureManagementAPI.CreateFeature(context.Background()).CreateFeatureRequest(createFeatureRequest).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `FeatureManagementAPI.CreateFeature``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `CreateFeature`: FeatureDefinitionDTO
	fmt.Fprintf(os.Stdout, "Response from `FeatureManagementAPI.CreateFeature`: %v\n", resp)
}
```

### Path Parameters



### Other Parameters

Other parameters are passed through a pointer to a apiCreateFeatureRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **createFeatureRequest** | [**CreateFeatureRequest**](CreateFeatureRequest.md) |  | 

### Return type

[**FeatureDefinitionDTO**](FeatureDefinitionDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## DeleteFeature

> DeleteFeature(ctx, featureKey).Execute()

Delete a feature definition



### Example

```go
package main

import (
	"context"
	"fmt"
	"os"
	openapiclient "github.com/GIT_USER_ID/GIT_REPO_ID"
)

func main() {
	featureKey := "featureKey_example" // string | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	r, err := apiClient.FeatureManagementAPI.DeleteFeature(context.Background(), featureKey).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `FeatureManagementAPI.DeleteFeature``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**featureKey** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiDeleteFeatureRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------


### Return type

 (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetFeatureAnalytics

> FeatureAnalyticsDTO GetFeatureAnalytics(ctx, featureKey).Execute()

Get usage analytics for a feature



### Example

```go
package main

import (
	"context"
	"fmt"
	"os"
	openapiclient "github.com/GIT_USER_ID/GIT_REPO_ID"
)

func main() {
	featureKey := "featureKey_example" // string | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.FeatureManagementAPI.GetFeatureAnalytics(context.Background(), featureKey).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `FeatureManagementAPI.GetFeatureAnalytics``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `GetFeatureAnalytics`: FeatureAnalyticsDTO
	fmt.Fprintf(os.Stdout, "Response from `FeatureManagementAPI.GetFeatureAnalytics`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**featureKey** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetFeatureAnalyticsRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------


### Return type

[**FeatureAnalyticsDTO**](FeatureAnalyticsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## ListFeatures

> []FeatureDefinitionDTO ListFeatures(ctx).Category(category).Type_(type_).Page(page).Size(size).Execute()

List all feature definitions



### Example

```go
package main

import (
	"context"
	"fmt"
	"os"
	openapiclient "github.com/GIT_USER_ID/GIT_REPO_ID"
)

func main() {
	category := openapiclient.FeatureCategory("GENERAL") // FeatureCategory |  (optional)
	type_ := openapiclient.FeatureType("TOUR") // FeatureType |  (optional)
	page := int32(56) // int32 |  (optional) (default to 0)
	size := int32(56) // int32 |  (optional) (default to 20)

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.FeatureManagementAPI.ListFeatures(context.Background()).Category(category).Type_(type_).Page(page).Size(size).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `FeatureManagementAPI.ListFeatures``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `ListFeatures`: []FeatureDefinitionDTO
	fmt.Fprintf(os.Stdout, "Response from `FeatureManagementAPI.ListFeatures`: %v\n", resp)
}
```

### Path Parameters



### Other Parameters

Other parameters are passed through a pointer to a apiListFeaturesRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **category** | [**FeatureCategory**](FeatureCategory.md) |  | 
 **type_** | [**FeatureType**](FeatureType.md) |  | 
 **page** | **int32** |  | [default to 0]
 **size** | **int32** |  | [default to 20]

### Return type

[**[]FeatureDefinitionDTO**](FeatureDefinitionDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## UpdateFeature

> FeatureDefinitionDTO UpdateFeature(ctx, featureKey).UpdateFeatureRequest(updateFeatureRequest).Execute()

Update an existing feature definition



### Example

```go
package main

import (
	"context"
	"fmt"
	"os"
	openapiclient "github.com/GIT_USER_ID/GIT_REPO_ID"
)

func main() {
	featureKey := "featureKey_example" // string | 
	updateFeatureRequest := *openapiclient.NewUpdateFeatureRequest() // UpdateFeatureRequest | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.FeatureManagementAPI.UpdateFeature(context.Background(), featureKey).UpdateFeatureRequest(updateFeatureRequest).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `FeatureManagementAPI.UpdateFeature``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `UpdateFeature`: FeatureDefinitionDTO
	fmt.Fprintf(os.Stdout, "Response from `FeatureManagementAPI.UpdateFeature`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**featureKey** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiUpdateFeatureRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------

 **updateFeatureRequest** | [**UpdateFeatureRequest**](UpdateFeatureRequest.md) |  | 

### Return type

[**FeatureDefinitionDTO**](FeatureDefinitionDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)

