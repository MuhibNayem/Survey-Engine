# ResponsesApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**compareSegments**](ResponsesApi.md#compareSegments) | **POST** /api/v1/analytics/campaigns/{campaignId}/compare | Compare multiple analytics segments |
| [**getCampaignAnalytics**](ResponsesApi.md#getCampaignAnalytics) | **GET** /api/v1/responses/analytics/{campaignId} | Get aggregated analytics for a campaign |
| [**getFullReport**](ResponsesApi.md#getFullReport) | **GET** /api/v1/analytics/campaigns/{campaignId}/full-report | Get full analytics report with metadata filters |
| [**getResponse**](ResponsesApi.md#getResponse) | **GET** /api/v1/responses/{id} | Get one response by id |
| [**listResponsesByCampaign**](ResponsesApi.md#listResponsesByCampaign) | **GET** /api/v1/responses/campaign/{campaignId} | List responses for a campaign |
| [**lockResponse**](ResponsesApi.md#lockResponse) | **POST** /api/v1/responses/{id}/lock | Lock a response manually |
| [**reopenResponse**](ResponsesApi.md#reopenResponse) | **POST** /api/v1/responses/{id}/reopen | Reopen a locked response with reason |
| [**submitResponse**](ResponsesApi.md#submitResponse) | **POST** /api/v1/responses | Submit survey response (public and private campaign entry) |


<a id="compareSegments"></a>
# **compareSegments**
> ComparisonAnalyticsResponse compareSegments(campaignId, body)

Compare multiple analytics segments

Why this endpoint is needed: Platform users need side-by-side comparative analytics of demographic segments.  What this endpoint does: Multiplexes the /full-report generation across up to 5 concurrent demographic segments. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ResponsesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ResponsesApi apiInstance = new ResponsesApi(defaultClient);
    UUID campaignId = UUID.randomUUID(); // UUID | 
    ComparisonRequest body = new ComparisonRequest(); // ComparisonRequest | 
    try {
      ComparisonAnalyticsResponse result = apiInstance.compareSegments(campaignId, body);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ResponsesApi#compareSegments");
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
| **campaignId** | **UUID**|  | |
| **body** | **ComparisonRequest**|  | |

### Return type

[**ComparisonAnalyticsResponse**](ComparisonAnalyticsResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Multi-segment comparison matrix returned |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="getCampaignAnalytics"></a>
# **getCampaignAnalytics**
> CampaignAnalytics getCampaignAnalytics(campaignId)

Get aggregated analytics for a campaign

Why this endpoint is needed: Campaign operators need near-real-time aggregate visibility without reading raw response payloads.  What this endpoint does: It returns aggregate counts and completion rate metrics for a campaign.  How this endpoint does it: Analytics service runs campaign-scoped aggregate queries under tenant context and returns CampaignAnalytics DTO. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ResponsesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ResponsesApi apiInstance = new ResponsesApi(defaultClient);
    UUID campaignId = UUID.randomUUID(); // UUID | 
    try {
      CampaignAnalytics result = apiInstance.getCampaignAnalytics(campaignId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ResponsesApi#getCampaignAnalytics");
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
| **campaignId** | **UUID**|  | |

### Return type

[**CampaignAnalytics**](CampaignAnalytics.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign analytics returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="getFullReport"></a>
# **getFullReport**
> FullCampaignAnalyticsResponse getFullReport(campaignId)

Get full analytics report with metadata filters

Why this endpoint is needed: The dashboard needs an aggregated snapshot of campaign performance, score curves, and individual question breakdowns.  What this endpoint does: Returns all analytics metrics for a single segment (or the entire campaign). 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ResponsesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ResponsesApi apiInstance = new ResponsesApi(defaultClient);
    UUID campaignId = UUID.randomUUID(); // UUID | 
    try {
      FullCampaignAnalyticsResponse result = apiInstance.getFullReport(campaignId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ResponsesApi#getFullReport");
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
| **campaignId** | **UUID**|  | |

### Return type

[**FullCampaignAnalyticsResponse**](FullCampaignAnalyticsResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Full campaign analytics payload returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="getResponse"></a>
# **getResponse**
> SurveyResponseResponse getResponse(id)

Get one response by id

Why this endpoint is needed: Support and operational teams need direct access to a specific response record for verification and exception handling.  What this endpoint does: It returns one response and its answer details.  How this endpoint does it: Response service resolves response by id under tenant context and maps nested answer records to DTO. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ResponsesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ResponsesApi apiInstance = new ResponsesApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      SurveyResponseResponse result = apiInstance.getResponse(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ResponsesApi#getResponse");
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

[**SurveyResponseResponse**](SurveyResponseResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Response returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="listResponsesByCampaign"></a>
# **listResponsesByCampaign**
> List&lt;SurveyResponseResponse&gt; listResponsesByCampaign(campaignId)

List responses for a campaign

Why this endpoint is needed: Campaign managers need a scoped list view of responses for review, quality checks, and manual workflows.  What this endpoint does: It returns campaign-scoped response list under tenant ownership checks.  How this endpoint does it: The service validates campaign accessibility under tenant context and fetches mapped response rows by campaign id. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ResponsesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ResponsesApi apiInstance = new ResponsesApi(defaultClient);
    UUID campaignId = UUID.randomUUID(); // UUID | 
    try {
      List<SurveyResponseResponse> result = apiInstance.listResponsesByCampaign(campaignId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ResponsesApi#listResponsesByCampaign");
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
| **campaignId** | **UUID**|  | |

### Return type

[**List&lt;SurveyResponseResponse&gt;**](SurveyResponseResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign responses returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="lockResponse"></a>
# **lockResponse**
> SurveyResponseResponse lockResponse(id)

Lock a response manually

Why this endpoint is needed: Although responses auto-lock on submit, operations teams may need explicit lock controls for exceptional workflows.  What this endpoint does: It changes response state to locked and records lock timestamp.  How this endpoint does it: Locking service validates current state and tenant ownership, applies status mutation, and returns updated response. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ResponsesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ResponsesApi apiInstance = new ResponsesApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      SurveyResponseResponse result = apiInstance.lockResponse(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ResponsesApi#lockResponse");
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

[**SurveyResponseResponse**](SurveyResponseResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Response locked |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |
| **409** | Requested action conflicts with current resource state |  -  |

<a id="reopenResponse"></a>
# **reopenResponse**
> SurveyResponseResponse reopenResponse(id, reopenRequest)

Reopen a locked response with reason

Why this endpoint is needed: Real operations require a controlled exception path for corrections after lock.  What this endpoint does: It reopens a previously locked response and captures reason/window metadata.  How this endpoint does it: Locking service validates state transitions and reason constraints, updates response status, and returns reopened response record. If campaign close date has passed, close-time lock enforcement may lock it again. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ResponsesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ResponsesApi apiInstance = new ResponsesApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    ReopenRequest reopenRequest = new ReopenRequest(); // ReopenRequest | 
    try {
      SurveyResponseResponse result = apiInstance.reopenResponse(id, reopenRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ResponsesApi#reopenResponse");
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
| **reopenRequest** | [**ReopenRequest**](ReopenRequest.md)|  | |

### Return type

[**SurveyResponseResponse**](SurveyResponseResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Response reopened |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="submitResponse"></a>
# **submitResponse**
> SurveyResponseResponse submitResponse(responseSubmissionRequest)

Submit survey response (public and private campaign entry)

Why this endpoint is needed: This is the central data-ingestion endpoint where responder participation is captured.  What this endpoint does: It accepts answer payload for a campaign, enforces campaign status and runtime controls, enforces private-auth requirements where applicable, validates against pinned snapshot versions, auto-calculates weighted score when default profile exists, stores response and answers, then auto-locks response.  How this endpoint does it: The service performs campaign lookup, access-mode enforcement (public or private), credential validation (responderToken or responderAccessCode for private), settings checks (quota/restrictions/timeouts), snapshot-based answer validation/scoring, persistence, and status transition to LOCKED before returning SurveyResponseResponse. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ResponsesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    ResponsesApi apiInstance = new ResponsesApi(defaultClient);
    ResponseSubmissionRequest responseSubmissionRequest = new ResponseSubmissionRequest(); // ResponseSubmissionRequest | 
    try {
      SurveyResponseResponse result = apiInstance.submitResponse(responseSubmissionRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ResponsesApi#submitResponse");
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
| **responseSubmissionRequest** | [**ResponseSubmissionRequest**](ResponseSubmissionRequest.md)|  | |

### Return type

[**SurveyResponseResponse**](SurveyResponseResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Response accepted and locked |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |
| **429** | Quota or throttling rule prevented execution |  -  |

