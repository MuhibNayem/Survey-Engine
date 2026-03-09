# ScoringApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**calculateWeightedScore**](ScoringApi.md#calculateWeightedScore) | **POST** /api/v1/scoring/calculate/{profileId} | Calculate weighted score from raw category scores |
| [**createWeightProfile**](ScoringApi.md#createWeightProfile) | **POST** /api/v1/scoring/profiles | Create scoring weight profile |
| [**deactivateWeightProfile**](ScoringApi.md#deactivateWeightProfile) | **DELETE** /api/v1/scoring/profiles/{id} | Deactivate scoring profile |
| [**getWeightProfile**](ScoringApi.md#getWeightProfile) | **GET** /api/v1/scoring/profiles/{id} | Get one scoring profile by id |
| [**listWeightProfilesByCampaign**](ScoringApi.md#listWeightProfilesByCampaign) | **GET** /api/v1/scoring/profiles/campaign/{campaignId} | List scoring profiles for campaign |
| [**updateWeightProfile**](ScoringApi.md#updateWeightProfile) | **PUT** /api/v1/scoring/profiles/{id} | Update scoring profile |
| [**validateWeightProfile**](ScoringApi.md#validateWeightProfile) | **POST** /api/v1/scoring/profiles/{id}/validate | Validate category weight sum for a profile |


<a id="calculateWeightedScore"></a>
# **calculateWeightedScore**
> ScoreResult calculateWeightedScore(profileId, requestBody)

Calculate weighted score from raw category scores

Why this endpoint is needed: Consumers need deterministic score output from raw category data and configured business weighting policy.  What this endpoint does: It computes normalized category contributions and returns a total weighted score breakdown.  How this endpoint does it: Scoring engine loads profile weights, validates inputs, computes per-category normalized and weighted values, and returns ScoreResult with category details. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ScoringApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ScoringApi apiInstance = new ScoringApi(defaultClient);
    UUID profileId = UUID.randomUUID(); // UUID | 
    Map<String, Double> requestBody = {"0f2069da-4f19-4f4a-a8f7-f2d067e1a5d0":82.5,"12a6a2f0-69da-4f19-9c80-c84b6df67464":71.0}; // Map<String, Double> | 
    try {
      ScoreResult result = apiInstance.calculateWeightedScore(profileId, requestBody);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ScoringApi#calculateWeightedScore");
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
| **profileId** | **UUID**|  | |
| **requestBody** | [**Map&lt;String, Double&gt;**](Double.md)|  | |

### Return type

[**ScoreResult**](ScoreResult.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Score calculated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="createWeightProfile"></a>
# **createWeightProfile**
> WeightProfileResponse createWeightProfile(weightProfileRequest)

Create scoring weight profile

Why this endpoint is needed: Score computation requires explicit category weighting definitions linked to campaign context.  What this endpoint does: It creates a weight profile that defines how raw category scores contribute to total score.  How this endpoint does it: Service validates campaign scope and category-weight payload, persists profile and nested weights, and returns profile representation. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ScoringApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ScoringApi apiInstance = new ScoringApi(defaultClient);
    WeightProfileRequest weightProfileRequest = new WeightProfileRequest(); // WeightProfileRequest | 
    try {
      WeightProfileResponse result = apiInstance.createWeightProfile(weightProfileRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ScoringApi#createWeightProfile");
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
| **weightProfileRequest** | [**WeightProfileRequest**](WeightProfileRequest.md)|  | |

### Return type

[**WeightProfileResponse**](WeightProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Weight profile created |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="deactivateWeightProfile"></a>
# **deactivateWeightProfile**
> deactivateWeightProfile(id)

Deactivate scoring profile

Why this endpoint is needed: Obsolete profiles must be retired while preserving historical result integrity.  What this endpoint does: It marks profile inactive.  How this endpoint does it: Tenant-scoped lookup followed by active-state mutation and no-content response. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ScoringApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ScoringApi apiInstance = new ScoringApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.deactivateWeightProfile(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling ScoringApi#deactivateWeightProfile");
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
| **204** | Weight profile deactivated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="getWeightProfile"></a>
# **getWeightProfile**
> WeightProfileResponse getWeightProfile(id)

Get one scoring profile by id

Why this endpoint is needed: Teams need profile-level visibility for verification and lifecycle updates.  What this endpoint does: It returns one weight profile including category weights.  How this endpoint does it: Service loads profile by id under tenant context and maps to DTO. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ScoringApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ScoringApi apiInstance = new ScoringApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      WeightProfileResponse result = apiInstance.getWeightProfile(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ScoringApi#getWeightProfile");
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

[**WeightProfileResponse**](WeightProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Weight profile returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="listWeightProfilesByCampaign"></a>
# **listWeightProfilesByCampaign**
> List&lt;WeightProfileResponse&gt; listWeightProfilesByCampaign(campaignId)

List scoring profiles for campaign

Why this endpoint is needed: Campaign-level scoring operations need discoverability of attached profiles.  What this endpoint does: It returns all active/in-scope profiles linked to the specified campaign.  How this endpoint does it: Service validates campaign access by tenant and fetches profile rows filtered by campaign id. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ScoringApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ScoringApi apiInstance = new ScoringApi(defaultClient);
    UUID campaignId = UUID.randomUUID(); // UUID | 
    try {
      List<WeightProfileResponse> result = apiInstance.listWeightProfilesByCampaign(campaignId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ScoringApi#listWeightProfilesByCampaign");
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

[**List&lt;WeightProfileResponse&gt;**](WeightProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Profiles returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="updateWeightProfile"></a>
# **updateWeightProfile**
> WeightProfileResponse updateWeightProfile(id, weightProfileRequest)

Update scoring profile

Why this endpoint is needed: Scoring rubrics evolve and profiles require controlled updates.  What this endpoint does: It updates profile metadata and category-weight assignments.  How this endpoint does it: Service validates payload and tenant ownership, persists new values, and returns updated profile DTO. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ScoringApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ScoringApi apiInstance = new ScoringApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    WeightProfileRequest weightProfileRequest = new WeightProfileRequest(); // WeightProfileRequest | 
    try {
      WeightProfileResponse result = apiInstance.updateWeightProfile(id, weightProfileRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ScoringApi#updateWeightProfile");
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
| **weightProfileRequest** | [**WeightProfileRequest**](WeightProfileRequest.md)|  | |

### Return type

[**WeightProfileResponse**](WeightProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Weight profile updated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="validateWeightProfile"></a>
# **validateWeightProfile**
> validateWeightProfile(id)

Validate category weight sum for a profile

Why this endpoint is needed: Invalid weighting definitions can produce misleading score outputs; explicit pre-check endpoint reduces operational mistakes.  What this endpoint does: It validates profile constraints (including total weight sum rule).  How this endpoint does it: Service computes profile totals and throws deterministic business error when constraints are violated. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ScoringApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ScoringApi apiInstance = new ScoringApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.validateWeightProfile(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling ScoringApi#validateWeightProfile");
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
| **200** | Profile is valid |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

