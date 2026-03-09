# CategoriesApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createCategory**](CategoriesApi.md#createCategory) | **POST** /api/v1/categories | Create a category grouping for questions |
| [**deactivateCategory**](CategoriesApi.md#deactivateCategory) | **DELETE** /api/v1/categories/{id} | Deactivate a category |
| [**getCategory**](CategoriesApi.md#getCategory) | **GET** /api/v1/categories/{id} | Get one category by id |
| [**listCategories**](CategoriesApi.md#listCategories) | **GET** /api/v1/categories | List active categories |
| [**updateCategory**](CategoriesApi.md#updateCategory) | **PUT** /api/v1/categories/{id} | Update category and mappings |


<a id="createCategory"></a>
# **createCategory**
> CategoryResponse createCategory(categoryRequest)

Create a category grouping for questions

Why this endpoint is needed: Categories group question assets into business dimensions that support structure, analytics, and scoring design.  What this endpoint does: It creates a category with optional question mappings and stores category metadata under tenant ownership.  How this endpoint does it: The service validates category payloads, checks referenced question constraints where needed, persists category + mappings, and returns CategoryResponse with mapping summaries. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CategoriesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CategoriesApi apiInstance = new CategoriesApi(defaultClient);
    CategoryRequest categoryRequest = new CategoryRequest(); // CategoryRequest | 
    try {
      CategoryResponse result = apiInstance.createCategory(categoryRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CategoriesApi#createCategory");
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
| **categoryRequest** | [**CategoryRequest**](CategoryRequest.md)|  | |

### Return type

[**CategoryResponse**](CategoryResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Category created |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="deactivateCategory"></a>
# **deactivateCategory**
> deactivateCategory(id)

Deactivate a category

Why this endpoint is needed: Teams need to retire categories without deleting historical campaign and scoring references.  What this endpoint does: It marks the category as inactive for future authoring while retaining historical records.  How this endpoint does it: The service enforces tenant ownership, flips active status, and returns no content. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CategoriesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CategoriesApi apiInstance = new CategoriesApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.deactivateCategory(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling CategoriesApi#deactivateCategory");
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
| **204** | Category deactivated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="getCategory"></a>
# **getCategory**
> CategoryResponse getCategory(id)

Get one category by id

Why this endpoint is needed: Category detail pages and editor forms need full mapping visibility.  What this endpoint does: It returns a single category record including mapped-question metadata.  How this endpoint does it: The service loads category and mapping state under tenant scope and returns a DTO optimized for UI consumption. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CategoriesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CategoriesApi apiInstance = new CategoriesApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      CategoryResponse result = apiInstance.getCategory(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CategoriesApi#getCategory");
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

[**CategoryResponse**](CategoryResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Category returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="listCategories"></a>
# **listCategories**
> List&lt;CategoryResponse&gt; listCategories()

List active categories

Why this endpoint is needed: Authoring screens need category inventory for survey composition and weighting strategy.  What this endpoint does: It returns active categories available in the authenticated tenant workspace.  How this endpoint does it: The category service applies tenant filters and active-state filters, then maps entity results to response DTOs. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CategoriesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CategoriesApi apiInstance = new CategoriesApi(defaultClient);
    try {
      List<CategoryResponse> result = apiInstance.listCategories();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CategoriesApi#listCategories");
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

[**List&lt;CategoryResponse&gt;**](CategoryResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Categories returned |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="updateCategory"></a>
# **updateCategory**
> CategoryResponse updateCategory(id, categoryRequest)

Update category and mappings

Why this endpoint is needed: Category definitions and ordering can change as survey programs mature.  What this endpoint does: It updates category metadata and mapped-question configuration.  How this endpoint does it: Validation and tenant checks run first, then mapping set updates are persisted and reflected in the response DTO. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CategoriesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CategoriesApi apiInstance = new CategoriesApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    CategoryRequest categoryRequest = new CategoryRequest(); // CategoryRequest | 
    try {
      CategoryResponse result = apiInstance.updateCategory(id, categoryRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CategoriesApi#updateCategory");
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
| **categoryRequest** | [**CategoryRequest**](CategoryRequest.md)|  | |

### Return type

[**CategoryResponse**](CategoryResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Category updated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

