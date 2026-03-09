# \AuthProfilesAPI

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**CreateAuthProfile**](AuthProfilesAPI.md#CreateAuthProfile) | **Post** /api/v1/auth/profiles | Create tenant responder-auth profile
[**GetAuthProfileAudit**](AuthProfilesAPI.md#GetAuthProfileAudit) | **Get** /api/v1/auth/profiles/{id}/audit | Get auth profile audit history
[**GetAuthProfileByTenant**](AuthProfilesAPI.md#GetAuthProfileByTenant) | **Get** /api/v1/auth/profiles/tenant/{tenantId} | Get auth profile for a tenant
[**GetProviderTemplate**](AuthProfilesAPI.md#GetProviderTemplate) | **Get** /api/v1/auth/providers/templates/{providerCode} | Get one provider setup template
[**ListProviderTemplates**](AuthProfilesAPI.md#ListProviderTemplates) | **Get** /api/v1/auth/providers/templates | List built-in provider setup templates
[**RotateAuthProfileKey**](AuthProfilesAPI.md#RotateAuthProfileKey) | **Post** /api/v1/auth/profiles/{id}/rotate-key | Rotate auth profile key version
[**UpdateAuthProfile**](AuthProfilesAPI.md#UpdateAuthProfile) | **Put** /api/v1/auth/profiles/{id} | Update tenant responder-auth profile
[**ValidateResponderToken**](AuthProfilesAPI.md#ValidateResponderToken) | **Post** /api/v1/auth/validate/{tenantId} | Validate responder token against tenant auth policy



## CreateAuthProfile

> AuthProfileResponse CreateAuthProfile(ctx).AuthProfileRequest(authProfileRequest).Execute()

Create tenant responder-auth profile



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
	authProfileRequest := *openapiclient.NewAuthProfileRequest("TenantId_example", "AuthMode_example") // AuthProfileRequest | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.AuthProfilesAPI.CreateAuthProfile(context.Background()).AuthProfileRequest(authProfileRequest).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `AuthProfilesAPI.CreateAuthProfile``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `CreateAuthProfile`: AuthProfileResponse
	fmt.Fprintf(os.Stdout, "Response from `AuthProfilesAPI.CreateAuthProfile`: %v\n", resp)
}
```

### Path Parameters



### Other Parameters

Other parameters are passed through a pointer to a apiCreateAuthProfileRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **authProfileRequest** | [**AuthProfileRequest**](AuthProfileRequest.md) |  | 

### Return type

[**AuthProfileResponse**](AuthProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetAuthProfileAudit

> []AuthConfigAudit GetAuthProfileAudit(ctx, id).Execute()

Get auth profile audit history



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
	resp, r, err := apiClient.AuthProfilesAPI.GetAuthProfileAudit(context.Background(), id).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `AuthProfilesAPI.GetAuthProfileAudit``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `GetAuthProfileAudit`: []AuthConfigAudit
	fmt.Fprintf(os.Stdout, "Response from `AuthProfilesAPI.GetAuthProfileAudit`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**id** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetAuthProfileAuditRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------


### Return type

[**[]AuthConfigAudit**](AuthConfigAudit.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetAuthProfileByTenant

> AuthProfileResponse GetAuthProfileByTenant(ctx, tenantId).Execute()

Get auth profile for a tenant



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
	tenantId := "tenantId_example" // string | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.AuthProfilesAPI.GetAuthProfileByTenant(context.Background(), tenantId).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `AuthProfilesAPI.GetAuthProfileByTenant``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `GetAuthProfileByTenant`: AuthProfileResponse
	fmt.Fprintf(os.Stdout, "Response from `AuthProfilesAPI.GetAuthProfileByTenant`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**tenantId** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetAuthProfileByTenantRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------


### Return type

[**AuthProfileResponse**](AuthProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetProviderTemplate

> ProviderTemplateResponse GetProviderTemplate(ctx, providerCode).Execute()

Get one provider setup template



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
	providerCode := "providerCode_example" // string | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.AuthProfilesAPI.GetProviderTemplate(context.Background(), providerCode).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `AuthProfilesAPI.GetProviderTemplate``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `GetProviderTemplate`: ProviderTemplateResponse
	fmt.Fprintf(os.Stdout, "Response from `AuthProfilesAPI.GetProviderTemplate`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**providerCode** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetProviderTemplateRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------


### Return type

[**ProviderTemplateResponse**](ProviderTemplateResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## ListProviderTemplates

> []ProviderTemplateResponse ListProviderTemplates(ctx).Execute()

List built-in provider setup templates



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
	resp, r, err := apiClient.AuthProfilesAPI.ListProviderTemplates(context.Background()).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `AuthProfilesAPI.ListProviderTemplates``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `ListProviderTemplates`: []ProviderTemplateResponse
	fmt.Fprintf(os.Stdout, "Response from `AuthProfilesAPI.ListProviderTemplates`: %v\n", resp)
}
```

### Path Parameters

This endpoint does not need any parameter.

### Other Parameters

Other parameters are passed through a pointer to a apiListProviderTemplatesRequest struct via the builder pattern


### Return type

[**[]ProviderTemplateResponse**](ProviderTemplateResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## RotateAuthProfileKey

> AuthProfileResponse RotateAuthProfileKey(ctx, id).Execute()

Rotate auth profile key version



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
	resp, r, err := apiClient.AuthProfilesAPI.RotateAuthProfileKey(context.Background(), id).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `AuthProfilesAPI.RotateAuthProfileKey``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `RotateAuthProfileKey`: AuthProfileResponse
	fmt.Fprintf(os.Stdout, "Response from `AuthProfilesAPI.RotateAuthProfileKey`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**id** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiRotateAuthProfileKeyRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------


### Return type

[**AuthProfileResponse**](AuthProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## UpdateAuthProfile

> AuthProfileResponse UpdateAuthProfile(ctx, id).AuthProfileRequest(authProfileRequest).Execute()

Update tenant responder-auth profile



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
	authProfileRequest := *openapiclient.NewAuthProfileRequest("TenantId_example", "AuthMode_example") // AuthProfileRequest | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.AuthProfilesAPI.UpdateAuthProfile(context.Background(), id).AuthProfileRequest(authProfileRequest).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `AuthProfilesAPI.UpdateAuthProfile``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `UpdateAuthProfile`: AuthProfileResponse
	fmt.Fprintf(os.Stdout, "Response from `AuthProfilesAPI.UpdateAuthProfile`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**id** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiUpdateAuthProfileRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------

 **authProfileRequest** | [**AuthProfileRequest**](AuthProfileRequest.md) |  | 

### Return type

[**AuthProfileResponse**](AuthProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## ValidateResponderToken

> TokenValidationResult ValidateResponderToken(ctx, tenantId).Body(body).Execute()

Validate responder token against tenant auth policy



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
	tenantId := "tenantId_example" // string | 
	body := "body_example" // string |  (optional)

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.AuthProfilesAPI.ValidateResponderToken(context.Background(), tenantId).Body(body).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `AuthProfilesAPI.ValidateResponderToken``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `ValidateResponderToken`: TokenValidationResult
	fmt.Fprintf(os.Stdout, "Response from `AuthProfilesAPI.ValidateResponderToken`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**tenantId** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiValidateResponderTokenRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------

 **body** | **string** |  | 

### Return type

[**TokenValidationResult**](TokenValidationResult.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: text/plain, application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)

