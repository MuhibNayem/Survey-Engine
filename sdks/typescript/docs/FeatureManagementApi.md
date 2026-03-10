# FeatureManagementApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**configureFeatureForTenant**](FeatureManagementApi.md#configurefeaturefortenant) | **POST** /api/v1/admin/features/{featureKey}/tenants/{tenantId}/configure | Configure feature for a specific tenant |
| [**createFeature**](FeatureManagementApi.md#createfeatureoperation) | **POST** /api/v1/admin/features | Create a new feature definition |
| [**deleteFeature**](FeatureManagementApi.md#deletefeature) | **DELETE** /api/v1/admin/features/{featureKey} | Delete a feature definition |
| [**getFeatureAnalytics**](FeatureManagementApi.md#getfeatureanalytics) | **GET** /api/v1/admin/features/{featureKey}/analytics | Get usage analytics for a feature |
| [**listFeatures**](FeatureManagementApi.md#listfeatures) | **GET** /api/v1/admin/features | List all feature definitions |
| [**updateFeature**](FeatureManagementApi.md#updatefeatureoperation) | **PUT** /api/v1/admin/features/{featureKey} | Update an existing feature definition |



## configureFeatureForTenant

> configureFeatureForTenant(featureKey, tenantId, tenantFeatureConfigDTO)

Configure feature for a specific tenant

Why this endpoint is needed: Enterprise tenants may need to enable/disable features or customize behavior for their user base. 

### Example

```ts
import {
  Configuration,
  FeatureManagementApi,
} from '@survey-engine/sdk';
import type { ConfigureFeatureForTenantRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new FeatureManagementApi(config);

  const body = {
    // string
    featureKey: featureKey_example,
    // string
    tenantId: tenantId_example,
    // TenantFeatureConfigDTO
    tenantFeatureConfigDTO: ...,
  } satisfies ConfigureFeatureForTenantRequest;

  try {
    const data = await api.configureFeatureForTenant(body);
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
| **tenantId** | `string` |  | [Defaults to `undefined`] |
| **tenantFeatureConfigDTO** | [TenantFeatureConfigDTO](TenantFeatureConfigDTO.md) |  | |

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
| **200** | Tenant configuration saved |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## createFeature

> FeatureDefinitionDTO createFeature(createFeatureRequest)

Create a new feature definition

Why this endpoint is needed: Super administrators need to register new features in the central registry with metadata, access rules, and rollout configuration. 

### Example

```ts
import {
  Configuration,
  FeatureManagementApi,
} from '@survey-engine/sdk';
import type { CreateFeatureOperationRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new FeatureManagementApi(config);

  const body = {
    // CreateFeatureRequest
    createFeatureRequest: ...,
  } satisfies CreateFeatureOperationRequest;

  try {
    const data = await api.createFeature(body);
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
| **createFeatureRequest** | [CreateFeatureRequest](CreateFeatureRequest.md) |  | |

### Return type

[**FeatureDefinitionDTO**](FeatureDefinitionDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Feature created |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **409** | Requested action conflicts with current resource state |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## deleteFeature

> deleteFeature(featureKey)

Delete a feature definition

Why this endpoint is needed: Retired features need to be removed from the registry to keep the catalog clean. 

### Example

```ts
import {
  Configuration,
  FeatureManagementApi,
} from '@survey-engine/sdk';
import type { DeleteFeatureRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new FeatureManagementApi(config);

  const body = {
    // string
    featureKey: featureKey_example,
  } satisfies DeleteFeatureRequest;

  try {
    const data = await api.deleteFeature(body);
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

`void` (Empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Feature deleted |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getFeatureAnalytics

> FeatureAnalyticsDTO getFeatureAnalytics(featureKey)

Get usage analytics for a feature

Why this endpoint is needed: Product teams need to measure feature adoption, completion rates, and user engagement. 

### Example

```ts
import {
  Configuration,
  FeatureManagementApi,
} from '@survey-engine/sdk';
import type { GetFeatureAnalyticsRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new FeatureManagementApi(config);

  const body = {
    // string
    featureKey: featureKey_example,
  } satisfies GetFeatureAnalyticsRequest;

  try {
    const data = await api.getFeatureAnalytics(body);
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

[**FeatureAnalyticsDTO**](FeatureAnalyticsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Feature analytics |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listFeatures

> Array&lt;FeatureDefinitionDTO&gt; listFeatures(category, type, page, size)

List all feature definitions

Why this endpoint is needed: Super administrators need a centralized view of all registered features including tours, tooltips, banners, and feature flags.  What this endpoint does: Returns paginated list of feature definitions with filtering by category and type. 

### Example

```ts
import {
  Configuration,
  FeatureManagementApi,
} from '@survey-engine/sdk';
import type { ListFeaturesRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new FeatureManagementApi(config);

  const body = {
    // FeatureCategory (optional)
    category: ...,
    // FeatureType (optional)
    type: ...,
    // number (optional)
    page: 56,
    // number (optional)
    size: 56,
  } satisfies ListFeaturesRequest;

  try {
    const data = await api.listFeatures(body);
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
| **type** | `FeatureType` |  | [Optional] [Defaults to `undefined`] [Enum: TOUR, TOOLTIP, BANNER, FEATURE_FLAG, ANNOUNCEMENT] |
| **page** | `number` |  | [Optional] [Defaults to `0`] |
| **size** | `number` |  | [Optional] [Defaults to `20`] |

### Return type

[**Array&lt;FeatureDefinitionDTO&gt;**](FeatureDefinitionDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | List of feature definitions |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateFeature

> FeatureDefinitionDTO updateFeature(featureKey, updateFeatureRequest)

Update an existing feature definition

Why this endpoint is needed: Feature metadata, access rules, and rollout percentages need to be adjustable as product strategy evolves. 

### Example

```ts
import {
  Configuration,
  FeatureManagementApi,
} from '@survey-engine/sdk';
import type { UpdateFeatureOperationRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new FeatureManagementApi(config);

  const body = {
    // string
    featureKey: featureKey_example,
    // UpdateFeatureRequest
    updateFeatureRequest: ...,
  } satisfies UpdateFeatureOperationRequest;

  try {
    const data = await api.updateFeature(body);
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
| **updateFeatureRequest** | [UpdateFeatureRequest](UpdateFeatureRequest.md) |  | |

### Return type

[**FeatureDefinitionDTO**](FeatureDefinitionDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Feature updated |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

