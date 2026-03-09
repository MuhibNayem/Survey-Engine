# PlanCatalogApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**listPlans**](PlanCatalogApi.md#listplans) | **GET** /api/v1/admin/plans | List active plan definitions |
| [**upsertPlan**](PlanCatalogApi.md#upsertplan) | **PUT** /api/v1/admin/plans | Create or update a plan definition (SUPER_ADMIN) |



## listPlans

> Array&lt;PlanDefinitionResponse&gt; listPlans()

List active plan definitions

Why this endpoint is needed: Tenants and platform operators both need a canonical list of active plans with limits and pricing attributes.  What this endpoint does: It returns all active plan definitions including quota constraints such as maximum campaigns, max responses per campaign, and max admin users.  How this endpoint does it: The plan catalog service reads persistent plan definition rows, filters active plans, maps entities to API DTOs, and returns a list suitable for onboarding forms and pricing pages. 

### Example

```ts
import {
  Configuration,
  PlanCatalogApi,
} from '@survey-engine/sdk';
import type { ListPlansRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const api = new PlanCatalogApi();

  try {
    const data = await api.listPlans();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**Array&lt;PlanDefinitionResponse&gt;**](PlanDefinitionResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Active plans returned |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## upsertPlan

> PlanDefinitionResponse upsertPlan(planDefinitionRequest)

Create or update a plan definition (SUPER_ADMIN)

Why this endpoint is needed: Plan governance must evolve over time as business strategy changes. A controlled mutation endpoint is needed for super administrators to adjust commercial limits safely.  What this endpoint does: It inserts or updates a plan definition, including display metadata, billing-cycle metadata, and quota parameters.  How this endpoint does it: The endpoint is protected by super-admin role checks, validates request constraints, persists the plan catalog mutation, and returns the resulting plan definition used by runtime quota enforcement. 

### Example

```ts
import {
  Configuration,
  PlanCatalogApi,
} from '@survey-engine/sdk';
import type { UpsertPlanRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new PlanCatalogApi(config);

  const body = {
    // PlanDefinitionRequest
    planDefinitionRequest: ...,
  } satisfies UpsertPlanRequest;

  try {
    const data = await api.upsertPlan(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **planDefinitionRequest** | [PlanDefinitionRequest](PlanDefinitionRequest.md) |  | |

### Return type

[**PlanDefinitionResponse**](PlanDefinitionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Plan upsert completed |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

