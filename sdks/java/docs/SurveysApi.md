# SurveysApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createSurvey**](SurveysApi.md#createSurvey) | **POST** /api/v1/surveys | Create a survey draft |
| [**deactivateSurvey**](SurveysApi.md#deactivateSurvey) | **DELETE** /api/v1/surveys/{id} | Deactivate a survey |
| [**getSurvey**](SurveysApi.md#getSurvey) | **GET** /api/v1/surveys/{id} | Get one survey by id |
| [**listSurveys**](SurveysApi.md#listSurveys) | **GET** /api/v1/surveys | List active surveys |
| [**transitionSurveyLifecycle**](SurveysApi.md#transitionSurveyLifecycle) | **POST** /api/v1/surveys/{id}/lifecycle | Transition survey lifecycle state |
| [**updateSurvey**](SurveysApi.md#updateSurvey) | **PUT** /api/v1/surveys/{id} | Update survey draft content |


<a id="createSurvey"></a>
# **createSurvey**
> SurveyResponse createSurvey(surveyRequest)

Create a survey draft

Why this endpoint is needed: Survey lifecycle begins with a draft container where structure can be assembled before publication.  What this endpoint does: It creates a tenant-scoped survey draft with title, description, and page/question structure.  How this endpoint does it: The service validates schema, persists draft state, and returns the survey representation including lifecycle status. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.SurveysApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SurveysApi apiInstance = new SurveysApi(defaultClient);
    SurveyRequest surveyRequest = new SurveyRequest(); // SurveyRequest | 
    try {
      SurveyResponse result = apiInstance.createSurvey(surveyRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SurveysApi#createSurvey");
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
| **surveyRequest** | [**SurveyRequest**](SurveyRequest.md)|  | |

### Return type

[**SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Survey draft created |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="deactivateSurvey"></a>
# **deactivateSurvey**
> deactivateSurvey(id)

Deactivate a survey

Why this endpoint is needed: Organizations need to retire obsolete surveys without physically deleting historical references.  What this endpoint does: It marks the survey inactive.  How this endpoint does it: Tenant ownership is validated and active flag is disabled; endpoint returns no-content on success. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.SurveysApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SurveysApi apiInstance = new SurveysApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.deactivateSurvey(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling SurveysApi#deactivateSurvey");
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
| **id** | **UUID**|  | |

### Return type

null (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Survey deactivated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="getSurvey"></a>
# **getSurvey**
> SurveyResponse getSurvey(id)

Get one survey by id

Why this endpoint is needed: Teams need detailed survey context for editing, publishing decisions, and audits.  What this endpoint does: It returns one survey including pages/questions and lifecycle metadata.  How this endpoint does it: The service resolves survey by id and tenant, then maps nested structures into SurveyResponse. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.SurveysApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SurveysApi apiInstance = new SurveysApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      SurveyResponse result = apiInstance.getSurvey(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SurveysApi#getSurvey");
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
| **id** | **UUID**|  | |

### Return type

[**SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Survey returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="listSurveys"></a>
# **listSurveys**
> List&lt;SurveyResponse&gt; listSurveys()

List active surveys

Why this endpoint is needed: Admin dashboards require an overview of reusable survey assets and lifecycle states.  What this endpoint does: It returns active surveys under current tenant scope.  How this endpoint does it: Survey repository filters by tenant and active state, then maps records to response DTOs. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.SurveysApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SurveysApi apiInstance = new SurveysApi(defaultClient);
    try {
      List<SurveyResponse> result = apiInstance.listSurveys();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SurveysApi#listSurveys");
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

[**List&lt;SurveyResponse&gt;**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Surveys returned |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="transitionSurveyLifecycle"></a>
# **transitionSurveyLifecycle**
> SurveyResponse transitionSurveyLifecycle(id, lifecycleTransitionRequest)

Transition survey lifecycle state

Why this endpoint is needed: Lifecycle transitions are a governance gate that prevents unsafe operations (for example activating campaigns from unpublished surveys).  What this endpoint does: It applies a requested lifecycle transition (such as Draft-&gt;Published or Closed-&gt;Published with reason).  How this endpoint does it: The service checks allowed transition matrix, validates transition requirements, updates lifecycle state, and returns the updated survey representation. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.SurveysApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SurveysApi apiInstance = new SurveysApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    LifecycleTransitionRequest lifecycleTransitionRequest = new LifecycleTransitionRequest(); // LifecycleTransitionRequest | 
    try {
      SurveyResponse result = apiInstance.transitionSurveyLifecycle(id, lifecycleTransitionRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SurveysApi#transitionSurveyLifecycle");
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
| **id** | **UUID**|  | |
| **lifecycleTransitionRequest** | [**LifecycleTransitionRequest**](LifecycleTransitionRequest.md)|  | |

### Return type

[**SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Lifecycle transition applied |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |
| **409** | Requested action conflicts with current resource state |  -  |

<a id="updateSurvey"></a>
# **updateSurvey**
> SurveyResponse updateSurvey(id, surveyRequest)

Update survey draft content

Why this endpoint is needed: Draft surveys evolve before publication and require iterative edits.  What this endpoint does: It updates survey details and structure when lifecycle rules allow modification.  How this endpoint does it: The service validates immutability/lifecycle constraints, persists updates, and returns the updated survey. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.SurveysApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SurveysApi apiInstance = new SurveysApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    SurveyRequest surveyRequest = new SurveyRequest(); // SurveyRequest | 
    try {
      SurveyResponse result = apiInstance.updateSurvey(id, surveyRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SurveysApi#updateSurvey");
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
| **id** | **UUID**|  | |
| **surveyRequest** | [**SurveyRequest**](SurveyRequest.md)|  | |

### Return type

[**SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Survey updated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |
| **409** | Requested action conflicts with current resource state |  -  |

