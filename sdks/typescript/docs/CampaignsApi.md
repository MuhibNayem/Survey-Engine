# CampaignsApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**activateCampaign**](CampaignsApi.md#activatecampaign) | **POST** /api/v1/campaigns/{id}/activate | Activate campaign to accept responses |
| [**createCampaign**](CampaignsApi.md#createcampaign) | **POST** /api/v1/campaigns | Create a campaign from a survey |
| [**deactivateCampaign**](CampaignsApi.md#deactivatecampaign) | **DELETE** /api/v1/campaigns/{id} | Deactivate campaign |
| [**generateCampaignChannels**](CampaignsApi.md#generatecampaignchannels) | **POST** /api/v1/campaigns/{id}/distribute | Generate distribution channels for a campaign |
| [**getCampaign**](CampaignsApi.md#getcampaign) | **GET** /api/v1/campaigns/{id} | Get campaign by id |
| [**getCampaignPreview**](CampaignsApi.md#getcampaignpreview) | **GET** /api/v1/campaigns/{id}/preview | Get admin preview payload for a campaign |
| [**getCampaignSettings**](CampaignsApi.md#getcampaignsettings) | **GET** /api/v1/campaigns/{id}/settings | Get campaign runtime settings |
| [**getPublicCampaignPreview**](CampaignsApi.md#getpubliccampaignpreview) | **GET** /api/v1/public/campaigns/{id}/preview | Get responder-facing preview payload (public endpoint) |
| [**listCampaignChannels**](CampaignsApi.md#listcampaignchannels) | **GET** /api/v1/campaigns/{id}/channels | List generated channels for a campaign |
| [**listCampaigns**](CampaignsApi.md#listcampaigns) | **GET** /api/v1/campaigns | List active campaigns |
| [**updateCampaign**](CampaignsApi.md#updatecampaign) | **PUT** /api/v1/campaigns/{id} | Update campaign metadata |
| [**updateCampaignSettings**](CampaignsApi.md#updatecampaignsettings) | **PUT** /api/v1/campaigns/{id}/settings | Update campaign runtime settings |



## activateCampaign

> CampaignResponse activateCampaign(id)

Activate campaign to accept responses

Why this endpoint is needed: Activation is a controlled launch gate. It prevents collecting responses from improperly prepared campaigns.  What this endpoint does: It transitions a campaign into active state when preconditions pass, links latest survey snapshot, and upserts campaign default weight profile from pinned category weights.  How this endpoint does it: The service verifies linked survey lifecycle requirements (must be PUBLISHED), loads latest snapshot, upserts default weight profile, updates campaign status to ACTIVE, and returns campaign response. 

### Example

```ts
import {
  Configuration,
  CampaignsApi,
} from '@survey-engine/sdk';
import type { ActivateCampaignRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CampaignsApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies ActivateCampaignRequest;

  try {
    const data = await api.activateCampaign(body);
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

[**CampaignResponse**](CampaignResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign activated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |
| **409** | Requested action conflicts with current resource state |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## createCampaign

> CampaignResponse createCampaign(campaignRequest)

Create a campaign from a survey

Why this endpoint is needed: Campaigns are the executable delivery unit for collecting responses from a specific survey snapshot and runtime policy set.  What this endpoint does: It creates a tenant-scoped campaign with name, survey binding, description, and access mode (PUBLIC/PRIVATE).  How this endpoint does it: The service validates survey reference and tenant scope, normalizes deprecated auth mode values, persists campaign state, and returns CampaignResponse. 

### Example

```ts
import {
  Configuration,
  CampaignsApi,
} from '@survey-engine/sdk';
import type { CreateCampaignRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CampaignsApi(config);

  const body = {
    // CampaignRequest
    campaignRequest: ...,
  } satisfies CreateCampaignRequest;

  try {
    const data = await api.createCampaign(body);
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
| **campaignRequest** | [CampaignRequest](CampaignRequest.md) |  | |

### Return type

[**CampaignResponse**](CampaignResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Campaign created |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## deactivateCampaign

> deactivateCampaign(id)

Deactivate campaign

Why this endpoint is needed: Retiring campaign availability should not erase historical response evidence.  What this endpoint does: It marks campaign inactive.  How this endpoint does it: Tenant-scoped campaign lookup is followed by status/active-state mutation and no-content completion. 

### Example

```ts
import {
  Configuration,
  CampaignsApi,
} from '@survey-engine/sdk';
import type { DeactivateCampaignRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CampaignsApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies DeactivateCampaignRequest;

  try {
    const data = await api.deactivateCampaign(body);
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
| **204** | Campaign deactivated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## generateCampaignChannels

> Array&lt;DistributionChannelResponse&gt; generateCampaignChannels(id)

Generate distribution channels for a campaign

Why this endpoint is needed: Operations teams need standardized distribution artifacts (links and embed formats) to launch outreach quickly.  What this endpoint does: It generates and stores channel records such as public/private links and embed snippets.  How this endpoint does it: Distribution service builds channel values from campaign context, persists channel rows, and returns generated list. 

### Example

```ts
import {
  Configuration,
  CampaignsApi,
} from '@survey-engine/sdk';
import type { GenerateCampaignChannelsRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CampaignsApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies GenerateCampaignChannelsRequest;

  try {
    const data = await api.generateCampaignChannels(body);
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

[**Array&lt;DistributionChannelResponse&gt;**](DistributionChannelResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Channels generated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCampaign

> CampaignResponse getCampaign(id)

Get campaign by id

Why this endpoint is needed: Campaign detail retrieval is required for settings updates, monitoring, and support troubleshooting.  What this endpoint does: It returns one campaign record with current status and metadata.  How this endpoint does it: The service resolves campaign by tenant and id and returns mapped DTO fields. 

### Example

```ts
import {
  Configuration,
  CampaignsApi,
} from '@survey-engine/sdk';
import type { GetCampaignRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CampaignsApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies GetCampaignRequest;

  try {
    const data = await api.getCampaign(body);
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

[**CampaignResponse**](CampaignResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCampaignPreview

> CampaignPreviewResponse getCampaignPreview(id)

Get admin preview payload for a campaign

Why this endpoint is needed: Campaign owners need a pre-launch visual/data validation endpoint to verify header/footer/questions and responder UX flags.  What this endpoint does: It assembles campaign, survey, and settings into a responder-facing preview model for admin users.  How this endpoint does it: Campaign service resolves campaign/settings/survey in tenant scope, maps each page/question with pinned version context, and returns CampaignPreviewResponse. 

### Example

```ts
import {
  Configuration,
  CampaignsApi,
} from '@survey-engine/sdk';
import type { GetCampaignPreviewRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CampaignsApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies GetCampaignPreviewRequest;

  try {
    const data = await api.getCampaignPreview(body);
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

[**CampaignPreviewResponse**](CampaignPreviewResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign preview returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCampaignSettings

> CampaignSettingsResponse getCampaignSettings(id)

Get campaign runtime settings

Why this endpoint is needed: Operations and UI flows require a read endpoint for persisted runtime controls before edit/save cycles.  What this endpoint does: It returns the current campaign settings object.  How this endpoint does it: Campaign service validates tenant-scoped campaign visibility and returns mapped CampaignSettingsResponse. 

### Example

```ts
import {
  Configuration,
  CampaignsApi,
} from '@survey-engine/sdk';
import type { GetCampaignSettingsRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CampaignsApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies GetCampaignSettingsRequest;

  try {
    const data = await api.getCampaignSettings(body);
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

[**CampaignSettingsResponse**](CampaignSettingsResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign settings returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getPublicCampaignPreview

> CampaignPreviewResponse getPublicCampaignPreview(id)

Get responder-facing preview payload (public endpoint)

Why this endpoint is needed: Responder runtime needs a non-admin endpoint to load campaign form structure for public/private submission flows.  What this endpoint does: It returns preview payload for campaigns currently active.  How this endpoint does it: Service resolves campaign/settings/survey and returns CampaignPreviewResponse when campaign is active; access mode enforcement for private campaigns happens on submit/auth endpoints. 

### Example

```ts
import {
  Configuration,
  CampaignsApi,
} from '@survey-engine/sdk';
import type { GetPublicCampaignPreviewRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const api = new CampaignsApi();

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies GetPublicCampaignPreviewRequest;

  try {
    const data = await api.getPublicCampaignPreview(body);
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

[**CampaignPreviewResponse**](CampaignPreviewResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Public preview returned |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listCampaignChannels

> Array&lt;DistributionChannelResponse&gt; listCampaignChannels(id)

List generated channels for a campaign

Why this endpoint is needed: Teams often need to re-fetch channel assets after initial generation for reminders and omnichannel delivery.  What this endpoint does: It returns all persisted distribution channels for the campaign.  How this endpoint does it: Distribution service reads campaign channel rows under tenant scope and maps to response DTOs. 

### Example

```ts
import {
  Configuration,
  CampaignsApi,
} from '@survey-engine/sdk';
import type { ListCampaignChannelsRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CampaignsApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies ListCampaignChannelsRequest;

  try {
    const data = await api.listCampaignChannels(body);
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

[**Array&lt;DistributionChannelResponse&gt;**](DistributionChannelResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Channel list returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listCampaigns

> Array&lt;CampaignResponse&gt; listCampaigns()

List active campaigns

Why this endpoint is needed: Campaign operations teams need a dashboard view of active and manageable campaigns.  What this endpoint does: It returns campaigns under authenticated tenant scope.  How this endpoint does it: Campaign service executes tenant-scoped retrieval and response mapping. 

### Example

```ts
import {
  Configuration,
  CampaignsApi,
} from '@survey-engine/sdk';
import type { ListCampaignsRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CampaignsApi(config);

  try {
    const data = await api.listCampaigns();
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

[**Array&lt;CampaignResponse&gt;**](CampaignResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign list returned |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateCampaign

> CampaignResponse updateCampaign(id, campaignRequest)

Update campaign metadata

Why this endpoint is needed: Campaigns require controlled edits to operational metadata and associations.  What this endpoint does: It updates campaign request fields while preserving lifecycle constraints.  How this endpoint does it: Service validates ownership and allowed update semantics, then persists and returns updated campaign data. 

### Example

```ts
import {
  Configuration,
  CampaignsApi,
} from '@survey-engine/sdk';
import type { UpdateCampaignRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CampaignsApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // CampaignRequest
    campaignRequest: ...,
  } satisfies UpdateCampaignRequest;

  try {
    const data = await api.updateCampaign(body);
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
| **campaignRequest** | [CampaignRequest](CampaignRequest.md) |  | |

### Return type

[**CampaignResponse**](CampaignResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign updated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateCampaignSettings

> CampaignResponse updateCampaignSettings(id, campaignSettingsRequest)

Update campaign runtime settings

Why this endpoint is needed: Campaign runtime behavior (quota, timeout, restrictions, UI toggles, metadata capture) must be adjustable independently from core campaign identity.  What this endpoint does: It stores runtime policy controls that are enforced when responders submit answers. If closeDate is set to now/past, open responses are auto-locked immediately.  How this endpoint does it: The service loads target campaign by tenant, merges settings payload, persists state, and returns updated campaign context. 

### Example

```ts
import {
  Configuration,
  CampaignsApi,
} from '@survey-engine/sdk';
import type { UpdateCampaignSettingsRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CampaignsApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // CampaignSettingsRequest
    campaignSettingsRequest: ...,
  } satisfies UpdateCampaignSettingsRequest;

  try {
    const data = await api.updateCampaignSettings(body);
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
| **campaignSettingsRequest** | [CampaignSettingsRequest](CampaignSettingsRequest.md) |  | |

### Return type

[**CampaignResponse**](CampaignResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign settings updated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

