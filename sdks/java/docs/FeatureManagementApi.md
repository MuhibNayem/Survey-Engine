# FeatureManagementApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**configureFeatureForTenant**](FeatureManagementApi.md#configureFeatureForTenant) | **POST** /api/v1/admin/features/{featureKey}/tenants/{tenantId}/configure | Configure feature for a specific tenant |
| [**createFeature**](FeatureManagementApi.md#createFeature) | **POST** /api/v1/admin/features | Create a new feature definition |
| [**deleteFeature**](FeatureManagementApi.md#deleteFeature) | **DELETE** /api/v1/admin/features/{featureKey} | Delete a feature definition |
| [**getFeatureAnalytics**](FeatureManagementApi.md#getFeatureAnalytics) | **GET** /api/v1/admin/features/{featureKey}/analytics | Get usage analytics for a feature |
| [**listFeatures**](FeatureManagementApi.md#listFeatures) | **GET** /api/v1/admin/features | List all feature definitions |
| [**updateFeature**](FeatureManagementApi.md#updateFeature) | **PUT** /api/v1/admin/features/{featureKey} | Update an existing feature definition |


<a id="configureFeatureForTenant"></a>
# **configureFeatureForTenant**
> configureFeatureForTenant(featureKey, tenantId, tenantFeatureConfigDTO)

Configure feature for a specific tenant

Why this endpoint is needed: Enterprise tenants may need to enable/disable features or customize behavior for their user base. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.FeatureManagementApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FeatureManagementApi apiInstance = new FeatureManagementApi(defaultClient);
    String featureKey = "featureKey_example"; // String | 
    String tenantId = "tenantId_example"; // String | 
    TenantFeatureConfigDTO tenantFeatureConfigDTO = new TenantFeatureConfigDTO(); // TenantFeatureConfigDTO | 
    try {
      apiInstance.configureFeatureForTenant(featureKey, tenantId, tenantFeatureConfigDTO);
    } catch (ApiException e) {
      System.err.println("Exception when calling FeatureManagementApi#configureFeatureForTenant");
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
| **featureKey** | **String**|  | |
| **tenantId** | **String**|  | |
| **tenantFeatureConfigDTO** | [**TenantFeatureConfigDTO**](TenantFeatureConfigDTO.md)|  | |

### Return type

null (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Tenant configuration saved |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="createFeature"></a>
# **createFeature**
> FeatureDefinitionDTO createFeature(createFeatureRequest)

Create a new feature definition

Why this endpoint is needed: Super administrators need to register new features in the central registry with metadata, access rules, and rollout configuration. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.FeatureManagementApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FeatureManagementApi apiInstance = new FeatureManagementApi(defaultClient);
    CreateFeatureRequest createFeatureRequest = new CreateFeatureRequest(); // CreateFeatureRequest | 
    try {
      FeatureDefinitionDTO result = apiInstance.createFeature(createFeatureRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FeatureManagementApi#createFeature");
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
| **createFeatureRequest** | [**CreateFeatureRequest**](CreateFeatureRequest.md)|  | |

### Return type

[**FeatureDefinitionDTO**](FeatureDefinitionDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Feature created |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **409** | Requested action conflicts with current resource state |  -  |

<a id="deleteFeature"></a>
# **deleteFeature**
> deleteFeature(featureKey)

Delete a feature definition

Why this endpoint is needed: Retired features need to be removed from the registry to keep the catalog clean. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.FeatureManagementApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FeatureManagementApi apiInstance = new FeatureManagementApi(defaultClient);
    String featureKey = "featureKey_example"; // String | 
    try {
      apiInstance.deleteFeature(featureKey);
    } catch (ApiException e) {
      System.err.println("Exception when calling FeatureManagementApi#deleteFeature");
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
| **featureKey** | **String**|  | |

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
| **204** | Feature deleted |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="getFeatureAnalytics"></a>
# **getFeatureAnalytics**
> FeatureAnalyticsDTO getFeatureAnalytics(featureKey)

Get usage analytics for a feature

Why this endpoint is needed: Product teams need to measure feature adoption, completion rates, and user engagement. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.FeatureManagementApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FeatureManagementApi apiInstance = new FeatureManagementApi(defaultClient);
    String featureKey = "featureKey_example"; // String | 
    try {
      FeatureAnalyticsDTO result = apiInstance.getFeatureAnalytics(featureKey);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FeatureManagementApi#getFeatureAnalytics");
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
| **featureKey** | **String**|  | |

### Return type

[**FeatureAnalyticsDTO**](FeatureAnalyticsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Feature analytics |  -  |

<a id="listFeatures"></a>
# **listFeatures**
> List&lt;FeatureDefinitionDTO&gt; listFeatures(category, type, page, size)

List all feature definitions

Why this endpoint is needed: Super administrators need a centralized view of all registered features including tours, tooltips, banners, and feature flags.  What this endpoint does: Returns paginated list of feature definitions with filtering by category and type. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.FeatureManagementApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FeatureManagementApi apiInstance = new FeatureManagementApi(defaultClient);
    FeatureCategory category = FeatureCategory.fromValue("GENERAL"); // FeatureCategory | 
    FeatureType type = FeatureType.fromValue("TOUR"); // FeatureType | 
    Integer page = 0; // Integer | 
    Integer size = 20; // Integer | 
    try {
      List<FeatureDefinitionDTO> result = apiInstance.listFeatures(category, type, page, size);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FeatureManagementApi#listFeatures");
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
| **category** | [**FeatureCategory**](.md)|  | [optional] [enum: GENERAL, DASHBOARD, SURVEYS, CAMPAIGNS, QUESTIONS, ANALYTICS, RESPONSES, SETTINGS, ADMIN] |
| **type** | [**FeatureType**](.md)|  | [optional] [enum: TOUR, TOOLTIP, BANNER, FEATURE_FLAG, ANNOUNCEMENT] |
| **page** | **Integer**|  | [optional] [default to 0] |
| **size** | **Integer**|  | [optional] [default to 20] |

### Return type

[**List&lt;FeatureDefinitionDTO&gt;**](FeatureDefinitionDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | List of feature definitions |  -  |

<a id="updateFeature"></a>
# **updateFeature**
> FeatureDefinitionDTO updateFeature(featureKey, updateFeatureRequest)

Update an existing feature definition

Why this endpoint is needed: Feature metadata, access rules, and rollout percentages need to be adjustable as product strategy evolves. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.FeatureManagementApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FeatureManagementApi apiInstance = new FeatureManagementApi(defaultClient);
    String featureKey = "featureKey_example"; // String | 
    UpdateFeatureRequest updateFeatureRequest = new UpdateFeatureRequest(); // UpdateFeatureRequest | 
    try {
      FeatureDefinitionDTO result = apiInstance.updateFeature(featureKey, updateFeatureRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FeatureManagementApi#updateFeature");
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
| **featureKey** | **String**|  | |
| **updateFeatureRequest** | [**UpdateFeatureRequest**](UpdateFeatureRequest.md)|  | |

### Return type

[**FeatureDefinitionDTO**](FeatureDefinitionDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Feature updated |  -  |

