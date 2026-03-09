# openapi_client.SurveysApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create_survey**](SurveysApi.md#create_survey) | **POST** /api/v1/surveys | Create a survey draft
[**deactivate_survey**](SurveysApi.md#deactivate_survey) | **DELETE** /api/v1/surveys/{id} | Deactivate a survey
[**get_survey**](SurveysApi.md#get_survey) | **GET** /api/v1/surveys/{id} | Get one survey by id
[**list_surveys**](SurveysApi.md#list_surveys) | **GET** /api/v1/surveys | List active surveys
[**transition_survey_lifecycle**](SurveysApi.md#transition_survey_lifecycle) | **POST** /api/v1/surveys/{id}/lifecycle | Transition survey lifecycle state
[**update_survey**](SurveysApi.md#update_survey) | **PUT** /api/v1/surveys/{id} | Update survey draft content


# **create_survey**
> SurveyResponse create_survey(survey_request)

Create a survey draft

Why this endpoint is needed:
Survey lifecycle begins with a draft container where structure can be assembled before publication.

What this endpoint does:
It creates a tenant-scoped survey draft with title, description, and page/question structure.

How this endpoint does it:
The service validates schema, persists draft state, and returns the survey representation including lifecycle status.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.survey_request import SurveyRequest
from openapi_client.models.survey_response import SurveyResponse
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
    api_instance = openapi_client.SurveysApi(api_client)
    survey_request = openapi_client.SurveyRequest() # SurveyRequest | 

    try:
        # Create a survey draft
        api_response = api_instance.create_survey(survey_request)
        print("The response of SurveysApi->create_survey:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SurveysApi->create_survey: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **survey_request** | [**SurveyRequest**](SurveyRequest.md)|  | 

### Return type

[**SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**201** | Survey draft created |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deactivate_survey**
> deactivate_survey(id)

Deactivate a survey

Why this endpoint is needed:
Organizations need to retire obsolete surveys without physically deleting historical references.

What this endpoint does:
It marks the survey inactive.

How this endpoint does it:
Tenant ownership is validated and active flag is disabled; endpoint returns no-content on success.


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
    api_instance = openapi_client.SurveysApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Deactivate a survey
        api_instance.deactivate_survey(id)
    except Exception as e:
        print("Exception when calling SurveysApi->deactivate_survey: %s\n" % e)
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
**204** | Survey deactivated |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_survey**
> SurveyResponse get_survey(id)

Get one survey by id

Why this endpoint is needed:
Teams need detailed survey context for editing, publishing decisions, and audits.

What this endpoint does:
It returns one survey including pages/questions and lifecycle metadata.

How this endpoint does it:
The service resolves survey by id and tenant, then maps nested structures into SurveyResponse.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.survey_response import SurveyResponse
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
    api_instance = openapi_client.SurveysApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Get one survey by id
        api_response = api_instance.get_survey(id)
        print("The response of SurveysApi->get_survey:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SurveysApi->get_survey: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

### Return type

[**SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Survey returned |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_surveys**
> List[SurveyResponse] list_surveys()

List active surveys

Why this endpoint is needed:
Admin dashboards require an overview of reusable survey assets and lifecycle states.

What this endpoint does:
It returns active surveys under current tenant scope.

How this endpoint does it:
Survey repository filters by tenant and active state, then maps records to response DTOs.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.survey_response import SurveyResponse
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
    api_instance = openapi_client.SurveysApi(api_client)

    try:
        # List active surveys
        api_response = api_instance.list_surveys()
        print("The response of SurveysApi->list_surveys:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SurveysApi->list_surveys: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**List[SurveyResponse]**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Surveys returned |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **transition_survey_lifecycle**
> SurveyResponse transition_survey_lifecycle(id, lifecycle_transition_request)

Transition survey lifecycle state

Why this endpoint is needed:
Lifecycle transitions are a governance gate that prevents unsafe operations (for example activating campaigns from unpublished surveys).

What this endpoint does:
It applies a requested lifecycle transition (such as Draft->Published or Closed->Published with reason).

How this endpoint does it:
The service checks allowed transition matrix, validates transition requirements, updates lifecycle state,
and returns the updated survey representation.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.lifecycle_transition_request import LifecycleTransitionRequest
from openapi_client.models.survey_response import SurveyResponse
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
    api_instance = openapi_client.SurveysApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 
    lifecycle_transition_request = openapi_client.LifecycleTransitionRequest() # LifecycleTransitionRequest | 

    try:
        # Transition survey lifecycle state
        api_response = api_instance.transition_survey_lifecycle(id, lifecycle_transition_request)
        print("The response of SurveysApi->transition_survey_lifecycle:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SurveysApi->transition_survey_lifecycle: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 
 **lifecycle_transition_request** | [**LifecycleTransitionRequest**](LifecycleTransitionRequest.md)|  | 

### Return type

[**SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Lifecycle transition applied |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |
**409** | Requested action conflicts with current resource state |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_survey**
> SurveyResponse update_survey(id, survey_request)

Update survey draft content

Why this endpoint is needed:
Draft surveys evolve before publication and require iterative edits.

What this endpoint does:
It updates survey details and structure when lifecycle rules allow modification.

How this endpoint does it:
The service validates immutability/lifecycle constraints, persists updates, and returns the updated survey.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.survey_request import SurveyRequest
from openapi_client.models.survey_response import SurveyResponse
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
    api_instance = openapi_client.SurveysApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 
    survey_request = openapi_client.SurveyRequest() # SurveyRequest | 

    try:
        # Update survey draft content
        api_response = api_instance.update_survey(id, survey_request)
        print("The response of SurveysApi->update_survey:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SurveysApi->update_survey: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 
 **survey_request** | [**SurveyRequest**](SurveyRequest.md)|  | 

### Return type

[**SurveyResponse**](SurveyResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Survey updated |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |
**409** | Requested action conflicts with current resource state |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

