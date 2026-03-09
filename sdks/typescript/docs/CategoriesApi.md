# CategoriesApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createCategory**](CategoriesApi.md#createcategory) | **POST** /api/v1/categories | Create a category grouping for questions |
| [**deactivateCategory**](CategoriesApi.md#deactivatecategory) | **DELETE** /api/v1/categories/{id} | Deactivate a category |
| [**getCategory**](CategoriesApi.md#getcategory) | **GET** /api/v1/categories/{id} | Get one category by id |
| [**listCategories**](CategoriesApi.md#listcategories) | **GET** /api/v1/categories | List active categories |
| [**updateCategory**](CategoriesApi.md#updatecategory) | **PUT** /api/v1/categories/{id} | Update category and mappings |



## createCategory

> CategoryResponse createCategory(categoryRequest)

Create a category grouping for questions

Why this endpoint is needed: Categories group question assets into business dimensions that support structure, analytics, and scoring design.  What this endpoint does: It creates a category with optional question mappings and stores category metadata under tenant ownership.  How this endpoint does it: The service validates category payloads, checks referenced question constraints where needed, persists category + mappings, and returns CategoryResponse with mapping summaries. 

### Example

```ts
import {
  Configuration,
  CategoriesApi,
} from '@survey-engine/sdk';
import type { CreateCategoryRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CategoriesApi(config);

  const body = {
    // CategoryRequest
    categoryRequest: ...,
  } satisfies CreateCategoryRequest;

  try {
    const data = await api.createCategory(body);
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
| **categoryRequest** | [CategoryRequest](CategoryRequest.md) |  | |

### Return type

[**CategoryResponse**](CategoryResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Category created |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## deactivateCategory

> deactivateCategory(id)

Deactivate a category

Why this endpoint is needed: Teams need to retire categories without deleting historical campaign and scoring references.  What this endpoint does: It marks the category as inactive for future authoring while retaining historical records.  How this endpoint does it: The service enforces tenant ownership, flips active status, and returns no content. 

### Example

```ts
import {
  Configuration,
  CategoriesApi,
} from '@survey-engine/sdk';
import type { DeactivateCategoryRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CategoriesApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies DeactivateCategoryRequest;

  try {
    const data = await api.deactivateCategory(body);
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
| **id** | `string` |  | [Defaults to `undefined`] |

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
| **204** | Category deactivated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCategory

> CategoryResponse getCategory(id)

Get one category by id

Why this endpoint is needed: Category detail pages and editor forms need full mapping visibility.  What this endpoint does: It returns a single category record including mapped-question metadata.  How this endpoint does it: The service loads category and mapping state under tenant scope and returns a DTO optimized for UI consumption. 

### Example

```ts
import {
  Configuration,
  CategoriesApi,
} from '@survey-engine/sdk';
import type { GetCategoryRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CategoriesApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies GetCategoryRequest;

  try {
    const data = await api.getCategory(body);
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
| **id** | `string` |  | [Defaults to `undefined`] |

### Return type

[**CategoryResponse**](CategoryResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Category returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listCategories

> Array&lt;CategoryResponse&gt; listCategories()

List active categories

Why this endpoint is needed: Authoring screens need category inventory for survey composition and weighting strategy.  What this endpoint does: It returns active categories available in the authenticated tenant workspace.  How this endpoint does it: The category service applies tenant filters and active-state filters, then maps entity results to response DTOs. 

### Example

```ts
import {
  Configuration,
  CategoriesApi,
} from '@survey-engine/sdk';
import type { ListCategoriesRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CategoriesApi(config);

  try {
    const data = await api.listCategories();
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

[**Array&lt;CategoryResponse&gt;**](CategoryResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Categories returned |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateCategory

> CategoryResponse updateCategory(id, categoryRequest)

Update category and mappings

Why this endpoint is needed: Category definitions and ordering can change as survey programs mature.  What this endpoint does: It updates category metadata and mapped-question configuration.  How this endpoint does it: Validation and tenant checks run first, then mapping set updates are persisted and reflected in the response DTO. 

### Example

```ts
import {
  Configuration,
  CategoriesApi,
} from '@survey-engine/sdk';
import type { UpdateCategoryRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CategoriesApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // CategoryRequest
    categoryRequest: ...,
  } satisfies UpdateCategoryRequest;

  try {
    const data = await api.updateCategory(body);
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
| **id** | `string` |  | [Defaults to `undefined`] |
| **categoryRequest** | [CategoryRequest](CategoryRequest.md) |  | |

### Return type

[**CategoryResponse**](CategoryResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Category updated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

