# UserPreferencesApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getFeatureCompletionStatus**](UserPreferencesApi.md#getFeatureCompletionStatus) | **GET** /api/v1/admin/preferences/{featureKey}/completed | Check whether a feature or onboarding item is completed |
| [**getUserPreferences**](UserPreferencesApi.md#getUserPreferences) | **GET** /api/v1/admin/preferences | Get all preferences for the current admin user |
| [**resetUserPreferences**](UserPreferencesApi.md#resetUserPreferences) | **DELETE** /api/v1/admin/preferences | Reset all preferences for the current admin user |
| [**setFeatureCompletionStatus**](UserPreferencesApi.md#setFeatureCompletionStatus) | **POST** /api/v1/admin/preferences/{featureKey}/complete | Mark a feature or onboarding item as completed or incomplete |
| [**setUserPreference**](UserPreferencesApi.md#setUserPreference) | **PATCH** /api/v1/admin/preferences/{key} | Update one preference key for the current admin user |
| [**updateUserPreferences**](UserPreferencesApi.md#updateUserPreferences) | **PATCH** /api/v1/admin/preferences | Update multiple preferences for the current admin user |


<a id="getFeatureCompletionStatus"></a>
# **getFeatureCompletionStatus**
> GetFeatureCompletionStatus200Response getFeatureCompletionStatus(featureKey)

Check whether a feature or onboarding item is completed

Why this endpoint is needed: Onboarding and contextual-help UX need a direct completion check for a single feature key.  What this endpoint does: It returns whether the current admin has marked the target feature as completed.  How this endpoint does it: The service resolves the current admin user and reads the completion flag from stored preferences. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.UserPreferencesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    UserPreferencesApi apiInstance = new UserPreferencesApi(defaultClient);
    String featureKey = "featureKey_example"; // String | 
    try {
      GetFeatureCompletionStatus200Response result = apiInstance.getFeatureCompletionStatus(featureKey);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserPreferencesApi#getFeatureCompletionStatus");
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

[**GetFeatureCompletionStatus200Response**](GetFeatureCompletionStatus200Response.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Completion state returned |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="getUserPreferences"></a>
# **getUserPreferences**
> UserPreferenceDTO getUserPreferences()

Get all preferences for the current admin user

Why this endpoint is needed: The admin application persists per-user UI state such as theme mode, completed onboarding flows, and dismissed hints.  What this endpoint does: It returns the preference map for the currently authenticated admin.  How this endpoint does it: The service resolves the current admin from auth context, loads the tenant-scoped preference record, and maps it to UserPreferenceDTO. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.UserPreferencesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    UserPreferencesApi apiInstance = new UserPreferencesApi(defaultClient);
    try {
      UserPreferenceDTO result = apiInstance.getUserPreferences();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserPreferencesApi#getUserPreferences");
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

[**UserPreferenceDTO**](UserPreferenceDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Current user preferences returned |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="resetUserPreferences"></a>
# **resetUserPreferences**
> resetUserPreferences()

Reset all preferences for the current admin user

Why this endpoint is needed: Support and onboarding workflows need a clean reset path so tours, theme defaults, and dismissed tips can be replayed.  What this endpoint does: It clears all stored preferences for the current admin user.  How this endpoint does it: The service deletes the current admin preference map and returns success without a body. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.UserPreferencesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    UserPreferencesApi apiInstance = new UserPreferencesApi(defaultClient);
    try {
      apiInstance.resetUserPreferences();
    } catch (ApiException e) {
      System.err.println("Exception when calling UserPreferencesApi#resetUserPreferences");
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

null (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Preferences reset |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="setFeatureCompletionStatus"></a>
# **setFeatureCompletionStatus**
> setFeatureCompletionStatus(featureKey, completed)

Mark a feature or onboarding item as completed or incomplete

Why this endpoint is needed: Guided tours and feature education flows need an explicit server-side completion write path that survives browsers and devices.  What this endpoint does: It stores the completion status for one feature key for the current admin user.  How this endpoint does it: The service updates the completion map in user preferences and returns success without a body. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.UserPreferencesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    UserPreferencesApi apiInstance = new UserPreferencesApi(defaultClient);
    String featureKey = "featureKey_example"; // String | 
    Boolean completed = true; // Boolean | 
    try {
      apiInstance.setFeatureCompletionStatus(featureKey, completed);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserPreferencesApi#setFeatureCompletionStatus");
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
| **completed** | **Boolean**|  | [optional] [default to true] |

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
| **200** | Completion state updated |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="setUserPreference"></a>
# **setUserPreference**
> setUserPreference(key, body)

Update one preference key for the current admin user

Why this endpoint is needed: Lightweight UI interactions such as theme changes and dismiss actions often update only one preference key.  What this endpoint does: It sets one preference value for the current admin user.  How this endpoint does it: The controller accepts a raw string body and the service stores it under the requested key. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.UserPreferencesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    UserPreferencesApi apiInstance = new UserPreferencesApi(defaultClient);
    String key = "key_example"; // String | 
    String body = "body_example"; // String | 
    try {
      apiInstance.setUserPreference(key, body);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserPreferencesApi#setUserPreference");
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
| **key** | **String**|  | |
| **body** | **String**|  | |

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
| **200** | Preference updated |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="updateUserPreferences"></a>
# **updateUserPreferences**
> updateUserPreferences(requestBody)

Update multiple preferences for the current admin user

Why this endpoint is needed: Modern UI flows often save several preference keys together, such as theme mode and onboarding completion checkpoints.  What this endpoint does: It updates multiple string-based preference values in a single call.  How this endpoint does it: The service merges the incoming key-value map into the current admin user&#39;s preference store and persists the result. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.UserPreferencesApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    UserPreferencesApi apiInstance = new UserPreferencesApi(defaultClient);
    Map<String, String> requestBody = new HashMap(); // Map<String, String> | 
    try {
      apiInstance.updateUserPreferences(requestBody);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserPreferencesApi#updateUserPreferences");
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
| **requestBody** | [**Map&lt;String, String&gt;**](String.md)|  | |

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
| **200** | Preferences updated |  -  |
| **401** | Authentication missing or invalid |  -  |

