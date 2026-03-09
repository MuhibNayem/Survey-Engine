# openapi_client.QuestionsApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create_question**](QuestionsApi.md#create_question) | **POST** /api/v1/questions | Create a reusable question
[**deactivate_question**](QuestionsApi.md#deactivate_question) | **DELETE** /api/v1/questions/{id} | Deactivate a question
[**get_question**](QuestionsApi.md#get_question) | **GET** /api/v1/questions/{id} | Get one question by id
[**list_questions**](QuestionsApi.md#list_questions) | **GET** /api/v1/questions | List active questions for current tenant
[**update_question**](QuestionsApi.md#update_question) | **PUT** /api/v1/questions/{id} | Update an existing question


# **create_question**
> QuestionResponse create_question(question_request)

Create a reusable question

Why this endpoint is needed:
The question bank is the foundation of reusable survey content. Teams need a way to add standardized question assets
once and reuse them across many surveys.

What this endpoint does:
It creates a question with text, type, and scoring metadata and stores version context used by downstream snapshots.

How this endpoint does it:
The service validates required fields and max score constraints, applies tenant scoping from auth context,
persists the question entity, and returns a QuestionResponse containing metadata and version pointers.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.question_request import QuestionRequest
from openapi_client.models.question_response import QuestionResponse
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
    api_instance = openapi_client.QuestionsApi(api_client)
    question_request = openapi_client.QuestionRequest() # QuestionRequest | 

    try:
        # Create a reusable question
        api_response = api_instance.create_question(question_request)
        print("The response of QuestionsApi->create_question:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling QuestionsApi->create_question: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **question_request** | [**QuestionRequest**](QuestionRequest.md)|  | 

### Return type

[**QuestionResponse**](QuestionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**201** | Question created |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deactivate_question**
> deactivate_question(id)

Deactivate a question

Why this endpoint is needed:
Historical integrity is important, so hard-deletes are avoided while obsolete questions must be removed from active authoring lists.

What this endpoint does:
It marks the target question as inactive.

How this endpoint does it:
The service resolves the question by tenant context, updates active status, and returns no-content on success.


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
    api_instance = openapi_client.QuestionsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Deactivate a question
        api_instance.deactivate_question(id)
    except Exception as e:
        print("Exception when calling QuestionsApi->deactivate_question: %s\n" % e)
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
**204** | Question deactivated |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_question**
> QuestionResponse get_question(id)

Get one question by id

Why this endpoint is needed:
Detailed question retrieval is required for edit forms, review pages, and diagnostics.

What this endpoint does:
It returns one question record by identifier under tenant access control.

How this endpoint does it:
The service resolves the record by ID with tenant-scoped checks and maps the result to a QuestionResponse DTO.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.question_response import QuestionResponse
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
    api_instance = openapi_client.QuestionsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Get one question by id
        api_response = api_instance.get_question(id)
        print("The response of QuestionsApi->get_question:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling QuestionsApi->get_question: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

### Return type

[**QuestionResponse**](QuestionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Question returned |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_questions**
> List[QuestionResponse] list_questions()

List active questions for current tenant

Why this endpoint is needed:
Authoring UIs require fast retrieval of available question inventory during survey design.

What this endpoint does:
It returns active, tenant-scoped question records currently available for composition.

How this endpoint does it:
The question service uses tenant-aware repository filtering and maps entity rows into response DTOs for list rendering.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.question_response import QuestionResponse
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
    api_instance = openapi_client.QuestionsApi(api_client)

    try:
        # List active questions for current tenant
        api_response = api_instance.list_questions()
        print("The response of QuestionsApi->list_questions:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling QuestionsApi->list_questions: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**List[QuestionResponse]**](QuestionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Active questions returned |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_question**
> QuestionResponse update_question(id, question_request)

Update an existing question

Why this endpoint is needed:
Content quality evolves over time and questions require controlled updates.

What this endpoint does:
It updates question fields and maintains version continuity used by snapshot-based references.

How this endpoint does it:
The service validates payload constraints, checks tenant ownership, persists changes, and returns updated question data.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.question_request import QuestionRequest
from openapi_client.models.question_response import QuestionResponse
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
    api_instance = openapi_client.QuestionsApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 
    question_request = openapi_client.QuestionRequest() # QuestionRequest | 

    try:
        # Update an existing question
        api_response = api_instance.update_question(id, question_request)
        print("The response of QuestionsApi->update_question:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling QuestionsApi->update_question: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 
 **question_request** | [**QuestionRequest**](QuestionRequest.md)|  | 

### Return type

[**QuestionResponse**](QuestionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Question updated |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

