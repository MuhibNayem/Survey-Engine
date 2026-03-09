# PlanCatalogApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**listPlans**](PlanCatalogApi.md#listPlans) | **GET** /api/v1/admin/plans | List active plan definitions |
| [**upsertPlan**](PlanCatalogApi.md#upsertPlan) | **PUT** /api/v1/admin/plans | Create or update a plan definition (SUPER_ADMIN) |


<a id="listPlans"></a>
# **listPlans**
> List&lt;PlanDefinitionResponse&gt; listPlans()

List active plan definitions

Why this endpoint is needed: Tenants and platform operators both need a canonical list of active plans with limits and pricing attributes.  What this endpoint does: It returns all active plan definitions including quota constraints such as maximum campaigns, max responses per campaign, and max admin users.  How this endpoint does it: The plan catalog service reads persistent plan definition rows, filters active plans, maps entities to API DTOs, and returns a list suitable for onboarding forms and pricing pages. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PlanCatalogApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    PlanCatalogApi apiInstance = new PlanCatalogApi(defaultClient);
    try {
      List<PlanDefinitionResponse> result = apiInstance.listPlans();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PlanCatalogApi#listPlans");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;PlanDefinitionResponse&gt;**](PlanDefinitionResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Active plans returned |  -  |

<a id="upsertPlan"></a>
# **upsertPlan**
> PlanDefinitionResponse upsertPlan(planDefinitionRequest)

Create or update a plan definition (SUPER_ADMIN)

Why this endpoint is needed: Plan governance must evolve over time as business strategy changes. A controlled mutation endpoint is needed for super administrators to adjust commercial limits safely.  What this endpoint does: It inserts or updates a plan definition, including display metadata, billing-cycle metadata, and quota parameters.  How this endpoint does it: The endpoint is protected by super-admin role checks, validates request constraints, persists the plan catalog mutation, and returns the resulting plan definition used by runtime quota enforcement. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PlanCatalogApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PlanCatalogApi apiInstance = new PlanCatalogApi(defaultClient);
    PlanDefinitionRequest planDefinitionRequest = new PlanDefinitionRequest(); // PlanDefinitionRequest | 
    try {
      PlanDefinitionResponse result = apiInstance.upsertPlan(planDefinitionRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PlanCatalogApi#upsertPlan");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **planDefinitionRequest** | [**PlanDefinitionRequest**](PlanDefinitionRequest.md)|  | |

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
| **200** | Plan upsert completed |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |

