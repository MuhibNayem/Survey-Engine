# UserFeaturesApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**completeFeature**](UserFeaturesApi.md#completefeatureoperation) | **POST** /api/v1/features/{featureKey}/complete | Mark a feature as completed |
| [**getAvailableFeatures**](UserFeaturesApi.md#getavailablefeatures) | **GET** /api/v1/features/available | Get features available to current user |
| [**getFeatureStatus**](UserFeaturesApi.md#getfeaturestatus) | **GET** /api/v1/features/{featureKey}/status | Get feature status for current user |



## completeFeature

> completeFeature(featureKey, completeFeatureRequest)

Mark a feature as completed

Why this endpoint is needed: Users need to record completion of tours, tooltips, or feature interactions for progress tracking. 

### Example

```ts
import {
  Configuration,
  UserFeaturesApi,
} from '@survey-engine/sdk';
import type { CompleteFeatureOperationRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new UserFeaturesApi(config);

  const body = {
    // string
    featureKey: featureKey_example,
    // CompleteFeatureRequest (optional)
    completeFeatureRequest: ...,
  } satisfies CompleteFeatureOperationRequest;

  try {
    const data = await api.completeFeature(body);
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
| **completeFeatureRequest** | [CompleteFeatureRequest](CompleteFeatureRequest.md) |  | [Optional] |

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
| **200** | Feature marked as completed |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getAvailableFeatures

> Array&lt;FeatureStatusDTO&gt; getAvailableFeatures(category)

Get features available to current user

Why this endpoint is needed: Frontend applications need to know which features are available and incomplete for the logged-in user. 

### Example

```ts
import {
  Configuration,
  UserFeaturesApi,
} from '@survey-engine/sdk';
import type { GetAvailableFeaturesRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new UserFeaturesApi(config);

  const body = {
    // FeatureCategory (optional)
    category: ...,
  } satisfies GetAvailableFeaturesRequest;

  try {
    const data = await api.getAvailableFeatures(body);
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
| **category** | `FeatureCategory` |  | [Optional] [Defaults to `undefined`] [Enum: GENERAL, DASHBOARD, SURVEYS, CAMPAIGNS, QUESTIONS, ANALYTICS, RESPONSES, SETTINGS, ADMIN] |

### Return type

[**Array&lt;FeatureStatusDTO&gt;**](FeatureStatusDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | List of available features |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getFeatureStatus

> FeatureStatusDTO getFeatureStatus(featureKey)

Get feature status for current user

Why this endpoint is needed: Frontend needs to check if a specific feature has been completed by the current user. 

### Example

```ts
import {
  Configuration,
  UserFeaturesApi,
} from '@survey-engine/sdk';
import type { GetFeatureStatusRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new UserFeaturesApi(config);

  const body = {
    // string
    featureKey: featureKey_example,
  } satisfies GetFeatureStatusRequest;

  try {
    const data = await api.getFeatureStatus(body);
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

[**FeatureStatusDTO**](FeatureStatusDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Feature status |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

