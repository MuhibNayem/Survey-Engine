# UserPreferencesApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getFeatureCompletionStatus**](UserPreferencesApi.md#getfeaturecompletionstatus) | **GET** /api/v1/admin/preferences/{featureKey}/completed | Check whether a feature or onboarding item is completed |
| [**getUserPreferences**](UserPreferencesApi.md#getuserpreferences) | **GET** /api/v1/admin/preferences | Get all preferences for the current admin user |
| [**resetUserPreferences**](UserPreferencesApi.md#resetuserpreferences) | **DELETE** /api/v1/admin/preferences | Reset all preferences for the current admin user |
| [**setFeatureCompletionStatus**](UserPreferencesApi.md#setfeaturecompletionstatus) | **POST** /api/v1/admin/preferences/{featureKey}/complete | Mark a feature or onboarding item as completed or incomplete |
| [**setUserPreference**](UserPreferencesApi.md#setuserpreference) | **PATCH** /api/v1/admin/preferences/{key} | Update one preference key for the current admin user |
| [**updateUserPreferences**](UserPreferencesApi.md#updateuserpreferences) | **PATCH** /api/v1/admin/preferences | Update multiple preferences for the current admin user |



## getFeatureCompletionStatus

> GetFeatureCompletionStatus200Response getFeatureCompletionStatus(featureKey)

Check whether a feature or onboarding item is completed

Why this endpoint is needed: Onboarding and contextual-help UX need a direct completion check for a single feature key.  What this endpoint does: It returns whether the current admin has marked the target feature as completed.  How this endpoint does it: The service resolves the current admin user and reads the completion flag from stored preferences. 

### Example

```ts
import {
  Configuration,
  UserPreferencesApi,
} from '@survey-engine/sdk';
import type { GetFeatureCompletionStatusRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new UserPreferencesApi(config);

  const body = {
    // string
    featureKey: featureKey_example,
  } satisfies GetFeatureCompletionStatusRequest;

  try {
    const data = await api.getFeatureCompletionStatus(body);
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
| **featureKey** | `string` |  | [Defaults to `undefined`] |

### Return type

[**GetFeatureCompletionStatus200Response**](GetFeatureCompletionStatus200Response.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Completion state returned |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getUserPreferences

> UserPreferenceDTO getUserPreferences()

Get all preferences for the current admin user

Why this endpoint is needed: The admin application persists per-user UI state such as theme mode, completed onboarding flows, and dismissed hints.  What this endpoint does: It returns the preference map for the currently authenticated admin.  How this endpoint does it: The service resolves the current admin from auth context, loads the tenant-scoped preference record, and maps it to UserPreferenceDTO. 

### Example

```ts
import {
  Configuration,
  UserPreferencesApi,
} from '@survey-engine/sdk';
import type { GetUserPreferencesRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new UserPreferencesApi(config);

  try {
    const data = await api.getUserPreferences();
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

[**UserPreferenceDTO**](UserPreferenceDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Current user preferences returned |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## resetUserPreferences

> resetUserPreferences()

Reset all preferences for the current admin user

Why this endpoint is needed: Support and onboarding workflows need a clean reset path so tours, theme defaults, and dismissed tips can be replayed.  What this endpoint does: It clears all stored preferences for the current admin user.  How this endpoint does it: The service deletes the current admin preference map and returns success without a body. 

### Example

```ts
import {
  Configuration,
  UserPreferencesApi,
} from '@survey-engine/sdk';
import type { ResetUserPreferencesRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new UserPreferencesApi(config);

  try {
    const data = await api.resetUserPreferences();
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

`void` (Empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Preferences reset |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## setFeatureCompletionStatus

> setFeatureCompletionStatus(featureKey, completed)

Mark a feature or onboarding item as completed or incomplete

Why this endpoint is needed: Guided tours and feature education flows need an explicit server-side completion write path that survives browsers and devices.  What this endpoint does: It stores the completion status for one feature key for the current admin user.  How this endpoint does it: The service updates the completion map in user preferences and returns success without a body. 

### Example

```ts
import {
  Configuration,
  UserPreferencesApi,
} from '@survey-engine/sdk';
import type { SetFeatureCompletionStatusRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new UserPreferencesApi(config);

  const body = {
    // string
    featureKey: featureKey_example,
    // boolean (optional)
    completed: true,
  } satisfies SetFeatureCompletionStatusRequest;

  try {
    const data = await api.setFeatureCompletionStatus(body);
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
| **featureKey** | `string` |  | [Defaults to `undefined`] |
| **completed** | `boolean` |  | [Optional] [Defaults to `true`] |

### Return type

`void` (Empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Completion state updated |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## setUserPreference

> setUserPreference(key, body)

Update one preference key for the current admin user

Why this endpoint is needed: Lightweight UI interactions such as theme changes and dismiss actions often update only one preference key.  What this endpoint does: It sets one preference value for the current admin user.  How this endpoint does it: The controller accepts a raw string body and the service stores it under the requested key. 

### Example

```ts
import {
  Configuration,
  UserPreferencesApi,
} from '@survey-engine/sdk';
import type { SetUserPreferenceRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new UserPreferencesApi(config);

  const body = {
    // string
    key: key_example,
    // string
    body: body_example,
  } satisfies SetUserPreferenceRequest;

  try {
    const data = await api.setUserPreference(body);
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
| **key** | `string` |  | [Defaults to `undefined`] |
| **body** | `string` |  | |

### Return type

`void` (Empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Preference updated |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateUserPreferences

> updateUserPreferences(requestBody)

Update multiple preferences for the current admin user

Why this endpoint is needed: Modern UI flows often save several preference keys together, such as theme mode and onboarding completion checkpoints.  What this endpoint does: It updates multiple string-based preference values in a single call.  How this endpoint does it: The service merges the incoming key-value map into the current admin user\&#39;s preference store and persists the result. 

### Example

```ts
import {
  Configuration,
  UserPreferencesApi,
} from '@survey-engine/sdk';
import type { UpdateUserPreferencesRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new UserPreferencesApi(config);

  const body = {
    // { [key: string]: string; }
    requestBody: Object,
  } satisfies UpdateUserPreferencesRequest;

  try {
    const data = await api.updateUserPreferences(body);
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
| **requestBody** | `{ [key: string]: string; }` |  | |

### Return type

`void` (Empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Preferences updated |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

