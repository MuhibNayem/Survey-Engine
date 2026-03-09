# ScoringApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**calculateWeightedScore**](ScoringApi.md#calculateweightedscore) | **POST** /api/v1/scoring/calculate/{profileId} | Calculate weighted score from raw category scores |
| [**createWeightProfile**](ScoringApi.md#createweightprofile) | **POST** /api/v1/scoring/profiles | Create scoring weight profile |
| [**deactivateWeightProfile**](ScoringApi.md#deactivateweightprofile) | **DELETE** /api/v1/scoring/profiles/{id} | Deactivate scoring profile |
| [**getWeightProfile**](ScoringApi.md#getweightprofile) | **GET** /api/v1/scoring/profiles/{id} | Get one scoring profile by id |
| [**listWeightProfilesByCampaign**](ScoringApi.md#listweightprofilesbycampaign) | **GET** /api/v1/scoring/profiles/campaign/{campaignId} | List scoring profiles for campaign |
| [**updateWeightProfile**](ScoringApi.md#updateweightprofile) | **PUT** /api/v1/scoring/profiles/{id} | Update scoring profile |
| [**validateWeightProfile**](ScoringApi.md#validateweightprofile) | **POST** /api/v1/scoring/profiles/{id}/validate | Validate category weight sum for a profile |



## calculateWeightedScore

> ScoreResult calculateWeightedScore(profileId, requestBody)

Calculate weighted score from raw category scores

Why this endpoint is needed: Consumers need deterministic score output from raw category data and configured business weighting policy.  What this endpoint does: It computes normalized category contributions and returns a total weighted score breakdown.  How this endpoint does it: Scoring engine loads profile weights, validates inputs, computes per-category normalized and weighted values, and returns ScoreResult with category details. 

### Example

```ts
import {
  Configuration,
  ScoringApi,
} from '@survey-engine/sdk';
import type { CalculateWeightedScoreRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ScoringApi(config);

  const body = {
    // string
    profileId: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // { [key: string]: number; }
    requestBody: {"0f2069da-4f19-4f4a-a8f7-f2d067e1a5d0":82.5,"12a6a2f0-69da-4f19-9c80-c84b6df67464":71.0},
  } satisfies CalculateWeightedScoreRequest;

  try {
    const data = await api.calculateWeightedScore(body);
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
| **profileId** | `string` |  | [Defaults to `undefined`] |
| **requestBody** | `{ [key: string]: number; }` |  | |

### Return type

[**ScoreResult**](ScoreResult.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Score calculated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## createWeightProfile

> WeightProfileResponse createWeightProfile(weightProfileRequest)

Create scoring weight profile

Why this endpoint is needed: Score computation requires explicit category weighting definitions linked to campaign context.  What this endpoint does: It creates a weight profile that defines how raw category scores contribute to total score.  How this endpoint does it: Service validates campaign scope and category-weight payload, persists profile and nested weights, and returns profile representation. 

### Example

```ts
import {
  Configuration,
  ScoringApi,
} from '@survey-engine/sdk';
import type { CreateWeightProfileRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ScoringApi(config);

  const body = {
    // WeightProfileRequest
    weightProfileRequest: ...,
  } satisfies CreateWeightProfileRequest;

  try {
    const data = await api.createWeightProfile(body);
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
| **weightProfileRequest** | [WeightProfileRequest](WeightProfileRequest.md) |  | |

### Return type

[**WeightProfileResponse**](WeightProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Weight profile created |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## deactivateWeightProfile

> deactivateWeightProfile(id)

Deactivate scoring profile

Why this endpoint is needed: Obsolete profiles must be retired while preserving historical result integrity.  What this endpoint does: It marks profile inactive.  How this endpoint does it: Tenant-scoped lookup followed by active-state mutation and no-content response. 

### Example

```ts
import {
  Configuration,
  ScoringApi,
} from '@survey-engine/sdk';
import type { DeactivateWeightProfileRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ScoringApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies DeactivateWeightProfileRequest;

  try {
    const data = await api.deactivateWeightProfile(body);
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
| **204** | Weight profile deactivated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getWeightProfile

> WeightProfileResponse getWeightProfile(id)

Get one scoring profile by id

Why this endpoint is needed: Teams need profile-level visibility for verification and lifecycle updates.  What this endpoint does: It returns one weight profile including category weights.  How this endpoint does it: Service loads profile by id under tenant context and maps to DTO. 

### Example

```ts
import {
  Configuration,
  ScoringApi,
} from '@survey-engine/sdk';
import type { GetWeightProfileRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ScoringApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies GetWeightProfileRequest;

  try {
    const data = await api.getWeightProfile(body);
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

[**WeightProfileResponse**](WeightProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Weight profile returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listWeightProfilesByCampaign

> Array&lt;WeightProfileResponse&gt; listWeightProfilesByCampaign(campaignId)

List scoring profiles for campaign

Why this endpoint is needed: Campaign-level scoring operations need discoverability of attached profiles.  What this endpoint does: It returns all active/in-scope profiles linked to the specified campaign.  How this endpoint does it: Service validates campaign access by tenant and fetches profile rows filtered by campaign id. 

### Example

```ts
import {
  Configuration,
  ScoringApi,
} from '@survey-engine/sdk';
import type { ListWeightProfilesByCampaignRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ScoringApi(config);

  const body = {
    // string
    campaignId: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies ListWeightProfilesByCampaignRequest;

  try {
    const data = await api.listWeightProfilesByCampaign(body);
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

[**Array&lt;WeightProfileResponse&gt;**](WeightProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Profiles returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateWeightProfile

> WeightProfileResponse updateWeightProfile(id, weightProfileRequest)

Update scoring profile

Why this endpoint is needed: Scoring rubrics evolve and profiles require controlled updates.  What this endpoint does: It updates profile metadata and category-weight assignments.  How this endpoint does it: Service validates payload and tenant ownership, persists new values, and returns updated profile DTO. 

### Example

```ts
import {
  Configuration,
  ScoringApi,
} from '@survey-engine/sdk';
import type { UpdateWeightProfileRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ScoringApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // WeightProfileRequest
    weightProfileRequest: ...,
  } satisfies UpdateWeightProfileRequest;

  try {
    const data = await api.updateWeightProfile(body);
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
| **weightProfileRequest** | [WeightProfileRequest](WeightProfileRequest.md) |  | |

### Return type

[**WeightProfileResponse**](WeightProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Weight profile updated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## validateWeightProfile

> validateWeightProfile(id)

Validate category weight sum for a profile

Why this endpoint is needed: Invalid weighting definitions can produce misleading score outputs; explicit pre-check endpoint reduces operational mistakes.  What this endpoint does: It validates profile constraints (including total weight sum rule).  How this endpoint does it: Service computes profile totals and throws deterministic business error when constraints are violated. 

### Example

```ts
import {
  Configuration,
  ScoringApi,
} from '@survey-engine/sdk';
import type { ValidateWeightProfileRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ScoringApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies ValidateWeightProfileRequest;

  try {
    const data = await api.validateWeightProfile(body);
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
| **200** | Profile is valid |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

