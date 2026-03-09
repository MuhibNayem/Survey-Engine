# \OIDCRespondentFlowAPI

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**CompleteRespondentOidc**](OIDCRespondentFlowAPI.md#CompleteRespondentOidc) | **Get** /api/v1/auth/respondent/oidc/callback | Complete OIDC callback and issue one-time responder access code
[**StartRespondentOidc**](OIDCRespondentFlowAPI.md#StartRespondentOidc) | **Post** /api/v1/auth/respondent/oidc/start | Start private responder OIDC flow



## CompleteRespondentOidc

> OidcCallbackResponse CompleteRespondentOidc(ctx).State(state).Code(code).Execute()

Complete OIDC callback and issue one-time responder access code



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
	state := "state_example" // string | 
	code := "code_example" // string | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.OIDCRespondentFlowAPI.CompleteRespondentOidc(context.Background()).State(state).Code(code).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `OIDCRespondentFlowAPI.CompleteRespondentOidc``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `CompleteRespondentOidc`: OidcCallbackResponse
	fmt.Fprintf(os.Stdout, "Response from `OIDCRespondentFlowAPI.CompleteRespondentOidc`: %v\n", resp)
}
```

### Path Parameters



### Other Parameters

Other parameters are passed through a pointer to a apiCompleteRespondentOidcRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **state** | **string** |  | 
 **code** | **string** |  | 

### Return type

[**OidcCallbackResponse**](OidcCallbackResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## StartRespondentOidc

> OidcStartResponse StartRespondentOidc(ctx).OidcStartRequest(oidcStartRequest).Execute()

Start private responder OIDC flow



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
	oidcStartRequest := *openapiclient.NewOidcStartRequest("TenantId_example", "CampaignId_example") // OidcStartRequest | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.OIDCRespondentFlowAPI.StartRespondentOidc(context.Background()).OidcStartRequest(oidcStartRequest).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `OIDCRespondentFlowAPI.StartRespondentOidc``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `StartRespondentOidc`: OidcStartResponse
	fmt.Fprintf(os.Stdout, "Response from `OIDCRespondentFlowAPI.StartRespondentOidc`: %v\n", resp)
}
```

### Path Parameters



### Other Parameters

Other parameters are passed through a pointer to a apiStartRespondentOidcRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **oidcStartRequest** | [**OidcStartRequest**](OidcStartRequest.md) |  | 

### Return type

[**OidcStartResponse**](OidcStartResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)

