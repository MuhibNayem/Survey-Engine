# SurveysApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createSurvey**](SurveysApi.md#createsurvey) | **POST** /api/v1/surveys | Create a survey draft |
| [**deactivateSurvey**](SurveysApi.md#deactivatesurvey) | **DELETE** /api/v1/surveys/{id} | Deactivate a survey |
| [**getSurvey**](SurveysApi.md#getsurvey) | **GET** /api/v1/surveys/{id} | Get one survey by id |
| [**listSurveys**](SurveysApi.md#listsurveys) | **GET** /api/v1/surveys | List active surveys |
| [**transitionSurveyLifecycle**](SurveysApi.md#transitionsurveylifecycle) | **POST** /api/v1/surveys/{id}/lifecycle | Transition survey lifecycle state |
| [**updateSurvey**](SurveysApi.md#updatesurvey) | **PUT** /api/v1/surveys/{id} | Update survey draft content |



## createSurvey

> SurveyResponse createSurvey(surveyRequest)

Create a survey draft

Why this endpoint is needed: Survey lifecycle begins with a draft container where structure can be assembled before publication.  What this endpoint does: It creates a tenant-scoped survey draft with title, description, and page/question structure.  How this endpoint does it: The service validates schema, persists draft state, and returns the survey representation including lifecycle status. 

### Example

```ts
import {
  Configuration,
  SurveysApi,
} from '@survey-engine/sdk';
import type { CreateSurveyRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new SurveysApi(config);

  const body = {
    // SurveyRequest
    surveyRequest: ...,
  } satisfies CreateSurveyRequest;

  try {
    const data = await api.createSurvey(body);
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
| **surveyRequest** | [SurveyRequest](SurveyRequest.md) |  | |

### Return type

[**SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Survey draft created |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## deactivateSurvey

> deactivateSurvey(id)

Deactivate a survey

Why this endpoint is needed: Organizations need to retire obsolete surveys without physically deleting historical references.  What this endpoint does: It marks the survey inactive.  How this endpoint does it: Tenant ownership is validated and active flag is disabled; endpoint returns no-content on success. 

### Example

```ts
import {
  Configuration,
  SurveysApi,
} from '@survey-engine/sdk';
import type { DeactivateSurveyRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new SurveysApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies DeactivateSurveyRequest;

  try {
    const data = await api.deactivateSurvey(body);
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
| **204** | Survey deactivated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getSurvey

> SurveyResponse getSurvey(id)

Get one survey by id

Why this endpoint is needed: Teams need detailed survey context for editing, publishing decisions, and audits.  What this endpoint does: It returns one survey including pages/questions and lifecycle metadata.  How this endpoint does it: The service resolves survey by id and tenant, then maps nested structures into SurveyResponse. 

### Example

```ts
import {
  Configuration,
  SurveysApi,
} from '@survey-engine/sdk';
import type { GetSurveyRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new SurveysApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies GetSurveyRequest;

  try {
    const data = await api.getSurvey(body);
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

[**SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Survey returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listSurveys

> Array&lt;SurveyResponse&gt; listSurveys()

List active surveys

Why this endpoint is needed: Admin dashboards require an overview of reusable survey assets and lifecycle states.  What this endpoint does: It returns active surveys under current tenant scope.  How this endpoint does it: Survey repository filters by tenant and active state, then maps records to response DTOs. 

### Example

```ts
import {
  Configuration,
  SurveysApi,
} from '@survey-engine/sdk';
import type { ListSurveysRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new SurveysApi(config);

  try {
    const data = await api.listSurveys();
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

[**Array&lt;SurveyResponse&gt;**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Surveys returned |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## transitionSurveyLifecycle

> SurveyResponse transitionSurveyLifecycle(id, lifecycleTransitionRequest)

Transition survey lifecycle state

Why this endpoint is needed: Lifecycle transitions are a governance gate that prevents unsafe operations (for example activating campaigns from unpublished surveys).  What this endpoint does: It applies a requested lifecycle transition (such as Draft-&gt;Published or Closed-&gt;Published with reason).  How this endpoint does it: The service checks allowed transition matrix, validates transition requirements, updates lifecycle state, and returns the updated survey representation. 

### Example

```ts
import {
  Configuration,
  SurveysApi,
} from '@survey-engine/sdk';
import type { TransitionSurveyLifecycleRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new SurveysApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // LifecycleTransitionRequest
    lifecycleTransitionRequest: ...,
  } satisfies TransitionSurveyLifecycleRequest;

  try {
    const data = await api.transitionSurveyLifecycle(body);
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
| **lifecycleTransitionRequest** | [LifecycleTransitionRequest](LifecycleTransitionRequest.md) |  | |

### Return type

[**SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Lifecycle transition applied |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |
| **409** | Requested action conflicts with current resource state |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateSurvey

> SurveyResponse updateSurvey(id, surveyRequest)

Update survey draft content

Why this endpoint is needed: Draft surveys evolve before publication and require iterative edits.  What this endpoint does: It updates survey details and structure when lifecycle rules allow modification.  How this endpoint does it: The service validates immutability/lifecycle constraints, persists updates, and returns the updated survey. 

### Example

```ts
import {
  Configuration,
  SurveysApi,
} from '@survey-engine/sdk';
import type { UpdateSurveyRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new SurveysApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // SurveyRequest
    surveyRequest: ...,
  } satisfies UpdateSurveyRequest;

  try {
    const data = await api.updateSurvey(body);
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
| **surveyRequest** | [SurveyRequest](SurveyRequest.md) |  | |

### Return type

[**SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Survey updated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |
| **409** | Requested action conflicts with current resource state |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

