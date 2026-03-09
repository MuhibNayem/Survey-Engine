# \PlanCatalogAPI

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**ListPlans**](PlanCatalogAPI.md#ListPlans) | **Get** /api/v1/admin/plans | List active plan definitions
[**UpsertPlan**](PlanCatalogAPI.md#UpsertPlan) | **Put** /api/v1/admin/plans | Create or update a plan definition (SUPER_ADMIN)



## ListPlans

> []PlanDefinitionResponse ListPlans(ctx).Execute()

List active plan definitions



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
	resp, r, err := apiClient.PlanCatalogAPI.ListPlans(context.Background()).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `PlanCatalogAPI.ListPlans``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `ListPlans`: []PlanDefinitionResponse
	fmt.Fprintf(os.Stdout, "Response from `PlanCatalogAPI.ListPlans`: %v\n", resp)
}
```

### Path Parameters

This endpoint does not need any parameter.

### Other Parameters

Other parameters are passed through a pointer to a apiListPlansRequest struct via the builder pattern


### Return type

[**[]PlanDefinitionResponse**](PlanDefinitionResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## UpsertPlan

> PlanDefinitionResponse UpsertPlan(ctx).PlanDefinitionRequest(planDefinitionRequest).Execute()

Create or update a plan definition (SUPER_ADMIN)



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
	planDefinitionRequest := *openapiclient.NewPlanDefinitionRequest("PlanCode_example", "DisplayName_example", float32(123), "Currency_example", int32(123), int32(123)) // PlanDefinitionRequest | 

	configuration := openapiclient.NewConfiguration()
	apiClient := openapiclient.NewAPIClient(configuration)
	resp, r, err := apiClient.PlanCatalogAPI.UpsertPlan(context.Background()).PlanDefinitionRequest(planDefinitionRequest).Execute()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error when calling `PlanCatalogAPI.UpsertPlan``: %v\n", err)
		fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
	}
	// response from `UpsertPlan`: PlanDefinitionResponse
	fmt.Fprintf(os.Stdout, "Response from `PlanCatalogAPI.UpsertPlan`: %v\n", resp)
}
```

### Path Parameters



### Other Parameters

Other parameters are passed through a pointer to a apiUpsertPlanRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **planDefinitionRequest** | [**PlanDefinitionRequest**](PlanDefinitionRequest.md) |  | 

### Return type

[**PlanDefinitionResponse**](PlanDefinitionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)

