# openapi_client.ResponsesApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**compare_segments**](ResponsesApi.md#compare_segments) | **POST** /api/v1/analytics/campaigns/{campaignId}/compare | Compare multiple analytics segments
[**get_campaign_analytics**](ResponsesApi.md#get_campaign_analytics) | **GET** /api/v1/responses/analytics/{campaignId} | Get aggregated analytics for a campaign
[**get_full_report**](ResponsesApi.md#get_full_report) | **GET** /api/v1/analytics/campaigns/{campaignId}/full-report | Get full analytics report with metadata filters
[**get_response**](ResponsesApi.md#get_response) | **GET** /api/v1/responses/{id} | Get one response by id
[**list_responses_by_campaign**](ResponsesApi.md#list_responses_by_campaign) | **GET** /api/v1/responses/campaign/{campaignId} | List responses for a campaign
[**lock_response**](ResponsesApi.md#lock_response) | **POST** /api/v1/responses/{id}/lock | Lock a response manually
[**reopen_response**](ResponsesApi.md#reopen_response) | **POST** /api/v1/responses/{id}/reopen | Reopen a locked response with reason
[**submit_response**](ResponsesApi.md#submit_response) | **POST** /api/v1/responses | Submit survey response (public and private campaign entry)


# **compare_segments**
> ComparisonAnalyticsResponse compare_segments(campaign_id, body)

Compare multiple analytics segments

Why this endpoint is needed:
Platform users need side-by-side comparative analytics of demographic segments.

What this endpoint does:
Multiplexes the /full-report generation across up to 5 concurrent demographic segments.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.comparison_analytics_response import ComparisonAnalyticsResponse
from openapi_client.models.comparison_request import ComparisonRequest
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
    api_instance = openapi_client.ResponsesApi(api_client)
    campaign_id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 
    body = openapi_client.ComparisonRequest() # ComparisonRequest | 

    try:
        # Compare multiple analytics segments
        api_response = api_instance.compare_segments(campaign_id, body)
        print("The response of ResponsesApi->compare_segments:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ResponsesApi->compare_segments: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **campaign_id** | **UUID**|  | 
 **body** | **ComparisonRequest**|  | 

### Return type

[**ComparisonAnalyticsResponse**](ComparisonAnalyticsResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Multi-segment comparison matrix returned |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_campaign_analytics**
> CampaignAnalytics get_campaign_analytics(campaign_id)

Get aggregated analytics for a campaign

Why this endpoint is needed:
Campaign operators need near-real-time aggregate visibility without reading raw response payloads.

What this endpoint does:
It returns aggregate counts and completion rate metrics for a campaign.

How this endpoint does it:
Analytics service runs campaign-scoped aggregate queries under tenant context and returns CampaignAnalytics DTO.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.campaign_analytics import CampaignAnalytics
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
    api_instance = openapi_client.ResponsesApi(api_client)
    campaign_id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Get aggregated analytics for a campaign
        api_response = api_instance.get_campaign_analytics(campaign_id)
        print("The response of ResponsesApi->get_campaign_analytics:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ResponsesApi->get_campaign_analytics: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **campaign_id** | **UUID**|  | 

### Return type

[**CampaignAnalytics**](CampaignAnalytics.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Campaign analytics returned |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_full_report**
> FullCampaignAnalyticsResponse get_full_report(campaign_id)

Get full analytics report with metadata filters

Why this endpoint is needed:
The dashboard needs an aggregated snapshot of campaign performance, score curves, and individual question breakdowns.

What this endpoint does:
Returns all analytics metrics for a single segment (or the entire campaign).


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.full_campaign_analytics_response import FullCampaignAnalyticsResponse
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
    api_instance = openapi_client.ResponsesApi(api_client)
    campaign_id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Get full analytics report with metadata filters
        api_response = api_instance.get_full_report(campaign_id)
        print("The response of ResponsesApi->get_full_report:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ResponsesApi->get_full_report: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **campaign_id** | **UUID**|  | 

### Return type

[**FullCampaignAnalyticsResponse**](FullCampaignAnalyticsResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Full campaign analytics payload returned |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_response**
> SurveyResponseResponse get_response(id)

Get one response by id

Why this endpoint is needed:
Support and operational teams need direct access to a specific response record for verification and exception handling.

What this endpoint does:
It returns one response and its answer details.

How this endpoint does it:
Response service resolves response by id under tenant context and maps nested answer records to DTO.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.survey_response_response import SurveyResponseResponse
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
    api_instance = openapi_client.ResponsesApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Get one response by id
        api_response = api_instance.get_response(id)
        print("The response of ResponsesApi->get_response:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ResponsesApi->get_response: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

### Return type

[**SurveyResponseResponse**](SurveyResponseResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Response returned |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_responses_by_campaign**
> List[SurveyResponseResponse] list_responses_by_campaign(campaign_id)

List responses for a campaign

Why this endpoint is needed:
Campaign managers need a scoped list view of responses for review, quality checks, and manual workflows.

What this endpoint does:
It returns campaign-scoped response list under tenant ownership checks.

How this endpoint does it:
The service validates campaign accessibility under tenant context and fetches mapped response rows by campaign id.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.survey_response_response import SurveyResponseResponse
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
    api_instance = openapi_client.ResponsesApi(api_client)
    campaign_id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # List responses for a campaign
        api_response = api_instance.list_responses_by_campaign(campaign_id)
        print("The response of ResponsesApi->list_responses_by_campaign:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ResponsesApi->list_responses_by_campaign: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **campaign_id** | **UUID**|  | 

### Return type

[**List[SurveyResponseResponse]**](SurveyResponseResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Campaign responses returned |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **lock_response**
> SurveyResponseResponse lock_response(id)

Lock a response manually

Why this endpoint is needed:
Although responses auto-lock on submit, operations teams may need explicit lock controls for exceptional workflows.

What this endpoint does:
It changes response state to locked and records lock timestamp.

How this endpoint does it:
Locking service validates current state and tenant ownership, applies status mutation, and returns updated response.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.survey_response_response import SurveyResponseResponse
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
    api_instance = openapi_client.ResponsesApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Lock a response manually
        api_response = api_instance.lock_response(id)
        print("The response of ResponsesApi->lock_response:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ResponsesApi->lock_response: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

### Return type

[**SurveyResponseResponse**](SurveyResponseResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Response locked |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |
**409** | Requested action conflicts with current resource state |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **reopen_response**
> SurveyResponseResponse reopen_response(id, reopen_request)

Reopen a locked response with reason

Why this endpoint is needed:
Real operations require a controlled exception path for corrections after lock.

What this endpoint does:
It reopens a previously locked response and captures reason/window metadata.

How this endpoint does it:
Locking service validates state transitions and reason constraints, updates response status,
and returns reopened response record. If campaign close date has passed, close-time lock enforcement may lock it again.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.reopen_request import ReopenRequest
from openapi_client.models.survey_response_response import SurveyResponseResponse
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
    api_instance = openapi_client.ResponsesApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 
    reopen_request = openapi_client.ReopenRequest() # ReopenRequest | 

    try:
        # Reopen a locked response with reason
        api_response = api_instance.reopen_response(id, reopen_request)
        print("The response of ResponsesApi->reopen_response:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ResponsesApi->reopen_response: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 
 **reopen_request** | [**ReopenRequest**](ReopenRequest.md)|  | 

### Return type

[**SurveyResponseResponse**](SurveyResponseResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Response reopened |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **submit_response**
> SurveyResponseResponse submit_response(response_submission_request)

Submit survey response (public and private campaign entry)

Why this endpoint is needed:
This is the central data-ingestion endpoint where responder participation is captured.

What this endpoint does:
It accepts answer payload for a campaign, enforces campaign status and runtime controls,
enforces private-auth requirements where applicable, validates against pinned snapshot versions,
auto-calculates weighted score when default profile exists, stores response and answers, then auto-locks response.

How this endpoint does it:
The service performs campaign lookup, access-mode enforcement (public or private), credential validation
(responderToken or responderAccessCode for private), settings checks (quota/restrictions/timeouts), snapshot-based
answer validation/scoring, persistence, and status transition to LOCKED before returning SurveyResponseResponse.


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
    api_instance = openapi_client.ResponsesApi(api_client)
    response_submission_request = openapi_client.ResponseSubmissionRequest() # ResponseSubmissionRequest | 

    try:
        # Submit survey response (public and private campaign entry)
        api_response = api_instance.submit_response(response_submission_request)
        print("The response of ResponsesApi->submit_response:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ResponsesApi->submit_response: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
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
**201** | Response accepted and locked |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |
**429** | Quota or throttling rule prevented execution |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

