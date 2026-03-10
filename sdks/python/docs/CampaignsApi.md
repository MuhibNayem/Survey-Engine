# openapi_client.CampaignsApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**activate_campaign**](CampaignsApi.md#activate_campaign) | **POST** /api/v1/campaigns/{id}/activate | Activate campaign to accept responses
[**create_campaign**](CampaignsApi.md#create_campaign) | **POST** /api/v1/campaigns | Create a campaign from a survey
[**deactivate_campaign**](CampaignsApi.md#deactivate_campaign) | **DELETE** /api/v1/campaigns/{id} | Deactivate campaign
[**generate_campaign_channels**](CampaignsApi.md#generate_campaign_channels) | **POST** /api/v1/campaigns/{id}/distribute | Generate distribution channels for a campaign
[**get_campaign**](CampaignsApi.md#get_campaign) | **GET** /api/v1/campaigns/{id} | Get campaign by id
[**get_campaign_preview**](CampaignsApi.md#get_campaign_preview) | **GET** /api/v1/campaigns/{id}/preview | Get admin preview payload for a campaign
[**get_campaign_settings**](CampaignsApi.md#get_campaign_settings) | **GET** /api/v1/campaigns/{id}/settings | Get campaign runtime settings
[**get_public_campaign_preview**](CampaignsApi.md#get_public_campaign_preview) | **GET** /api/v1/public/campaigns/{id}/preview | Get responder-facing preview payload (public endpoint)
[**get_responder_session_status**](CampaignsApi.md#get_responder_session_status) | **GET** /api/v1/public/campaigns/{id}/auth/session | Get current private responder session status for a campaign
[**list_campaign_channels**](CampaignsApi.md#list_campaign_channels) | **GET** /api/v1/campaigns/{id}/channels | List generated channels for a campaign
[**list_campaigns**](CampaignsApi.md#list_campaigns) | **GET** /api/v1/campaigns | List active campaigns
[**load_public_draft**](CampaignsApi.md#load_public_draft) | **POST** /api/v1/public/campaigns/{id}/responses/draft/load | Load an existing in-progress responder draft
[**logout_responder_session**](CampaignsApi.md#logout_responder_session) | **POST** /api/v1/public/campaigns/{id}/auth/logout | Revoke the current private responder session for a campaign
[**save_public_draft**](CampaignsApi.md#save_public_draft) | **POST** /api/v1/public/campaigns/{id}/responses/draft | Create or update an in-progress responder draft
[**update_campaign**](CampaignsApi.md#update_campaign) | **PUT** /api/v1/campaigns/{id} | Update campaign metadata
[**update_campaign_settings**](CampaignsApi.md#update_campaign_settings) | **PUT** /api/v1/campaigns/{id}/settings | Update campaign runtime settings


# **activate_campaign**
> CampaignResponse activate_campaign(id)

Activate campaign to accept responses

Why this endpoint is needed:
Activation is a controlled launch gate. It prevents collecting responses from improperly prepared campaigns.

What this endpoint does:
It transitions a campaign into active state when preconditions pass, links latest survey snapshot,
and upserts campaign default weight profile from pinned category weights.

How this endpoint does it:
The service verifies linked survey lifecycle requirements (must be PUBLISHED), loads latest snapshot,
upserts default weight profile, updates campaign status to ACTIVE, and returns campaign response.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.campaign_response import CampaignResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.CampaignsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Activate campaign to accept responses
        api_response = api_instance.activate_campaign(id)
        print("The response of CampaignsApi->activate_campaign:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CampaignsApi->activate_campaign: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

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
**200** | Campaign activated |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |
**409** | Requested action conflicts with current resource state |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_campaign**
> CampaignResponse create_campaign(campaign_request)

Create a campaign from a survey

Why this endpoint is needed:
Campaigns are the executable delivery unit for collecting responses from a specific survey snapshot and runtime policy set.

What this endpoint does:
It creates a tenant-scoped campaign with name, survey binding, description, and access mode (PUBLIC/PRIVATE).

How this endpoint does it:
The service validates survey reference and tenant scope, normalizes deprecated auth mode values, persists campaign state,
and returns CampaignResponse.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.campaign_request import CampaignRequest
from openapi_client.models.campaign_response import CampaignResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.CampaignsApi(api_client)
    campaign_request = openapi_client.CampaignRequest() # CampaignRequest | 

    try:
        # Create a campaign from a survey
        api_response = api_instance.create_campaign(campaign_request)
        print("The response of CampaignsApi->create_campaign:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CampaignsApi->create_campaign: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **campaign_request** | [**CampaignRequest**](CampaignRequest.md)|  | 

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
**201** | Campaign created |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deactivate_campaign**
> deactivate_campaign(id)

Deactivate campaign

Why this endpoint is needed:
Retiring campaign availability should not erase historical response evidence.

What this endpoint does:
It marks campaign inactive.

How this endpoint does it:
Tenant-scoped campaign lookup is followed by status/active-state mutation and no-content completion.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.CampaignsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Deactivate campaign
        api_instance.deactivate_campaign(id)
    except Exception as e:
        print("Exception when calling CampaignsApi->deactivate_campaign: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**204** | Campaign deactivated |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **generate_campaign_channels**
> List[DistributionChannelResponse] generate_campaign_channels(id)

Generate distribution channels for a campaign

Why this endpoint is needed:
Operations teams need standardized distribution artifacts (links and embed formats) to launch outreach quickly.

What this endpoint does:
It generates and stores channel records such as public/private links and embed snippets.

How this endpoint does it:
Distribution service builds channel values from campaign context, persists channel rows, and returns generated list.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.distribution_channel_response import DistributionChannelResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.CampaignsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Generate distribution channels for a campaign
        api_response = api_instance.generate_campaign_channels(id)
        print("The response of CampaignsApi->generate_campaign_channels:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CampaignsApi->generate_campaign_channels: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

### Return type

[**List[DistributionChannelResponse]**](DistributionChannelResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**201** | Channels generated |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_campaign**
> CampaignResponse get_campaign(id)

Get campaign by id

Why this endpoint is needed:
Campaign detail retrieval is required for settings updates, monitoring, and support troubleshooting.

What this endpoint does:
It returns one campaign record with current status and metadata.

How this endpoint does it:
The service resolves campaign by tenant and id and returns mapped DTO fields.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.campaign_response import CampaignResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.CampaignsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Get campaign by id
        api_response = api_instance.get_campaign(id)
        print("The response of CampaignsApi->get_campaign:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CampaignsApi->get_campaign: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

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
**200** | Campaign returned |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_campaign_preview**
> CampaignPreviewResponse get_campaign_preview(id)

Get admin preview payload for a campaign

Why this endpoint is needed:
Campaign owners need a pre-launch visual/data validation endpoint to verify header/footer/questions and responder UX flags.

What this endpoint does:
It assembles campaign, survey, and settings into a responder-facing preview model for admin users.

How this endpoint does it:
Campaign service resolves campaign/settings/survey in tenant scope, maps each page/question with pinned version context,
and returns CampaignPreviewResponse.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.campaign_preview_response import CampaignPreviewResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.CampaignsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Get admin preview payload for a campaign
        api_response = api_instance.get_campaign_preview(id)
        print("The response of CampaignsApi->get_campaign_preview:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CampaignsApi->get_campaign_preview: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

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
**200** | Campaign preview returned |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_campaign_settings**
> CampaignSettingsResponse get_campaign_settings(id)

Get campaign runtime settings

Why this endpoint is needed:
Operations and UI flows require a read endpoint for persisted runtime controls before edit/save cycles.

What this endpoint does:
It returns the current campaign settings object.

How this endpoint does it:
Campaign service validates tenant-scoped campaign visibility and returns mapped CampaignSettingsResponse.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.campaign_settings_response import CampaignSettingsResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.CampaignsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Get campaign runtime settings
        api_response = api_instance.get_campaign_settings(id)
        print("The response of CampaignsApi->get_campaign_settings:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CampaignsApi->get_campaign_settings: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

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
**200** | Campaign settings returned |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_public_campaign_preview**
> CampaignPreviewResponse get_public_campaign_preview(id)

Get responder-facing preview payload (public endpoint)

Why this endpoint is needed:
Responder runtime needs a non-admin endpoint to load campaign form structure for public/private submission flows.

What this endpoint does:
It returns preview payload for campaigns currently active.

How this endpoint does it:
Service resolves campaign/settings/survey and returns CampaignPreviewResponse when campaign is active; access
mode enforcement for private campaigns happens on submit/auth endpoints.


### Example


```python
import openapi_client
from openapi_client.models.campaign_preview_response import CampaignPreviewResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)


# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.CampaignsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Get responder-facing preview payload (public endpoint)
        api_response = api_instance.get_public_campaign_preview(id)
        print("The response of CampaignsApi->get_public_campaign_preview:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CampaignsApi->get_public_campaign_preview: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

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
**200** | Public preview returned |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_responder_session_status**
> ResponderSessionStatusResponse get_responder_session_status(id)

Get current private responder session status for a campaign

Why this endpoint is needed:
Private responder runtime needs a non-destructive way to detect whether an authenticated responder session already exists after SSO redirect or page refresh.

What this endpoint does:
It returns whether a valid responder session cookie is currently active for the target private campaign.

How this endpoint does it:
The controller resolves the campaign, validates that it is private, reads the responder session cookie, and returns a simple authenticated/email payload.


### Example


```python
import openapi_client
from openapi_client.models.responder_session_status_response import ResponderSessionStatusResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)


# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.CampaignsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Get current private responder session status for a campaign
        api_response = api_instance.get_responder_session_status(id)
        print("The response of CampaignsApi->get_responder_session_status:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CampaignsApi->get_responder_session_status: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

### Return type

[**ResponderSessionStatusResponse**](ResponderSessionStatusResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Responder session status returned |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_campaign_channels**
> List[DistributionChannelResponse] list_campaign_channels(id)

List generated channels for a campaign

Why this endpoint is needed:
Teams often need to re-fetch channel assets after initial generation for reminders and omnichannel delivery.

What this endpoint does:
It returns all persisted distribution channels for the campaign.

How this endpoint does it:
Distribution service reads campaign channel rows under tenant scope and maps to response DTOs.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.distribution_channel_response import DistributionChannelResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.CampaignsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # List generated channels for a campaign
        api_response = api_instance.list_campaign_channels(id)
        print("The response of CampaignsApi->list_campaign_channels:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CampaignsApi->list_campaign_channels: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

### Return type

[**List[DistributionChannelResponse]**](DistributionChannelResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Channel list returned |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_campaigns**
> List[CampaignResponse] list_campaigns()

List active campaigns

Why this endpoint is needed:
Campaign operations teams need a dashboard view of active and manageable campaigns.

What this endpoint does:
It returns campaigns under authenticated tenant scope.

How this endpoint does it:
Campaign service executes tenant-scoped retrieval and response mapping.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.campaign_response import CampaignResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.CampaignsApi(api_client)

    try:
        # List active campaigns
        api_response = api_instance.list_campaigns()
        print("The response of CampaignsApi->list_campaigns:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CampaignsApi->list_campaigns: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**List[CampaignResponse]**](CampaignResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Campaign list returned |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **load_public_draft**
> SurveyResponseResponse load_public_draft(id, response_draft_lookup_request)

Load an existing in-progress responder draft

Why this endpoint is needed:
Responder runtime must restore saved survey state after refresh, return visit, or successful private SSO authentication.

What this endpoint does:
It returns the matching in-progress draft when one exists for the supplied identity or response id.

How this endpoint does it:
The service looks up the latest open response by explicit response id or responder identity, validates access mode, and returns 204 when no draft exists.


### Example


```python
import openapi_client
from openapi_client.models.response_draft_lookup_request import ResponseDraftLookupRequest
from openapi_client.models.survey_response_response import SurveyResponseResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)


# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.CampaignsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 
    response_draft_lookup_request = openapi_client.ResponseDraftLookupRequest() # ResponseDraftLookupRequest | 

    try:
        # Load an existing in-progress responder draft
        api_response = api_instance.load_public_draft(id, response_draft_lookup_request)
        print("The response of CampaignsApi->load_public_draft:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CampaignsApi->load_public_draft: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 
 **response_draft_lookup_request** | [**ResponseDraftLookupRequest**](ResponseDraftLookupRequest.md)|  | 

### Return type

[**SurveyResponseResponse**](SurveyResponseResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Draft restored |  -  |
**204** | No draft exists for the supplied identity |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **logout_responder_session**
> logout_responder_session(id)

Revoke the current private responder session for a campaign

Why this endpoint is needed:
Private survey sessions require explicit sign-out on shared or managed devices without relying only on expiry.

What this endpoint does:
It revokes the current responder session and clears the responder session cookie.

How this endpoint does it:
The controller resolves the campaign, revokes the matching responder session for the current cookie, and returns a no-content response with a clearing cookie.


### Example


```python
import openapi_client
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)


# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.CampaignsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Revoke the current private responder session for a campaign
        api_instance.logout_responder_session(id)
    except Exception as e:
        print("Exception when calling CampaignsApi->logout_responder_session: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**204** | Responder session revoked and cookie cleared |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **save_public_draft**
> SurveyResponseResponse save_public_draft(id, response_submission_request)

Create or update an in-progress responder draft

Why this endpoint is needed:
Responders need an interruption-safe draft path so multi-page surveys can be resumed later without losing answers or metadata.

What this endpoint does:
It creates a new IN_PROGRESS response or updates an existing draft response for the target campaign.

How this endpoint does it:
The service validates campaign access rules, upserts the response row, merges answers by question, persists respondent metadata, and returns the current draft state.


### Example


```python
import openapi_client
from openapi_client.models.response_submission_request import ResponseSubmissionRequest
from openapi_client.models.survey_response_response import SurveyResponseResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)


# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.CampaignsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 
    response_submission_request = openapi_client.ResponseSubmissionRequest() # ResponseSubmissionRequest | 

    try:
        # Create or update an in-progress responder draft
        api_response = api_instance.save_public_draft(id, response_submission_request)
        print("The response of CampaignsApi->save_public_draft:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CampaignsApi->save_public_draft: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 
 **response_submission_request** | [**ResponseSubmissionRequest**](ResponseSubmissionRequest.md)|  | 

### Return type

[**SurveyResponseResponse**](SurveyResponseResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Draft saved |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_campaign**
> CampaignResponse update_campaign(id, campaign_request)

Update campaign metadata

Why this endpoint is needed:
Campaigns require controlled edits to operational metadata and associations.

What this endpoint does:
It updates campaign request fields while preserving lifecycle constraints.

How this endpoint does it:
Service validates ownership and allowed update semantics, then persists and returns updated campaign data.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.campaign_request import CampaignRequest
from openapi_client.models.campaign_response import CampaignResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.CampaignsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 
    campaign_request = openapi_client.CampaignRequest() # CampaignRequest | 

    try:
        # Update campaign metadata
        api_response = api_instance.update_campaign(id, campaign_request)
        print("The response of CampaignsApi->update_campaign:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CampaignsApi->update_campaign: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 
 **campaign_request** | [**CampaignRequest**](CampaignRequest.md)|  | 

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
**200** | Campaign updated |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_campaign_settings**
> CampaignResponse update_campaign_settings(id, campaign_settings_request)

Update campaign runtime settings

Why this endpoint is needed:
Campaign runtime behavior (quota, timeout, restrictions, UI toggles, metadata capture) must be adjustable
independently from core campaign identity.

What this endpoint does:
It stores runtime policy controls that are enforced when responders submit answers.
If closeDate is set to now/past, open responses are auto-locked immediately.

How this endpoint does it:
The service loads target campaign by tenant, merges settings payload, persists state,
and returns updated campaign context.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.campaign_response import CampaignResponse
from openapi_client.models.campaign_settings_request import CampaignSettingsRequest
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.CampaignsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 
    campaign_settings_request = openapi_client.CampaignSettingsRequest() # CampaignSettingsRequest | 

    try:
        # Update campaign runtime settings
        api_response = api_instance.update_campaign_settings(id, campaign_settings_request)
        print("The response of CampaignsApi->update_campaign_settings:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CampaignsApi->update_campaign_settings: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 
 **campaign_settings_request** | [**CampaignSettingsRequest**](CampaignSettingsRequest.md)|  | 

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
**200** | Campaign settings updated |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

