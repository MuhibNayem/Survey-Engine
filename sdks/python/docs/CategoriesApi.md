# openapi_client.CategoriesApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create_category**](CategoriesApi.md#create_category) | **POST** /api/v1/categories | Create a category grouping for questions
[**deactivate_category**](CategoriesApi.md#deactivate_category) | **DELETE** /api/v1/categories/{id} | Deactivate a category
[**get_category**](CategoriesApi.md#get_category) | **GET** /api/v1/categories/{id} | Get one category by id
[**list_categories**](CategoriesApi.md#list_categories) | **GET** /api/v1/categories | List active categories
[**update_category**](CategoriesApi.md#update_category) | **PUT** /api/v1/categories/{id} | Update category and mappings


# **create_category**
> CategoryResponse create_category(category_request)

Create a category grouping for questions

Why this endpoint is needed:
Categories group question assets into business dimensions that support structure, analytics, and scoring design.

What this endpoint does:
It creates a category with optional question mappings and stores category metadata under tenant ownership.

How this endpoint does it:
The service validates category payloads, checks referenced question constraints where needed,
persists category + mappings, and returns CategoryResponse with mapping summaries.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.category_request import CategoryRequest
from openapi_client.models.category_response import CategoryResponse
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
    api_instance = openapi_client.CategoriesApi(api_client)
    category_request = openapi_client.CategoryRequest() # CategoryRequest | 

    try:
        # Create a category grouping for questions
        api_response = api_instance.create_category(category_request)
        print("The response of CategoriesApi->create_category:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CategoriesApi->create_category: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **category_request** | [**CategoryRequest**](CategoryRequest.md)|  | 

### Return type

[**CategoryResponse**](CategoryResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**201** | Category created |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deactivate_category**
> deactivate_category(id)

Deactivate a category

Why this endpoint is needed:
Teams need to retire categories without deleting historical campaign and scoring references.

What this endpoint does:
It marks the category as inactive for future authoring while retaining historical records.

How this endpoint does it:
The service enforces tenant ownership, flips active status, and returns no content.


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
    api_instance = openapi_client.CategoriesApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Deactivate a category
        api_instance.deactivate_category(id)
    except Exception as e:
        print("Exception when calling CategoriesApi->deactivate_category: %s\n" % e)
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
**204** | Category deactivated |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_category**
> CategoryResponse get_category(id)

Get one category by id

Why this endpoint is needed:
Category detail pages and editor forms need full mapping visibility.

What this endpoint does:
It returns a single category record including mapped-question metadata.

How this endpoint does it:
The service loads category and mapping state under tenant scope and returns a DTO optimized for UI consumption.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.category_response import CategoryResponse
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
    api_instance = openapi_client.CategoriesApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Get one category by id
        api_response = api_instance.get_category(id)
        print("The response of CategoriesApi->get_category:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CategoriesApi->get_category: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

### Return type

[**CategoryResponse**](CategoryResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Category returned |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_categories**
> List[CategoryResponse] list_categories()

List active categories

Why this endpoint is needed:
Authoring screens need category inventory for survey composition and weighting strategy.

What this endpoint does:
It returns active categories available in the authenticated tenant workspace.

How this endpoint does it:
The category service applies tenant filters and active-state filters, then maps entity results to response DTOs.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.category_response import CategoryResponse
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
    api_instance = openapi_client.CategoriesApi(api_client)

    try:
        # List active categories
        api_response = api_instance.list_categories()
        print("The response of CategoriesApi->list_categories:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CategoriesApi->list_categories: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**List[CategoryResponse]**](CategoryResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Categories returned |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_category**
> CategoryResponse update_category(id, category_request)

Update category and mappings

Why this endpoint is needed:
Category definitions and ordering can change as survey programs mature.

What this endpoint does:
It updates category metadata and mapped-question configuration.

How this endpoint does it:
Validation and tenant checks run first, then mapping set updates are persisted and reflected in the response DTO.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.category_request import CategoryRequest
from openapi_client.models.category_response import CategoryResponse
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
    api_instance = openapi_client.CategoriesApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 
    category_request = openapi_client.CategoryRequest() # CategoryRequest | 

    try:
        # Update category and mappings
        api_response = api_instance.update_category(id, category_request)
        print("The response of CategoriesApi->update_category:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CategoriesApi->update_category: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 
 **category_request** | [**CategoryRequest**](CategoryRequest.md)|  | 

### Return type

[**CategoryResponse**](CategoryResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Category updated |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

