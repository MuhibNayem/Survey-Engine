# openapi_client.PlanCatalogApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**list_plans**](PlanCatalogApi.md#list_plans) | **GET** /api/v1/admin/plans | List active plan definitions
[**upsert_plan**](PlanCatalogApi.md#upsert_plan) | **PUT** /api/v1/admin/plans | Create or update a plan definition (SUPER_ADMIN)


# **list_plans**
> List[PlanDefinitionResponse] list_plans()

List active plan definitions

Why this endpoint is needed:
Tenants and platform operators both need a canonical list of active plans with limits and pricing attributes.

What this endpoint does:
It returns all active plan definitions including quota constraints such as maximum campaigns,
max responses per campaign, and max admin users.

How this endpoint does it:
The plan catalog service reads persistent plan definition rows, filters active plans, maps entities to API DTOs,
and returns a list suitable for onboarding forms and pricing pages.


### Example


```python
import openapi_client
from openapi_client.models.plan_definition_response import PlanDefinitionResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)


# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.PlanCatalogApi(api_client)

    try:
        # List active plan definitions
        api_response = api_instance.list_plans()
        print("The response of PlanCatalogApi->list_plans:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PlanCatalogApi->list_plans: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**List[PlanDefinitionResponse]**](PlanDefinitionResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Active plans returned |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **upsert_plan**
> PlanDefinitionResponse upsert_plan(plan_definition_request)

Create or update a plan definition (SUPER_ADMIN)

Why this endpoint is needed:
Plan governance must evolve over time as business strategy changes. A controlled mutation endpoint is needed
for super administrators to adjust commercial limits safely.

What this endpoint does:
It inserts or updates a plan definition, including display metadata, billing-cycle metadata, and quota parameters.

How this endpoint does it:
The endpoint is protected by super-admin role checks, validates request constraints,
persists the plan catalog mutation, and returns the resulting plan definition used by runtime quota enforcement.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.plan_definition_request import PlanDefinitionRequest
from openapi_client.models.plan_definition_response import PlanDefinitionResponse
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
    api_instance = openapi_client.PlanCatalogApi(api_client)
    plan_definition_request = openapi_client.PlanDefinitionRequest() # PlanDefinitionRequest | 

    try:
        # Create or update a plan definition (SUPER_ADMIN)
        api_response = api_instance.upsert_plan(plan_definition_request)
        print("The response of PlanCatalogApi->upsert_plan:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PlanCatalogApi->upsert_plan: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **plan_definition_request** | [**PlanDefinitionRequest**](PlanDefinitionRequest.md)|  | 

### Return type

[**PlanDefinitionResponse**](PlanDefinitionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Plan upsert completed |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

