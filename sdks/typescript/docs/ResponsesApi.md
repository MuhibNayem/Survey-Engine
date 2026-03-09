# ResponsesApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**compareSegments**](ResponsesApi.md#comparesegments) | **POST** /api/v1/analytics/campaigns/{campaignId}/compare | Compare multiple analytics segments |
| [**getCampaignAnalytics**](ResponsesApi.md#getcampaignanalytics) | **GET** /api/v1/responses/analytics/{campaignId} | Get aggregated analytics for a campaign |
| [**getFullReport**](ResponsesApi.md#getfullreport) | **GET** /api/v1/analytics/campaigns/{campaignId}/full-report | Get full analytics report with metadata filters |
| [**getResponse**](ResponsesApi.md#getresponse) | **GET** /api/v1/responses/{id} | Get one response by id |
| [**listResponsesByCampaign**](ResponsesApi.md#listresponsesbycampaign) | **GET** /api/v1/responses/campaign/{campaignId} | List responses for a campaign |
| [**lockResponse**](ResponsesApi.md#lockresponse) | **POST** /api/v1/responses/{id}/lock | Lock a response manually |
| [**reopenResponse**](ResponsesApi.md#reopenresponse) | **POST** /api/v1/responses/{id}/reopen | Reopen a locked response with reason |
| [**submitResponse**](ResponsesApi.md#submitresponse) | **POST** /api/v1/responses | Submit survey response (public and private campaign entry) |



## compareSegments

> ComparisonAnalyticsResponse compareSegments(campaignId, body)

Compare multiple analytics segments

Why this endpoint is needed: Platform users need side-by-side comparative analytics of demographic segments.  What this endpoint does: Multiplexes the /full-report generation across up to 5 concurrent demographic segments. 

### Example

```ts
import {
  Configuration,
  ResponsesApi,
} from '@survey-engine/sdk';
import type { CompareSegmentsRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ResponsesApi(config);

  const body = {
    // string
    campaignId: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // ComparisonRequest
    body: ...,
  } satisfies CompareSegmentsRequest;

  try {
    const data = await api.compareSegments(body);
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
| **campaignId** | `string` |  | [Defaults to `undefined`] |
| **body** | `ComparisonRequest` |  | |

### Return type

[**ComparisonAnalyticsResponse**](ComparisonAnalyticsResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Multi-segment comparison matrix returned |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCampaignAnalytics

> CampaignAnalytics getCampaignAnalytics(campaignId)

Get aggregated analytics for a campaign

Why this endpoint is needed: Campaign operators need near-real-time aggregate visibility without reading raw response payloads.  What this endpoint does: It returns aggregate counts and completion rate metrics for a campaign.  How this endpoint does it: Analytics service runs campaign-scoped aggregate queries under tenant context and returns CampaignAnalytics DTO. 

### Example

```ts
import {
  Configuration,
  ResponsesApi,
} from '@survey-engine/sdk';
import type { GetCampaignAnalyticsRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ResponsesApi(config);

  const body = {
    // string
    campaignId: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies GetCampaignAnalyticsRequest;

  try {
    const data = await api.getCampaignAnalytics(body);
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
| **campaignId** | `string` |  | [Defaults to `undefined`] |

### Return type

[**CampaignAnalytics**](CampaignAnalytics.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign analytics returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getFullReport

> FullCampaignAnalyticsResponse getFullReport(campaignId)

Get full analytics report with metadata filters

Why this endpoint is needed: The dashboard needs an aggregated snapshot of campaign performance, score curves, and individual question breakdowns.  What this endpoint does: Returns all analytics metrics for a single segment (or the entire campaign). 

### Example

```ts
import {
  Configuration,
  ResponsesApi,
} from '@survey-engine/sdk';
import type { GetFullReportRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ResponsesApi(config);

  const body = {
    // string
    campaignId: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies GetFullReportRequest;

  try {
    const data = await api.getFullReport(body);
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
| **campaignId** | `string` |  | [Defaults to `undefined`] |

### Return type

[**FullCampaignAnalyticsResponse**](FullCampaignAnalyticsResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Full campaign analytics payload returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getResponse

> SurveyResponseResponse getResponse(id)

Get one response by id

Why this endpoint is needed: Support and operational teams need direct access to a specific response record for verification and exception handling.  What this endpoint does: It returns one response and its answer details.  How this endpoint does it: Response service resolves response by id under tenant context and maps nested answer records to DTO. 

### Example

```ts
import {
  Configuration,
  ResponsesApi,
} from '@survey-engine/sdk';
import type { GetResponseRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ResponsesApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies GetResponseRequest;

  try {
    const data = await api.getResponse(body);
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

[**SurveyResponseResponse**](SurveyResponseResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Response returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listResponsesByCampaign

> Array&lt;SurveyResponseResponse&gt; listResponsesByCampaign(campaignId)

List responses for a campaign

Why this endpoint is needed: Campaign managers need a scoped list view of responses for review, quality checks, and manual workflows.  What this endpoint does: It returns campaign-scoped response list under tenant ownership checks.  How this endpoint does it: The service validates campaign accessibility under tenant context and fetches mapped response rows by campaign id. 

### Example

```ts
import {
  Configuration,
  ResponsesApi,
} from '@survey-engine/sdk';
import type { ListResponsesByCampaignRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ResponsesApi(config);

  const body = {
    // string
    campaignId: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies ListResponsesByCampaignRequest;

  try {
    const data = await api.listResponsesByCampaign(body);
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
| **campaignId** | `string` |  | [Defaults to `undefined`] |

### Return type

[**Array&lt;SurveyResponseResponse&gt;**](SurveyResponseResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign responses returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## lockResponse

> SurveyResponseResponse lockResponse(id)

Lock a response manually

Why this endpoint is needed: Although responses auto-lock on submit, operations teams may need explicit lock controls for exceptional workflows.  What this endpoint does: It changes response state to locked and records lock timestamp.  How this endpoint does it: Locking service validates current state and tenant ownership, applies status mutation, and returns updated response. 

### Example

```ts
import {
  Configuration,
  ResponsesApi,
} from '@survey-engine/sdk';
import type { LockResponseRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ResponsesApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies LockResponseRequest;

  try {
    const data = await api.lockResponse(body);
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

[**SurveyResponseResponse**](SurveyResponseResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Response locked |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |
| **409** | Requested action conflicts with current resource state |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## reopenResponse

> SurveyResponseResponse reopenResponse(id, reopenRequest)

Reopen a locked response with reason

Why this endpoint is needed: Real operations require a controlled exception path for corrections after lock.  What this endpoint does: It reopens a previously locked response and captures reason/window metadata.  How this endpoint does it: Locking service validates state transitions and reason constraints, updates response status, and returns reopened response record. If campaign close date has passed, close-time lock enforcement may lock it again. 

### Example

```ts
import {
  Configuration,
  ResponsesApi,
} from '@survey-engine/sdk';
import type { ReopenResponseRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ResponsesApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // ReopenRequest
    reopenRequest: ...,
  } satisfies ReopenResponseRequest;

  try {
    const data = await api.reopenResponse(body);
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
| **reopenRequest** | [ReopenRequest](ReopenRequest.md) |  | |

### Return type

[**SurveyResponseResponse**](SurveyResponseResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Response reopened |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## submitResponse

> SurveyResponseResponse submitResponse(responseSubmissionRequest)

Submit survey response (public and private campaign entry)

Why this endpoint is needed: This is the central data-ingestion endpoint where responder participation is captured.  What this endpoint does: It accepts answer payload for a campaign, enforces campaign status and runtime controls, enforces private-auth requirements where applicable, validates against pinned snapshot versions, auto-calculates weighted score when default profile exists, stores response and answers, then auto-locks response.  How this endpoint does it: The service performs campaign lookup, access-mode enforcement (public or private), credential validation (responderToken or responderAccessCode for private), settings checks (quota/restrictions/timeouts), snapshot-based answer validation/scoring, persistence, and status transition to LOCKED before returning SurveyResponseResponse. 

### Example

```ts
import {
  Configuration,
  ResponsesApi,
} from '@survey-engine/sdk';
import type { SubmitResponseRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const api = new ResponsesApi();

  const body = {
    // ResponseSubmissionRequest
    responseSubmissionRequest: ...,
  } satisfies SubmitResponseRequest;

  try {
    const data = await api.submitResponse(body);
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
| **responseSubmissionRequest** | [ResponseSubmissionRequest](ResponseSubmissionRequest.md) |  | |

### Return type

[**SurveyResponseResponse**](SurveyResponseResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Response accepted and locked |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |
| **429** | Quota or throttling rule prevented execution |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

