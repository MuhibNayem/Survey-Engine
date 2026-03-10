# UserFeaturesApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**completeFeature**](UserFeaturesApi.md#completeFeature) | **POST** /api/v1/features/{featureKey}/complete | Mark a feature as completed |
| [**getAvailableFeatures**](UserFeaturesApi.md#getAvailableFeatures) | **GET** /api/v1/features/available | Get features available to current user |
| [**getFeatureStatus**](UserFeaturesApi.md#getFeatureStatus) | **GET** /api/v1/features/{featureKey}/status | Get feature status for current user |


<a id="completeFeature"></a>
# **completeFeature**
> completeFeature(featureKey, completeFeatureRequest)

Mark a feature as completed

Why this endpoint is needed: Users need to record completion of tours, tooltips, or feature interactions for progress tracking. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.UserFeaturesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    UserFeaturesApi apiInstance = new UserFeaturesApi(defaultClient);
    String featureKey = "featureKey_example"; // String | 
    CompleteFeatureRequest completeFeatureRequest = new CompleteFeatureRequest(); // CompleteFeatureRequest | 
    try {
      apiInstance.completeFeature(featureKey, completeFeatureRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserFeaturesApi#completeFeature");
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
| **completeFeatureRequest** | [**CompleteFeatureRequest**](CompleteFeatureRequest.md)|  | [optional] |

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
| **200** | Feature marked as completed |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="getAvailableFeatures"></a>
# **getAvailableFeatures**
> List&lt;FeatureStatusDTO&gt; getAvailableFeatures(category)

Get features available to current user

Why this endpoint is needed: Frontend applications need to know which features are available and incomplete for the logged-in user. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.UserFeaturesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    UserFeaturesApi apiInstance = new UserFeaturesApi(defaultClient);
    FeatureCategory category = FeatureCategory.fromValue("GENERAL"); // FeatureCategory | 
    try {
      List<FeatureStatusDTO> result = apiInstance.getAvailableFeatures(category);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserFeaturesApi#getAvailableFeatures");
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

### Return type

[**List&lt;FeatureStatusDTO&gt;**](FeatureStatusDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | List of available features |  -  |

<a id="getFeatureStatus"></a>
# **getFeatureStatus**
> FeatureStatusDTO getFeatureStatus(featureKey)

Get feature status for current user

Why this endpoint is needed: Frontend needs to check if a specific feature has been completed by the current user. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.UserFeaturesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    UserFeaturesApi apiInstance = new UserFeaturesApi(defaultClient);
    String featureKey = "featureKey_example"; // String | 
    try {
      FeatureStatusDTO result = apiInstance.getFeatureStatus(featureKey);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserFeaturesApi#getFeatureStatus");
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

[**FeatureStatusDTO**](FeatureStatusDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Feature status |  -  |

