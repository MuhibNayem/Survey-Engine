# \UserFeaturesAPI

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**CompleteFeature**](UserFeaturesAPI.md#CompleteFeature) | **Post** /api/v1/features/{featureKey}/complete | Mark a feature as completed
[**GetAvailableFeatures**](UserFeaturesAPI.md#GetAvailableFeatures) | **Get** /api/v1/features/available | Get features available to current user
[**GetFeatureStatus**](UserFeaturesAPI.md#GetFeatureStatus) | **Get** /api/v1/features/{featureKey}/status | Get feature status for current user



## CompleteFeature

> CompleteFeature(ctx, featureKey).CompleteFeatureRequest(completeFeatureRequest).Execute()

Mark a feature as completed



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
	completeFeatureRequest := *openapiclient.NewCompleteFeatureRequest() // CompleteFeatureRequest |  (optional)

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	r, err := apiClient.UserFeaturesAPI.CompleteFeature(context.Background(), featureKey).CompleteFeatureRequest(completeFeatureRequest).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `UserFeaturesAPI.CompleteFeature``: %v\n", err)
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

Other parameters are passed through a pointer to a apiCompleteFeatureRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------

 **completeFeatureRequest** | [**CompleteFeatureRequest**](CompleteFeatureRequest.md) |  | 

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


## GetAvailableFeatures

> []FeatureStatusDTO GetAvailableFeatures(ctx).Category(category).Execute()

Get features available to current user



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

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.UserFeaturesAPI.GetAvailableFeatures(context.Background()).Category(category).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `UserFeaturesAPI.GetAvailableFeatures``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `GetAvailableFeatures`: []FeatureStatusDTO
	fmt.Fprintf(os.Stdout, "Response from `UserFeaturesAPI.GetAvailableFeatures`: %v\n", resp)
}
```

### Path Parameters



### Other Parameters

Other parameters are passed through a pointer to a apiGetAvailableFeaturesRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **category** | [**FeatureCategory**](FeatureCategory.md) |  | 

### Return type

[**[]FeatureStatusDTO**](FeatureStatusDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetFeatureStatus

> FeatureStatusDTO GetFeatureStatus(ctx, featureKey).Execute()

Get feature status for current user



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
	resp, r, err := apiClient.UserFeaturesAPI.GetFeatureStatus(context.Background(), featureKey).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `UserFeaturesAPI.GetFeatureStatus``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `GetFeatureStatus`: FeatureStatusDTO
	fmt.Fprintf(os.Stdout, "Response from `UserFeaturesAPI.GetFeatureStatus`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**featureKey** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetFeatureStatusRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------


### Return type

[**FeatureStatusDTO**](FeatureStatusDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)

