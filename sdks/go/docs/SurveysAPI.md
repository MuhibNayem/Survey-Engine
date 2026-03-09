# \SurveysAPI

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**CreateSurvey**](SurveysAPI.md#CreateSurvey) | **Post** /api/v1/surveys | Create a survey draft
[**DeactivateSurvey**](SurveysAPI.md#DeactivateSurvey) | **Delete** /api/v1/surveys/{id} | Deactivate a survey
[**GetSurvey**](SurveysAPI.md#GetSurvey) | **Get** /api/v1/surveys/{id} | Get one survey by id
[**ListSurveys**](SurveysAPI.md#ListSurveys) | **Get** /api/v1/surveys | List active surveys
[**TransitionSurveyLifecycle**](SurveysAPI.md#TransitionSurveyLifecycle) | **Post** /api/v1/surveys/{id}/lifecycle | Transition survey lifecycle state
[**UpdateSurvey**](SurveysAPI.md#UpdateSurvey) | **Put** /api/v1/surveys/{id} | Update survey draft content



## CreateSurvey

> SurveyResponse CreateSurvey(ctx).SurveyRequest(surveyRequest).Execute()

Create a survey draft



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
	surveyRequest := *openapiclient.NewSurveyRequest("Title_example") // SurveyRequest | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.SurveysAPI.CreateSurvey(context.Background()).SurveyRequest(surveyRequest).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `SurveysAPI.CreateSurvey``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `CreateSurvey`: SurveyResponse
	fmt.Fprintf(os.Stdout, "Response from `SurveysAPI.CreateSurvey`: %v\n", resp)
}
```

### Path Parameters



### Other Parameters

Other parameters are passed through a pointer to a apiCreateSurveyRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **surveyRequest** | [**SurveyRequest**](SurveyRequest.md) |  | 

### Return type

[**SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## DeactivateSurvey

> DeactivateSurvey(ctx, id).Execute()

Deactivate a survey



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
	id := "38400000-8cf0-11bd-b23e-10b96e4ef00d" // string | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	r, err := apiClient.SurveysAPI.DeactivateSurvey(context.Background(), id).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `SurveysAPI.DeactivateSurvey``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**id** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiDeactivateSurveyRequest struct via the builder pattern


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


## GetSurvey

> SurveyResponse GetSurvey(ctx, id).Execute()

Get one survey by id



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
	id := "38400000-8cf0-11bd-b23e-10b96e4ef00d" // string | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.SurveysAPI.GetSurvey(context.Background(), id).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `SurveysAPI.GetSurvey``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `GetSurvey`: SurveyResponse
	fmt.Fprintf(os.Stdout, "Response from `SurveysAPI.GetSurvey`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**id** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetSurveyRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------


### Return type

[**SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## ListSurveys

> []SurveyResponse ListSurveys(ctx).Execute()

List active surveys



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
	resp, r, err := apiClient.SurveysAPI.ListSurveys(context.Background()).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `SurveysAPI.ListSurveys``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `ListSurveys`: []SurveyResponse
	fmt.Fprintf(os.Stdout, "Response from `SurveysAPI.ListSurveys`: %v\n", resp)
}
```

### Path Parameters

This endpoint does not need any parameter.

### Other Parameters

Other parameters are passed through a pointer to a apiListSurveysRequest struct via the builder pattern


### Return type

[**[]SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## TransitionSurveyLifecycle

> SurveyResponse TransitionSurveyLifecycle(ctx, id).LifecycleTransitionRequest(lifecycleTransitionRequest).Execute()

Transition survey lifecycle state



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
	id := "38400000-8cf0-11bd-b23e-10b96e4ef00d" // string | 
	lifecycleTransitionRequest := *openapiclient.NewLifecycleTransitionRequest("TargetState_example") // LifecycleTransitionRequest | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.SurveysAPI.TransitionSurveyLifecycle(context.Background(), id).LifecycleTransitionRequest(lifecycleTransitionRequest).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `SurveysAPI.TransitionSurveyLifecycle``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `TransitionSurveyLifecycle`: SurveyResponse
	fmt.Fprintf(os.Stdout, "Response from `SurveysAPI.TransitionSurveyLifecycle`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**id** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiTransitionSurveyLifecycleRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------

 **lifecycleTransitionRequest** | [**LifecycleTransitionRequest**](LifecycleTransitionRequest.md) |  | 

### Return type

[**SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## UpdateSurvey

> SurveyResponse UpdateSurvey(ctx, id).SurveyRequest(surveyRequest).Execute()

Update survey draft content



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
	id := "38400000-8cf0-11bd-b23e-10b96e4ef00d" // string | 
	surveyRequest := *openapiclient.NewSurveyRequest("Title_example") // SurveyRequest | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.SurveysAPI.UpdateSurvey(context.Background(), id).SurveyRequest(surveyRequest).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `SurveysAPI.UpdateSurvey``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `UpdateSurvey`: SurveyResponse
	fmt.Fprintf(os.Stdout, "Response from `SurveysAPI.UpdateSurvey`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**id** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiUpdateSurveyRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------

 **surveyRequest** | [**SurveyRequest**](SurveyRequest.md) |  | 

### Return type

[**SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)

