# \UserPreferencesAPI

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**GetFeatureCompletionStatus**](UserPreferencesAPI.md#GetFeatureCompletionStatus) | **Get** /api/v1/admin/preferences/{featureKey}/completed | Check whether a feature or onboarding item is completed
[**GetUserPreferences**](UserPreferencesAPI.md#GetUserPreferences) | **Get** /api/v1/admin/preferences | Get all preferences for the current admin user
[**ResetUserPreferences**](UserPreferencesAPI.md#ResetUserPreferences) | **Delete** /api/v1/admin/preferences | Reset all preferences for the current admin user
[**SetFeatureCompletionStatus**](UserPreferencesAPI.md#SetFeatureCompletionStatus) | **Post** /api/v1/admin/preferences/{featureKey}/complete | Mark a feature or onboarding item as completed or incomplete
[**SetUserPreference**](UserPreferencesAPI.md#SetUserPreference) | **Patch** /api/v1/admin/preferences/{key} | Update one preference key for the current admin user
[**UpdateUserPreferences**](UserPreferencesAPI.md#UpdateUserPreferences) | **Patch** /api/v1/admin/preferences | Update multiple preferences for the current admin user



## GetFeatureCompletionStatus

> GetFeatureCompletionStatus200Response GetFeatureCompletionStatus(ctx, featureKey).Execute()

Check whether a feature or onboarding item is completed



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
	resp, r, err := apiClient.UserPreferencesAPI.GetFeatureCompletionStatus(context.Background(), featureKey).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `UserPreferencesAPI.GetFeatureCompletionStatus``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `GetFeatureCompletionStatus`: GetFeatureCompletionStatus200Response
	fmt.Fprintf(os.Stdout, "Response from `UserPreferencesAPI.GetFeatureCompletionStatus`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**featureKey** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetFeatureCompletionStatusRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------


### Return type

[**GetFeatureCompletionStatus200Response**](GetFeatureCompletionStatus200Response.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetUserPreferences

> UserPreferenceDTO GetUserPreferences(ctx).Execute()

Get all preferences for the current admin user



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

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.UserPreferencesAPI.GetUserPreferences(context.Background()).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `UserPreferencesAPI.GetUserPreferences``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `GetUserPreferences`: UserPreferenceDTO
	fmt.Fprintf(os.Stdout, "Response from `UserPreferencesAPI.GetUserPreferences`: %v\n", resp)
}
```

### Path Parameters

This endpoint does not need any parameter.

### Other Parameters

Other parameters are passed through a pointer to a apiGetUserPreferencesRequest struct via the builder pattern


### Return type

[**UserPreferenceDTO**](UserPreferenceDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## ResetUserPreferences

> ResetUserPreferences(ctx).Execute()

Reset all preferences for the current admin user



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

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	r, err := apiClient.UserPreferencesAPI.ResetUserPreferences(context.Background()).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `UserPreferencesAPI.ResetUserPreferences``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
}
```

### Path Parameters

This endpoint does not need any parameter.

### Other Parameters

Other parameters are passed through a pointer to a apiResetUserPreferencesRequest struct via the builder pattern


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


## SetFeatureCompletionStatus

> SetFeatureCompletionStatus(ctx, featureKey).Completed(completed).Execute()

Mark a feature or onboarding item as completed or incomplete



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
	completed := true // bool |  (optional) (default to true)

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	r, err := apiClient.UserPreferencesAPI.SetFeatureCompletionStatus(context.Background(), featureKey).Completed(completed).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `UserPreferencesAPI.SetFeatureCompletionStatus``: %v\n", err)
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

Other parameters are passed through a pointer to a apiSetFeatureCompletionStatusRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------

 **completed** | **bool** |  | [default to true]

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


## SetUserPreference

> SetUserPreference(ctx, key).Body(body).Execute()

Update one preference key for the current admin user



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
	key := "key_example" // string | 
	body := "body_example" // string | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	r, err := apiClient.UserPreferencesAPI.SetUserPreference(context.Background(), key).Body(body).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `UserPreferencesAPI.SetUserPreference``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**key** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiSetUserPreferenceRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------

 **body** | **string** |  | 

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


## UpdateUserPreferences

> UpdateUserPreferences(ctx).RequestBody(requestBody).Execute()

Update multiple preferences for the current admin user



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
	requestBody := map[string]string{"key": "Inner_example"} // map[string]string | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	r, err := apiClient.UserPreferencesAPI.UpdateUserPreferences(context.Background()).RequestBody(requestBody).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `UserPreferencesAPI.UpdateUserPreferences``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
}
```

### Path Parameters



### Other Parameters

Other parameters are passed through a pointer to a apiUpdateUserPreferencesRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | **map[string]string** |  | 

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

