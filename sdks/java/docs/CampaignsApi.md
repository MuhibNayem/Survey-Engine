# CampaignsApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**activateCampaign**](CampaignsApi.md#activateCampaign) | **POST** /api/v1/campaigns/{id}/activate | Activate campaign to accept responses |
| [**createCampaign**](CampaignsApi.md#createCampaign) | **POST** /api/v1/campaigns | Create a campaign from a survey |
| [**deactivateCampaign**](CampaignsApi.md#deactivateCampaign) | **DELETE** /api/v1/campaigns/{id} | Deactivate campaign |
| [**generateCampaignChannels**](CampaignsApi.md#generateCampaignChannels) | **POST** /api/v1/campaigns/{id}/distribute | Generate distribution channels for a campaign |
| [**getCampaign**](CampaignsApi.md#getCampaign) | **GET** /api/v1/campaigns/{id} | Get campaign by id |
| [**getCampaignPreview**](CampaignsApi.md#getCampaignPreview) | **GET** /api/v1/campaigns/{id}/preview | Get admin preview payload for a campaign |
| [**getCampaignSettings**](CampaignsApi.md#getCampaignSettings) | **GET** /api/v1/campaigns/{id}/settings | Get campaign runtime settings |
| [**getPublicCampaignPreview**](CampaignsApi.md#getPublicCampaignPreview) | **GET** /api/v1/public/campaigns/{id}/preview | Get responder-facing preview payload (public endpoint) |
| [**listCampaignChannels**](CampaignsApi.md#listCampaignChannels) | **GET** /api/v1/campaigns/{id}/channels | List generated channels for a campaign |
| [**listCampaigns**](CampaignsApi.md#listCampaigns) | **GET** /api/v1/campaigns | List active campaigns |
| [**updateCampaign**](CampaignsApi.md#updateCampaign) | **PUT** /api/v1/campaigns/{id} | Update campaign metadata |
| [**updateCampaignSettings**](CampaignsApi.md#updateCampaignSettings) | **PUT** /api/v1/campaigns/{id}/settings | Update campaign runtime settings |


<a id="activateCampaign"></a>
# **activateCampaign**
> CampaignResponse activateCampaign(id)

Activate campaign to accept responses

Why this endpoint is needed: Activation is a controlled launch gate. It prevents collecting responses from improperly prepared campaigns.  What this endpoint does: It transitions a campaign into active state when preconditions pass, links latest survey snapshot, and upserts campaign default weight profile from pinned category weights.  How this endpoint does it: The service verifies linked survey lifecycle requirements (must be PUBLISHED), loads latest snapshot, upserts default weight profile, updates campaign status to ACTIVE, and returns campaign response. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CampaignsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CampaignsApi apiInstance = new CampaignsApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      CampaignResponse result = apiInstance.activateCampaign(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CampaignsApi#activateCampaign");
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
| **id** | **UUID**|  | |

### Return type

[**CampaignResponse**](CampaignResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign activated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |
| **409** | Requested action conflicts with current resource state |  -  |

<a id="createCampaign"></a>
# **createCampaign**
> CampaignResponse createCampaign(campaignRequest)

Create a campaign from a survey

Why this endpoint is needed: Campaigns are the executable delivery unit for collecting responses from a specific survey snapshot and runtime policy set.  What this endpoint does: It creates a tenant-scoped campaign with name, survey binding, description, and access mode (PUBLIC/PRIVATE).  How this endpoint does it: The service validates survey reference and tenant scope, normalizes deprecated auth mode values, persists campaign state, and returns CampaignResponse. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CampaignsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CampaignsApi apiInstance = new CampaignsApi(defaultClient);
    CampaignRequest campaignRequest = new CampaignRequest(); // CampaignRequest | 
    try {
      CampaignResponse result = apiInstance.createCampaign(campaignRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CampaignsApi#createCampaign");
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
| **campaignRequest** | [**CampaignRequest**](CampaignRequest.md)|  | |

### Return type

[**CampaignResponse**](CampaignResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Campaign created |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="deactivateCampaign"></a>
# **deactivateCampaign**
> deactivateCampaign(id)

Deactivate campaign

Why this endpoint is needed: Retiring campaign availability should not erase historical response evidence.  What this endpoint does: It marks campaign inactive.  How this endpoint does it: Tenant-scoped campaign lookup is followed by status/active-state mutation and no-content completion. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CampaignsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CampaignsApi apiInstance = new CampaignsApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.deactivateCampaign(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling CampaignsApi#deactivateCampaign");
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
| **id** | **UUID**|  | |

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
| **204** | Campaign deactivated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="generateCampaignChannels"></a>
# **generateCampaignChannels**
> List&lt;DistributionChannelResponse&gt; generateCampaignChannels(id)

Generate distribution channels for a campaign

Why this endpoint is needed: Operations teams need standardized distribution artifacts (links and embed formats) to launch outreach quickly.  What this endpoint does: It generates and stores channel records such as public/private links and embed snippets.  How this endpoint does it: Distribution service builds channel values from campaign context, persists channel rows, and returns generated list. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CampaignsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CampaignsApi apiInstance = new CampaignsApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      List<DistributionChannelResponse> result = apiInstance.generateCampaignChannels(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CampaignsApi#generateCampaignChannels");
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
| **id** | **UUID**|  | |

### Return type

[**List&lt;DistributionChannelResponse&gt;**](DistributionChannelResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Channels generated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="getCampaign"></a>
# **getCampaign**
> CampaignResponse getCampaign(id)

Get campaign by id

Why this endpoint is needed: Campaign detail retrieval is required for settings updates, monitoring, and support troubleshooting.  What this endpoint does: It returns one campaign record with current status and metadata.  How this endpoint does it: The service resolves campaign by tenant and id and returns mapped DTO fields. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CampaignsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CampaignsApi apiInstance = new CampaignsApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      CampaignResponse result = apiInstance.getCampaign(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CampaignsApi#getCampaign");
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
| **id** | **UUID**|  | |

### Return type

[**CampaignResponse**](CampaignResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="getCampaignPreview"></a>
# **getCampaignPreview**
> CampaignPreviewResponse getCampaignPreview(id)

Get admin preview payload for a campaign

Why this endpoint is needed: Campaign owners need a pre-launch visual/data validation endpoint to verify header/footer/questions and responder UX flags.  What this endpoint does: It assembles campaign, survey, and settings into a responder-facing preview model for admin users.  How this endpoint does it: Campaign service resolves campaign/settings/survey in tenant scope, maps each page/question with pinned version context, and returns CampaignPreviewResponse. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CampaignsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CampaignsApi apiInstance = new CampaignsApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      CampaignPreviewResponse result = apiInstance.getCampaignPreview(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CampaignsApi#getCampaignPreview");
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
| **id** | **UUID**|  | |

### Return type

[**CampaignPreviewResponse**](CampaignPreviewResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign preview returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="getCampaignSettings"></a>
# **getCampaignSettings**
> CampaignSettingsResponse getCampaignSettings(id)

Get campaign runtime settings

Why this endpoint is needed: Operations and UI flows require a read endpoint for persisted runtime controls before edit/save cycles.  What this endpoint does: It returns the current campaign settings object.  How this endpoint does it: Campaign service validates tenant-scoped campaign visibility and returns mapped CampaignSettingsResponse. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CampaignsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CampaignsApi apiInstance = new CampaignsApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      CampaignSettingsResponse result = apiInstance.getCampaignSettings(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CampaignsApi#getCampaignSettings");
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
| **id** | **UUID**|  | |

### Return type

[**CampaignSettingsResponse**](CampaignSettingsResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign settings returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="getPublicCampaignPreview"></a>
# **getPublicCampaignPreview**
> CampaignPreviewResponse getPublicCampaignPreview(id)

Get responder-facing preview payload (public endpoint)

Why this endpoint is needed: Responder runtime needs a non-admin endpoint to load campaign form structure for public/private submission flows.  What this endpoint does: It returns preview payload for campaigns currently active.  How this endpoint does it: Service resolves campaign/settings/survey and returns CampaignPreviewResponse when campaign is active; access mode enforcement for private campaigns happens on submit/auth endpoints. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CampaignsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    CampaignsApi apiInstance = new CampaignsApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      CampaignPreviewResponse result = apiInstance.getPublicCampaignPreview(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CampaignsApi#getPublicCampaignPreview");
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
| **id** | **UUID**|  | |

### Return type

[**CampaignPreviewResponse**](CampaignPreviewResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Public preview returned |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="listCampaignChannels"></a>
# **listCampaignChannels**
> List&lt;DistributionChannelResponse&gt; listCampaignChannels(id)

List generated channels for a campaign

Why this endpoint is needed: Teams often need to re-fetch channel assets after initial generation for reminders and omnichannel delivery.  What this endpoint does: It returns all persisted distribution channels for the campaign.  How this endpoint does it: Distribution service reads campaign channel rows under tenant scope and maps to response DTOs. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CampaignsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CampaignsApi apiInstance = new CampaignsApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      List<DistributionChannelResponse> result = apiInstance.listCampaignChannels(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CampaignsApi#listCampaignChannels");
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
| **id** | **UUID**|  | |

### Return type

[**List&lt;DistributionChannelResponse&gt;**](DistributionChannelResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Channel list returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="listCampaigns"></a>
# **listCampaigns**
> List&lt;CampaignResponse&gt; listCampaigns()

List active campaigns

Why this endpoint is needed: Campaign operations teams need a dashboard view of active and manageable campaigns.  What this endpoint does: It returns campaigns under authenticated tenant scope.  How this endpoint does it: Campaign service executes tenant-scoped retrieval and response mapping. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CampaignsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CampaignsApi apiInstance = new CampaignsApi(defaultClient);
    try {
      List<CampaignResponse> result = apiInstance.listCampaigns();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CampaignsApi#listCampaigns");
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

[**List&lt;CampaignResponse&gt;**](CampaignResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign list returned |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="updateCampaign"></a>
# **updateCampaign**
> CampaignResponse updateCampaign(id, campaignRequest)

Update campaign metadata

Why this endpoint is needed: Campaigns require controlled edits to operational metadata and associations.  What this endpoint does: It updates campaign request fields while preserving lifecycle constraints.  How this endpoint does it: Service validates ownership and allowed update semantics, then persists and returns updated campaign data. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CampaignsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CampaignsApi apiInstance = new CampaignsApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    CampaignRequest campaignRequest = new CampaignRequest(); // CampaignRequest | 
    try {
      CampaignResponse result = apiInstance.updateCampaign(id, campaignRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CampaignsApi#updateCampaign");
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
| **id** | **UUID**|  | |
| **campaignRequest** | [**CampaignRequest**](CampaignRequest.md)|  | |

### Return type

[**CampaignResponse**](CampaignResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign updated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="updateCampaignSettings"></a>
# **updateCampaignSettings**
> CampaignResponse updateCampaignSettings(id, campaignSettingsRequest)

Update campaign runtime settings

Why this endpoint is needed: Campaign runtime behavior (quota, timeout, restrictions, UI toggles, metadata capture) must be adjustable independently from core campaign identity.  What this endpoint does: It stores runtime policy controls that are enforced when responders submit answers. If closeDate is set to now/past, open responses are auto-locked immediately.  How this endpoint does it: The service loads target campaign by tenant, merges settings payload, persists state, and returns updated campaign context. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CampaignsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CampaignsApi apiInstance = new CampaignsApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    CampaignSettingsRequest campaignSettingsRequest = new CampaignSettingsRequest(); // CampaignSettingsRequest | 
    try {
      CampaignResponse result = apiInstance.updateCampaignSettings(id, campaignSettingsRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CampaignsApi#updateCampaignSettings");
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
| **id** | **UUID**|  | |
| **campaignSettingsRequest** | [**CampaignSettingsRequest**](CampaignSettingsRequest.md)|  | |

### Return type

[**CampaignResponse**](CampaignResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Campaign settings updated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

